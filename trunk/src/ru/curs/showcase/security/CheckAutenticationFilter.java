package ru.curs.showcase.security;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import ru.curs.showcase.app.server.AppInfoSingleton;
import ru.curs.showcase.exception.SettingsFileOpenException;
import ru.curs.showcase.util.TextUtils;

/**
 * Фильтр, контролирующий доступ к данным системы.
 * 
 */
public class CheckAutenticationFilter implements Filter {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckAutenticationFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		if (httpReq.getSession(false) == null) {
			httpReq.getSession();
			response.reset();
			response.setContentType("text/html");
			response.setCharacterEncoding(TextUtils.DEF_ENCODING);
			response.getWriter().append("SessionNotAuthenticated");
			response.getWriter().close();

		} else {

			LOGGER.debug(httpReq.getSession().getId());

			String url = null;
			try {
				url = SecurityParamsFactory.getLocalAuthServerUrl();
			} catch (SettingsFileOpenException e) {
				throw new ServletException(AuthServerUtils.APP_PROP_READ_ERROR);
			}
			AuthServerUtils.init(url);

			if (AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(
					httpReq.getSession().getId())) {
				UserData ud =
					AuthServerUtils.getTheAuthServerAlias().isAuthenticated(
							httpReq.getSession().getId());
				if (SecurityContextHolder.getContext().getAuthentication() != null) {
					LOGGER.debug("RequestContextHolder = "
							+ ((WebAuthenticationDetails) SecurityContextHolder.getContext()
									.getAuthentication().getDetails()).getSessionId());
				}
				if (ud == null) {
					response.reset();
					response.setContentType("text/html");
					response.setCharacterEncoding(TextUtils.DEF_ENCODING);
					response.getWriter().append("SessionNotAuthenticated");
					response.getWriter().close();
				} else {
					filterChain.doFilter(request, response);
				}
			} else {
				filterChain.doFilter(request, response);
			}
		}
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {
	}

}
