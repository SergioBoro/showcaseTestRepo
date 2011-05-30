package ru.curs.showcase.model;

import java.io.InputStream;
import java.sql.*;

import org.slf4j.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.util.*;

/**
 * Абстрактный класс, содержащий базовые константы и функции для вызова хранимых
 * процедур с данными.
 * 
 * @author den
 * 
 */
public abstract class SPCallHelper extends DataCheckGateway {
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SPCallHelper.class);

	/**
	 * Соединение с БД.
	 */
	private Connection db = null;

	/**
	 * Интерфейс вызова хранимых процедур JDBC.
	 */
	private CallableStatement cs = null;

	/**
	 * Контекст вызова хранимой процедуры.
	 */
	private CompositeContext context = null;

	/**
	 * Информация об элементе, данные которого загружает процедура.
	 */
	private DataPanelElementInfo elementInfo = null;

	public SPCallHelper() {
		super();
	}

	public static final String NO_RESULTSET_ERROR = "хранимая процедура не возвратила данные";
	protected static final String FILTER_COLUMNNAME = "filterinfo";
	protected static final String ELEMENTID_COLUMNNAME = "element_id";
	protected static final String SESSION_CONTEXT_PARAM = "session_context";

	/**
	 * Функция для настройки общих параметров запроса: контекста и фильтров.
	 * 
	 * @throws SQLException
	 */
	protected void setupGeneralParameters() throws SQLException {
		cs.setString(MAIN_CONTEXT_ATTR_NAME, "");
		cs.setString(ADD_CONTEXT_ATTR_NAME, "");
		cs.setString(SESSION_CONTEXT_PARAM, "");
		cs.setString(FILTER_COLUMNNAME, "");
		if (context != null) {
			if (context.getMain() != null) {
				cs.setString(MAIN_CONTEXT_ATTR_NAME, context.getMain());
			}
			if (context.getAdditional() != null) {
				cs.setString(ADD_CONTEXT_ATTR_NAME, context.getAdditional());
			}
			if (context.getSession() != null) {
				cs.setString(SESSION_CONTEXT_PARAM, context.getSession());
			}
			if (context.getFilter() != null) {
				cs.setString(FILTER_COLUMNNAME, context.getFilter());
			}
		}

		cs.setString(ELEMENTID_COLUMNNAME, elementInfo.getId());

		LOGGER.debug(context.toString());
		LOGGER.debug(elementInfo.toString());
	}

	/**
	 * Стандартная функция подготовки CallableStatement для выборки данных для
	 * элемента информационной панели.
	 * 
	 * @throws SQLException
	 * 
	 */
	protected void prepareStdStatement() throws SQLException {
		db = ConnectionFactory.getConnection();
		String sql = String.format(getSqlTemplate(), elementInfo.getProcName());
		cs = db.prepareCall(sql);
		setupGeneralParameters();
		cs.registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
	}

	public Connection getDb() {
		return db;
	}

	public void setDb(final Connection aDb) {
		db = aDb;
	}

	public CallableStatement getCs() {
		return cs;
	}

	public void setCs(final CallableStatement aCs) {
		cs = aCs;
	}

	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext aContext) {
		context = aContext;
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
	}

	/**
	 * Возвращает имя OUT параметра с настройками элемента.
	 * 
	 * @return - имя параметра.
	 */
	public abstract String getOutSettingsParam();

	/**
	 * Возвращает шаблон для запуска SQL процедуры.
	 * 
	 * @return - шаблон.
	 */
	protected abstract String getSqlTemplate();

	/**
	 * Возвращает имя схемы для проверки свойств элемента.
	 * 
	 * @return - имя схемы.
	 */
	protected abstract String getSettingsSchema();

	/**
	 * Возвращает поток с настройками элемента, который можно использовать с SAX
	 * парсере.
	 * 
	 * @return - поток.
	 * @throws SQLException
	 */
	protected InputStream getSettingsStream() throws SQLException {
		InputStream validatedSettings = null;
		SQLXML xml;
		xml = getCs().getSQLXML(getOutSettingsParam());
		if (xml != null) {
			InputStream settings = xml.getBinaryStream();
			validatedSettings = XMLUtils.validateXMLStream(settings, getSettingsSchema());
		}
		return validatedSettings;
	}

	/**
	 * Функция освобождения ресурсов, необходимых для создания объекта.
	 * 
	 */
	public void releaseResources() {
		if (getDb() != null) {
			try {
				getDb().close();
			} catch (SQLException e) {
				throw new DBConnectException(e);
			}
		}
	}

	/**
	 * Стандартный обработчик исключений, включающий в себя особую обработку
	 * SolutionDBException.
	 * 
	 * @param e
	 *            - исключение.
	 */
	protected void dbExceptionHandler(final SQLException e) {
		if (SolutionDBException.isSolutionDBException(e)) {
			throw new SolutionDBException(e);
		} else {
			throw new DBQueryException(e, getElementInfo(), getContext());
		}

	}
}