package ru.curs.showcase.app.server;

import java.io.*;
import java.lang.reflect.Modifier;

import org.slf4j.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.chart.*;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.model.geomap.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.model.navigator.*;
import ru.curs.showcase.model.webtext.*;
import ru.curs.showcase.model.xform.*;
import ru.curs.showcase.util.*;

import com.google.gson.*;

/**
 * Реализация функций сервисного слоя приложения не зависимая от GWT Servlet.
 * Позволяет вызывать функции сервисного слоя не из GWT кода.
 * 
 * @author den
 * 
 */
public final class ServiceLayerDataServiceImpl implements DataService, DataServiceExt {

	@SuppressWarnings("unused")
	private ServiceLayerDataServiceImpl() {
		throw new UnsupportedOperationException(
				"Запрещено создавать ServiceLayerDataServiceImpl не передавая ему SessionId");
	}

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceLayerDataServiceImpl.class);

	static final String JSON_MAP_DATA = "Сформирован JSON с данными карты: ";
	static final String JSON_MAP_TEMPLATE = "Получен JSON с шаблоном карты: ";

	/**
	 * Идентификатор текущей HTTP сессии.
	 */
	private String sessionId = null;

	public ServiceLayerDataServiceImpl(final String aSessionId) {
		super();
		sessionId = aSessionId;
	}

	@Override
	public Navigator getNavigator() throws GeneralServerException {
		InputStream xml;
		getServerCurrentState();
		Navigator nav = null;

		try {
			NavigatorGateway gw = new NavigatorDBGateway();
			CompositeContext context = new CompositeContext();
			prepareContext(context);
			try {
				xml = gw.getData(context);
				NavigatorFactory factory = new NavigatorFactory();
				nav = factory.fromStream(xml);
			} finally {
				gw.releaseResources();
			}
			outputDebugInfo(nav);
			return nav;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	private SolutionMessage getSolutionMessage(final Throwable exc) {
		if (exc.getClass() == SolutionDBException.class) {
			return ((SolutionDBException) exc).getSolutionMessage();
		}
		return null;
	}

	@Override
	public DataPanel getDataPanel(final Action action) throws GeneralServerException {
		DataPanel panel = null;

		try {
			DataPanelGateway gateway = new DataPanelXMLGateway();
			DataFile<InputStream> file =
				gateway.getXML(action.getDataPanelLink().getDataPanelId());
			DataPanelFactory factory = new DataPanelFactory();
			panel = factory.fromStream(file);
			outputDebugInfo(panel);
			return panel;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	@Override
	public WebText getWebText(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralServerException {
		try {
			WebTextGateway wtgateway = new WebTextDBGateway();
			prepareContext(context);
			HTMLBasedElementRawData rawWT = wtgateway.getRawData(context, element);
			WebTextDBFactory builder = new WebTextDBFactory(rawWT);
			WebText webtext = builder.build();
			outputDebugInfo(webtext);
			return webtext;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	@Override
	public Grid getGrid(final CompositeContext context, final DataPanelElementInfo element,
			final GridRequestedSettings aSettings) throws GeneralServerException {
		try {
			GridGateway gateway = new GridDBGateway();
			prepareContext(context);
			ElementRawData raw = gateway.getFactorySource(context, element, aSettings);
			GridDBFactory factory = new GridDBFactory(raw, aSettings);
			Grid grid = factory.build();
			outputDebugInfo(grid);
			return grid;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	private void outputDebugInfo(final Object obj) {
		if (LOGGER.isDebugEnabled()) {
			ExclusionStrategy es = new ExclusionStrategy() {
				@Override
				public boolean shouldSkipClass(final Class<?> aClass) {
					return false;
				}

				@Override
				public boolean shouldSkipField(final FieldAttributes fa) {
					return (fa.getAnnotation(ExcludeFromSerialization.class) != null);
				}
			};
			Gson gson =
				new GsonBuilder().setPrettyPrinting().setExclusionStrategies(es).serializeNulls()
						.excludeFieldsWithModifiers(Modifier.TRANSIENT + Modifier.STATIC).create();
			LOGGER.debug(gson.toJson(obj));
		}
	}

	@Override
	public Chart getChart(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralServerException {
		try {
			ChartGateway gateway = new ChartDBGateway();
			prepareContext(context);
			ElementRawData raw = gateway.getFactorySource(context, element);
			ChartDBFactory factory = new ChartDBFactory(raw);
			Chart chart = factory.build();
			AdapterForJS adapter = new AdapterForJS();
			adapter.adapt(chart);
			outputDebugInfo(chart);
			return chart;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	private String getOriginalMessage(final Throwable original) {
		if (original instanceof AbstractShowcaseThrowable) {
			return ((AbstractShowcaseThrowable) original).getOriginalMessage();
		}
		return null;
	}

	@Override
	public ExcelFile generateExcelFromGrid(final GridToExcelExportType exportType,
			final CompositeContext context, final DataPanelElementInfo element,
			final GridRequestedSettings settings, final ColumnSet cs)
			throws GeneralServerException {
		ByteArrayOutputStream result = null;
		Grid grid = null;
		try {
			if (exportType == GridToExcelExportType.ALL) {
				settings.resetForReturnAllRecords();
			}
			settings.setApplyLocalFormatting(false);
			prepareContext(context);
			grid = getGrid(context, element, settings);
			GridXMLBuilder builder = new GridXMLBuilder(grid);
			Document xml = builder.build(cs);
			result = XMLUtils.xsltTransformForGrid(xml);
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
		return new ExcelFile(result);
	}

	@Override
	public void saveColumnSet(final ColumnSet aCs) throws GeneralServerException {
	}

	@Override
	public GeoMap getGeoMap(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralServerException {
		try {
			GeoMapGateway gateway = new GeoMapDBGateway();
			prepareContext(context);
			ElementRawData raw = gateway.getFactorySource(context, element);
			GeoMapDBFactory factory = new GeoMapDBFactory(raw);
			GeoMap map = factory.build();
			outputDebugInfo(map);
			AdapterForJS adapter = new AdapterForJS();
			adapter.adapt(map);
			return map;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	@Override
	public XForms getXForms(final CompositeContext context, final DataPanelElementInfo element,
			final String currentData) throws GeneralServerException {
		try {
			XFormsGateway gateway = new XFormsDBGateway();
			prepareContext(context);
			HTMLBasedElementRawData raw = gateway.getInitialData(context, element);
			if (currentData != null) {
				raw.setData(currentData);
			}
			XFormsDBFactory factory = new XFormsDBFactory(raw);
			XForms xforms = factory.build();
			outputDebugInfo(xforms);
			return xforms;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	@Override
	public CommandResult saveXForms(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final String data)
			throws GeneralServerException {
		try {
			prepareContext(context);
			LOGGER.debug("Идет сохранение данных XForms: " + data);

			UserXMLTransformer transformer =
				new UserXMLTransformer(data, elementInfo.getSaveProc());
			transformer.checkAndTransform();
			XFormsGateway gateway = new XFormsDBGateway();
			CommandResult res =
				gateway.saveData(context, elementInfo, transformer.getStringResult());

			outputDebugInfo(res);
			return res;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	private void prepareContext(final CompositeContext context) {
		context.setSession(AppInfoSingleton.getSessionContext(sessionId));
	}

	@Override
	public RequestResult handleSQLSubmission(final String procName, final String content)
			throws GeneralServerException {
		try {
			XFormsGateway gateway = new XFormsDBGateway();
			RequestResult res = gateway.handleSubmission(procName, content);
			if (res.getSuccess()) {
				LOGGER.debug("Submission успешно выполнен: " + res.getData());
			} else {
				LOGGER.debug("Submission вернул ошибку: " + res.generateStandartErrorMessage());
			}
			return res;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	@Override
	public String handleXSLTSubmission(final String xsltFile, final String content)
			throws GeneralServerException {
		try {
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(content));
			Document doc = XMLUtils.createBuilder().parse(is);
			String res = XMLUtils.xsltTransform(doc, xsltFile);
			LOGGER.debug("XFormsTransformationServlet успешно выполнен: " + res);
			return res;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	@Override
	public ServerCurrentState getServerCurrentState() throws GeneralServerException {
		try {
			ServerCurrentState res = ServerCurrentStateBuilder.build(sessionId);
			LOGGER.debug(res.toString());
			return res;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	@Override
	public DataFile<ByteArrayOutputStream> getDownloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final String linkId, final String data)
			throws GeneralServerException {
		try {
			LOGGER.debug("Данные формы при выгрузке файла:" + data);
			prepareContext(context);

			XFormsGateway gateway = new XFormsDBGateway();
			DataFile<ByteArrayOutputStream> file =
				gateway.downloadFile(context, elementInfo, linkId, data);

			UserXMLTransformer transformer =
				new UserXMLTransformer(file, elementInfo.getProcs().get(linkId));
			transformer.checkAndTransform();
			file = transformer.getOutputStreamResult();

			LOGGER.debug(String
					.format("Размер скачиваемого файла: %d байт", file.getData().size()));
			return file;
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}

	@Override
	public void uploadFile(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String linkId, final String data, final DataFile<ByteArrayOutputStream> file)
			throws GeneralServerException {
		try {
			LOGGER.debug("Данные формы при загрузке файла:" + data);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Получен файл '" + file.getName() + "' размером "
						+ file.getData().size() + " байт");
			}
			prepareContext(context);

			UserXMLTransformer transformer =
				new UserXMLTransformer(file, elementInfo.getProcs().get(linkId));
			transformer.checkAndTransform();
			XFormsGateway gateway = new XFormsDBGateway();
			gateway.uploadFile(context, elementInfo, linkId, data,
					transformer.getInputStreamResult());
		} catch (Throwable e) {
			throw new GeneralServerException(e, getOriginalMessage(e), getSolutionMessage(e));
		}
	}
}
