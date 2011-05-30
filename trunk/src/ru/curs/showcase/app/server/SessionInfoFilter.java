package ru.curs.showcase.app.server;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Фильтр для считывание из URL параметров сессии.
 * 
 * @author den
 * 
 */
public class SessionInfoFilter implements Filter {
	/**
	 * Префикс сервлетов, используемых в механизме аутентификации.
	 */
	static final String AUTH_DATA_SERVLET_PREFIX = "auth";
	/**
	 * Префикс сервлетов, используемых для передачи или получения
	 * пользовательских данных.
	 */
	static final String SECURED_DATA_SERVLET_PREFIX = "secured";
	/**
	 * Имя основной страницы приложения.
	 */
	private static final String INDEX_FILE = "index.jsp";

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse resp,
			final FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpreq = ((HttpServletRequest) req);
			if (isMainPage(httpreq)) {
				readURLParams(httpreq);
			}
			if (isDynamicDataServlet(httpreq)) {
				skipServletCaching(resp);
			}
		}
		chain.doFilter(req, resp);
	}

	private void skipServletCaching(final ServletResponse resp) {
		HttpServletResponse httpresp = ((HttpServletResponse) resp);
		ServletUtils.doNoCasheResponse(httpresp);
	}

	private void readURLParams(final HttpServletRequest httpreq)
			throws UnsupportedEncodingException {
		SortedMap<String, String[]> params = prepareParamsMap(httpreq);
		HttpSession session = httpreq.getSession(true);
		AppInfoSingleton.getAppInfo().setParams(session.getId(), params);
	}

	private boolean isMainPage(final HttpServletRequest httpreq) {
		return httpreq.getServletPath().contains("/" + INDEX_FILE);
	}

	private boolean isDynamicDataServlet(final HttpServletRequest httpreq) {
		String servletPath = httpreq.getServletPath();
		return servletPath.startsWith("/" + SECURED_DATA_SERVLET_PREFIX)
				|| servletPath.startsWith("/" + AUTH_DATA_SERVLET_PREFIX);
	}

	/**
	 * Подготавливает карту с параметрами URL. При подготовке учитывается то,
	 * что русские параметры URL считываются сервером в кодировке ISO-8859-1 при
	 * том, что в реальности они приходят либо в UTF-8 либо в СP1251, а также
	 * тот факт, что установка req.setCharacterEncoding("ISO-8859-1"): 1) не
	 * помогает и 2) приводит к сбоям GWT-RPC вызовов
	 * 
	 * @param req
	 *            - http запрос.
	 * @return - map с параметрами.
	 * @throws UnsupportedEncodingException
	 */
	private SortedMap<String, String[]> prepareParamsMap(final HttpServletRequest req)
			throws UnsupportedEncodingException {
		SortedMap<String, String[]> cur = new TreeMap<String, String[]>();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = req.getParameterMap().keySet().iterator();
		while (iterator.hasNext()) {
			String oldKey = iterator.next();
			String key = ServletUtils.checkAndRecodeURLParam(oldKey);
			String[] oldValues = (String[]) req.getParameterMap().get(oldKey);
			String[] values = oldValues.clone();
			for (int i = 0; i < oldValues.length; i++) {
				values[i] = ServletUtils.checkAndRecodeURLParam(oldValues[i]);
			}
			cur.put(key, values);
		}
		return cur;
	}

	@Override
	public void init(final FilterConfig config) throws ServletException {
	}

}
