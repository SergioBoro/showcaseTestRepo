package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.http.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import ru.curs.showcase.util.TextUtils;

/**
 * Вспомогательные функции для работы с сервлетами.
 * 
 * @author den
 * 
 */
public final class ServletUtils {

	private ServletUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Возвращает имя пользователя из текущей сессии приложения.
	 * 
	 * @return - имя пользователя.
	 */
	public static String getUserNameFromSession() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return SecurityContextHolder.getContext().getAuthentication().getName();
		} else {
			return null;
		}
	}

	/**
	 * Возвращает идентификатор текущей сессии приложения.
	 * 
	 * @return - идентификатор текущей сессии приложения.
	 */
	public static String getSessionId() {
		String id = null;
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			id =
				((WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication()
						.getDetails()).getSessionId();
		}
		return id;
	}

	/**
	 * Определяет, является ли браузер пользователя "старой версией IE".
	 * "Старыми" считаются 6 и 7 IE.
	 * 
	 * @param request
	 *            - HttpServletRequest.
	 * @return - результат проверки.
	 */
	public static boolean isOldIE(final HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		boolean isOldIE =
			((userAgent.indexOf("msie 6.0") != -1) || (userAgent.indexOf("msie 7.0") != -1)) ? true
					: false;
		return isOldIE;
	}

	/**
	 * Возвращает содержимое реквеста в виде строки. Применяется для обработки
	 * submission с X-форм.
	 * 
	 * @param request
	 *            реквест
	 * @return - строку с содержимым.
	 */
	public static String getRequestAsString(final HttpServletRequest request)
			throws java.io.IOException {
		BufferedReader requestData =
			new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

		StringBuffer stringBuffer = new StringBuffer();
		String line;

		// stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		// Читаем все строчки
		while ((line = requestData.readLine()) != null) {
			stringBuffer.append(line);
		}
		line = stringBuffer.toString();

		return line;
	}

	/**
	 * Определяет тип браузера по UserAgent.
	 * 
	 * @param req
	 *            - HttpServletRequest.
	 * @return - тип браузера.
	 */
	public static BrowserType getBrowser(final HttpServletRequest req) {
		String userAgent = req.getHeader("User-Agent");
		if (userAgent.contains("Gecko") && userAgent.contains("Firefox")) {
			return BrowserType.FIREFOX;
		}
		if (userAgent.contains("AppleWebKit") && userAgent.contains("Chrome")) {
			return BrowserType.CHROME;
		}
		if (userAgent.contains("AppleWebKit") && userAgent.contains("Safari")) {
			return BrowserType.SAFARI;
		}
		if (userAgent.contains("Opera")) {
			return BrowserType.OPERA;
		}
		if (userAgent.contains("MSIE")) {
			return BrowserType.IE;
		}
		return null;
	}

	/**
	 * Возвращает кодировку для текста в адресной строке браузера. Данный метод
	 * корректно работает с кириллическими символами при переходах по ссылке в
	 * любых браузерах, всегда - в Safari, Chrome и Opera, и при обновлении
	 * страницы кнопкой "Обновить" - также в IE и Firefox (при нажатии Enter в
	 * адресной строке в данных браузерах - не работает).
	 * 
	 * @param req
	 *            - HttpServletRequest.
	 * @return - строка с кодировкой.
	 */
	public static String getCharsetForURLParams(final HttpServletRequest req) {
		return "UTF-8";
	}

	/**
	 * Функция устанавливает у response сервера атрибуты, предотвращающие
	 * кэширование результатов запроса в различных браузерах и прокси-серверах.
	 * 
	 * @param resp
	 *            - response (ответ сервера).
	 */
	public static void doNoCasheResponse(final HttpServletResponse resp) {
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "must-revalidate,no-store,no-cache");
		resp.setDateHeader("Expires", 0);
	}

	/**
	 * Проверяет параметр на неверную кодировку ANSI\ISO вместо UTF8 и
	 * исправляет ее при необходимости.
	 * 
	 * @param param
	 *            - параметр.
	 * @return - правильно кодированный параметр.
	 * @throws UnsupportedEncodingException
	 */
	public static String checkAndRecodeURLParam(final String param)
			throws UnsupportedEncodingException {
		String enc = TextUtils.getRealEncoding(param);
		if (enc != ServletUtils.getCharsetForURLParams(null)) {
			return TextUtils.recode(param, enc, ServletUtils.getCharsetForURLParams(null));
		}
		return param;
	}
}
