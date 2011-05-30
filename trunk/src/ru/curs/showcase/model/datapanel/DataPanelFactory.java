package ru.curs.showcase.model.datapanel;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Фабрика для создания информационных панелей.
 * 
 * @author den
 * 
 */
public final class DataPanelFactory extends GeneralXMLHelper {
	public static final String DATAPANEL_XSD = "datapanel.xsd";
	static final String NEVER_SHOW_IN_PANEL_TAG = "neverShowInPanel";
	static final String PROC_TAG = "proc";
	static final String SAVE_PROC_TAG = "saveProc";
	static final String REFRESH_INTERVAL_TAG = "refreshInterval";
	static final String REFRESH_MODE_TAG = "refreshMode";
	/**
	 * Создаваемая панель.
	 */
	private DataPanel result;
	/**
	 * Обработчик для SAX парсера.
	 */
	private final DefaultHandler myHandler;

	/**
	 * Файл с исходными данными.
	 */
	private DataFile<InputStream> file;

	/**
	 * Текущая вкладка.
	 */
	private DataPanelTab currentTab = null;

	public DataPanelFactory() {
		super();

		myHandler = new DefaultHandler() {
			private int elCounter = 0;

			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				String value;
				if (qname.equalsIgnoreCase(DP_TAG)) {
					result = new DataPanel(file.getId());
					if (attrs.getIndex(REFRESH_MODE_TAG) > -1) {
						value = attrs.getValue(REFRESH_MODE_TAG);
						result.setRefreshMode(DataPanelRefreshMode.valueOf(value));
					}
					if (attrs.getIndex(REFRESH_INTERVAL_TAG) > -1) {
						value = attrs.getValue(REFRESH_INTERVAL_TAG);
						result.setRefreshInterval(Integer.valueOf(value));
					}
					return;
				}
				if (qname.equalsIgnoreCase(TAB_TAG)) {
					currentTab = result.add(attrs.getValue(ID_TAG), attrs.getValue(NAME_TAG));
					return;
				}
				if (qname.equalsIgnoreCase(ELEMENT_TAG)) {
					DataPanelElementInfo el = new DataPanelElementInfo(elCounter++, currentTab);
					el.setId(attrs.getValue(ID_TAG));
					el.setType(DataPanelElementType
							.valueOf(attrs.getValue(TYPE_TAG).toUpperCase()));
					if (attrs.getIndex(STYLE_CLASS_TAG) > -1) {
						el.setStyleClass(attrs.getValue(STYLE_CLASS_TAG));
					}
					if (attrs.getIndex(PROC_ATTR_NAME) > -1) {
						el.setProcName(attrs.getValue(PROC_ATTR_NAME));
					}
					if (el.getType() == DataPanelElementType.WEBTEXT) {
						el.setTransformName(attrs.getValue(TRANSFORM_ATTR_NAME));
					}
					if (el.getType() == DataPanelElementType.XFORMS) {
						el.setTemplateName(attrs.getValue(TEMPLATE_TAG));
					}
					if (attrs.getIndex(HIDE_ON_LOAD_TAG) > -1) {
						value = attrs.getValue(HIDE_ON_LOAD_TAG);
						el.setHideOnLoad(Boolean.valueOf(value));
					}
					if (attrs.getIndex(NEVER_SHOW_IN_PANEL_TAG) > -1) {
						value = attrs.getValue(NEVER_SHOW_IN_PANEL_TAG);
						el.setNeverShowInPanel(Boolean.valueOf(value));
					}

					currentTab.getElements().add(el);
					return;
				}
				if (qname.equalsIgnoreCase(PROC_TAG)) {
					DataPanelElementProc proc = new DataPanelElementProc();
					proc.setId(attrs.getValue(ID_TAG));
					proc.setName(attrs.getValue(NAME_TAG));
					proc.setType(DataPanelElementProcType.valueOf(attrs.getValue(TYPE_TAG)));
					if (attrs.getIndex(TRANSFORM_ATTR_NAME) > -1) {
						proc.setTransformName(attrs.getValue(TRANSFORM_ATTR_NAME));
					}
					if (attrs.getIndex(SCHEMA_TAG) > -1) {
						proc.setSchemaName(attrs.getValue(SCHEMA_TAG));
					}
					currentTab.getElements().get(currentTab.getElements().size() - 1).getProcs()
							.put(proc.getId(), proc);
				}
			}
		};
	}

	/**
	 * Функция построения панели из XML файла.
	 * 
	 * @param aFile
	 *            - файл с панелью.
	 * @return - информационная панель.
	 */
	public DataPanel fromStream(final DataFile<InputStream> aFile) {
		file = aFile;
		XMLUtils.validateXMLStream(file, DATAPANEL_XSD);

		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(file.getData(), myHandler);
		} catch (Throwable e) {
			XMLUtils.stdSAXErrorHandler(e, file.getName());
		}
		return result;
	}
}
