package ru.curs.showcase.app.server;

import java.util.*;

import org.slf4j.*;

import ru.curs.showcase.exception.XSLTTransformException;
import ru.curs.showcase.util.AppProps;

/**
 * Синглетон для хранения информации о сессиях приложения и глобальной
 * информации в приложении. Хранить данные пользовательских сессий на сервере
 * нежелательно - это усложняет приложение - но в данном случае это вынужденная
 * мера.
 * 
 * @author den
 * 
 */
public final class AppInfoSingleton {
	static final String USER_SESSION_INFO_GENERATE_ERROR =
		"Ошибка при сохранении информации о пользовательской сессии";

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoSingleton.class);

	/** Список userdata. */
	private final HashMap<String, UserData> userdatas = new HashMap<String, UserData>();

	private AppInfoSingleton() {
		super();
	}

	/**
	 * Синглетон.
	 */
	private static final AppInfoSingleton SINGLETON = new AppInfoSingleton();

	/**
	 * Карта пользовательских сессий.
	 */
	private final Map<String, SessionInfo> sessionInfoMap = Collections
			.synchronizedMap(new HashMap<String, SessionInfo>());

	public Map<String, SessionInfo> getSessionInfoMap() {
		return sessionInfoMap;
	}

	/**
	 * Версия контейнера сервлетов.
	 */
	private String servletContainerVersion;

	public String getServletContainerVersion() {
		return servletContainerVersion;
	}

	public void setServletContainerVersion(final String aServletContainerVersion) {
		servletContainerVersion = aServletContainerVersion;
	}

	public static AppInfoSingleton getAppInfo() {
		return SINGLETON;
	}

	/**
	 * Устанавливает session context для сессии используя текущее имя
	 * пользователя и параметры URL.
	 * 
	 * @param params
	 *            - карта с параметрами, полученная из HTTPRequest.
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @throws XSLTTransformException
	 */
	public void setParams(final String sessionId, final Map<String, String[]> params) {
		SortedMap<String, String[]> cur =
			Collections.synchronizedSortedMap(new TreeMap<String, String[]>());
		cur.putAll(params);
		try {
			SessionInfo si = getOrInitSessionInfoObject(sessionId);
			si.setContext(SessionInfoGenerator.generateSessionContext(sessionId, cur));
			si.setUserdataId(getSessionUserdataId(cur));
		} catch (XSLTTransformException e) {
			LOGGER.error(USER_SESSION_INFO_GENERATE_ERROR);
		}
		LOGGER.info("Число пользовательских сессий: " + getAppInfo().sessionInfoMap.size());
	}

	/**
	 * Добавляет сессию в список без параметров URL. Функция используется в
	 * тестовых целях.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 */
	public void addSession(final String sessionId) {
		setParams(sessionId, new TreeMap<String, String[]>());
	}

	/**
	 * Получает идентификатор Userdata из параметров URL.
	 * 
	 * @return - строку с идентификатором.
	 * @param params
	 *            - параметры URL.
	 */
	private String getSessionUserdataId(final Map<String, String[]> params) {
		String userdataId = null;

		if ((params != null) && (!params.isEmpty())) {
			Iterator<String> iterator = params.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (AppProps.URL_PARAM_USERDATA.equals(key)) {
					if (params.get(key) != null) {
						userdataId = Arrays.toString(params.get(key)).trim();
						userdataId = userdataId.replace("[", "").replace("]", "");
						break;
					}
				}
			}
		}

		return userdataId;
	}

	public HashMap<String, UserData> getUserdatas() {
		return userdatas;
	}

	/**
	 * Добавляет UserData в список.
	 * 
	 * @param userdataId
	 *            Идентификатор UserData
	 * 
	 * @param path
	 *            Путь к userdata
	 */
	public void addUserData(final String userdataId, final String path) {
		userdatas.put(userdataId, new UserData(path));
	}

	/**
	 * Возвращает UserData по его идентификатору.
	 * 
	 * @param userdataId
	 *            Идентификатор UserData
	 * 
	 * @return UserData
	 */
	public UserData getUserData(final String userdataId) {
		UserData us = null;
		if (userdataId != null) {
			us = userdatas.get(userdataId);
		}
		return us;
	}

	/**
	 * Получает идентификатор userdata для сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return идентификатор userdata.
	 */
	public String getUserDataIdForSession(final String sessionId) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		return si.getUserdataId();
	}

	/**
	 * Устанавливает признак authViaAuthServer для сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @param authViaAuthServer
	 *            - признак того, что аутентификация прошла через AuthServer.
	 */
	public void setAuthViaAuthServerForSession(final String sessionId,
			final boolean authViaAuthServer) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		si.setAuthViaAuthServer(authViaAuthServer);
	}

	/**
	 * Получает значение признака authViaAuthServer для сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - значение признака.
	 */
	public boolean getAuthViaAuthServerForSession(final String sessionId) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		return si.getAuthViaAuthServer();
	}

	/**
	 * Устанавливает временный уникальный пароль для пользователя, который
	 * аутентифицировался через AuthServer.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @param pass
	 *            - пароль.
	 */
	public void setAuthServerCrossAppPasswordForSession(final String sessionId, final String pass) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		si.setAuthServerCrossAppPassword(pass);
	}

	/**
	 * Получает временный уникальный пароль для пользователя, который
	 * аутентифицировался через AuthServer.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - пароль.
	 */
	public String getAuthServerCrossAppPasswordForSession(final String sessionId) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		return si.getAuthServerCrossAppPassword();
	}

	public Integer getGridColumnGapWidth() {
		return getUserdatas().get(AppProps.getUserDataId()).getGridColumnGapWidth();
	}

	/**
	 * Инициализирует пустой объект с информацией о сессии в карте сессий.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - объект с информацией о сессии.
	 */
	public SessionInfo getOrInitSessionInfoObject(final String sessionId) {
		SessionInfo res = sessionInfoMap.get(sessionId);
		if (res == null) {
			res = new SessionInfo();
			sessionInfoMap.put(sessionId, res);
		}
		return res;
	}

	/**
	 * Удаляет информацию о сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 */
	public void removeSessionInfo(final String sessionId) {
		sessionInfoMap.remove(sessionId);
	}

	/**
	 * Возвращает текущий контекст сессии (для передачи в БД).
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - контекст.
	 */
	static String getSessionContext(final String sessionId) {
		if (sessionId == null) {
			return null;
		}

		String sessionContext =
			AppInfoSingleton.getAppInfo().getOrInitSessionInfoObject(sessionId).getContext();

		if (sessionContext == null) {
			sessionContext = SessionInfoGenerator.generateSessionContext(sessionId, null);
			AppInfoSingleton.getAppInfo().getOrInitSessionInfoObject(sessionId)
					.setContext(sessionContext);
		}

		LOGGER.debug("Session context: " + sessionContext);
		return sessionContext;
	}

	/**
	 * Очищает карту сессий.
	 */
	public void clearSessions() {
		sessionInfoMap.clear();
	}

}
