package ru.curs.showcase.security;

import ru.curs.showcase.util.AppProps;

/**
 * @author anlug
 * 
 *         Класс, позволяющий получить url для сервера аутентификации.
 * 
 */
public final class SecurityParamsFactory {
	/**
	 * Имя параметра в файле настроек, содержащего путь к AuthServer.
	 */
	public static final String AUTH_SERVER_URL_PARAM = "security.authserverurl";

	/**
	 * Имя параметра в файле настроек, содержащего локальный путь к AuthServer.
	 */
	public static final String LOCAL_AUTH_SERVER_URL_PARAM = "local.security.authserverurl";

	private SecurityParamsFactory() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * Возвращает URL сервера аутентификации в строке.
	 * 
	 * @return - url.
	 */
	public static String getAuthServerUrl() {

		return AppProps.getRequiredValueByName(AUTH_SERVER_URL_PARAM);

	}

	/**
	 * 
	 * Возвращает локальный URL сервера аутентификации в строке. Локальный URL
	 * нужен для случая, когда Showcase и AuthServer установлены на одном
	 * компьютере, на котором нет доступа к DNS серверам, но при этом auth куки
	 * должны быть привязаны к полному DNS имени AuthServer.
	 * 
	 * @return - url.
	 */
	public static String getLocalAuthServerUrl() {
		String result = AppProps.getOptionalValueByName(LOCAL_AUTH_SERVER_URL_PARAM);
		if (result == null) {
			result = getAuthServerUrl();
		}
		return result;

	}

}
