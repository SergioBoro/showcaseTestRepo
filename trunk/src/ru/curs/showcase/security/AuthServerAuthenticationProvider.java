package ru.curs.showcase.security;

import java.net.*;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import ru.curs.showcase.app.server.AppInfoSingleton;
import ru.curs.showcase.exception.SettingsFileOpenException;

/**
 * @author anlug
 * 
 *         Класс, реализующий провайдер аутентификации с помощью AuthServer.
 * 
 */
public class AuthServerAuthenticationProvider implements AuthenticationProvider {
	@Override
	public Authentication authenticate(final Authentication arg1) {

		// TODO Auto-generated method stub
		// arg0.getAuthorities().iterator().next().
		// if (SecurityContextHolder.getContext().getAuthentication() != null) {
		// if
		// (SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
		// {
		// return arg0;
		// }
		// }
		// authentication.
		// UsernamePasswordAuthenticationToken arg0 = arg1;

		// arg0

		String url = "";
		String login = arg1.getPrincipal().toString();
		String pwd = arg1.getCredentials().toString();
		String sesid = ((WebAuthenticationDetails) arg1.getDetails()).getSessionId();

		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e1) {
			throw new AuthenticationServiceException(AuthServerUtils.APP_PROP_READ_ERROR);
		}

		// AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(sesid,
		// false);
		// AppCurrContext.getInstance().setAuthViaAuthServ(false);
		if (!(pwd.equals("9152046062107176349L_default_value"))) {

			AuthServerUtils.init(url);

			// UserData ud =
			// AuthServerUtils.getTheAuthServerAlias().isAuthenticated(sesid);
			// if (ud == null) {
			try {
				URL server =
					new URL(url
							+ String.format("/login?sesid=%s&login=%s&pwd=%s", sesid,
									URLEncoder.encode(login, "UTF-8"),
									URLEncoder.encode(pwd, "UTF-8")));

				HttpURLConnection c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod("GET");
				c.connect();
				// Thread.sleep(1000);
				if (!(c.getResponseCode() == HttpURLConnection.HTTP_OK)) {
					throw new BadCredentialsException("Bad credentials");
				} else {
					// AppCurrContext.getInstance();
					AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(sesid, true);
					// AppCurrContext.getInstance().setAuthViaAuthServ(true);
				}

			} catch (Exception e) {
				if ("Bad credentials".equals(e.getMessage())) {
					throw new BadCredentialsException(e.getMessage());
				} else {
					throw new BadCredentialsException("Authentication server is not available: "
							+ e.getMessage());
				}
			}
			// }
		} else {
			// AppCurrContext.getInstance().setAuthViaAuthServ(true);
			AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(sesid, true);
		}

		// Authentication g = new Authentication.;

		// try {
		// arg0.setAuthenticated(true);
		// } catch (Exception e) {
		// }
		// SecurityContextHolder.getContext().setAuthentication(arg0);
		return arg1;

	}

	@Override
	public boolean supports(final Class<? extends Object> arg0) {
		return (SignedUsernamePasswordAuthenticationToken.class.isAssignableFrom(arg0));
	}

}