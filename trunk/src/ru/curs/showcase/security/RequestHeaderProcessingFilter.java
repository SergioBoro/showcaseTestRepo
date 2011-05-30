package ru.curs.showcase.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.security.core.*;
import org.springframework.security.web.authentication.*;

//imports omitted
/**
 * Тестовый фильтр аутентификации Spring Security.
 * 
 * @author den
 * 
 */
public class RequestHeaderProcessingFilter extends AbstractAuthenticationProcessingFilter {

	protected RequestHeaderProcessingFilter() {
		super("/j_spring_security_check");
	}

	/**
	 * Параметр HttpServletRequest с именем пользователя.
	 */
	private final String usernameHeader = "j_username";
	/**
	 * Параметр HttpServletRequest с паролем пользователя.
	 */
	private final String passwordHeader = "j_password";

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException {
		String username = request.getParameter(usernameHeader);
		String password = request.getParameter(passwordHeader);
		SignedUsernamePasswordAuthenticationToken authRequest =
			new SignedUsernamePasswordAuthenticationToken(username, password);
		authRequest.setDetails(new WebAuthenticationDetails(request));

		// обработчик устанавливающий что будет происходить в случае когда в
		// процессе аутентификации произошла ошибка - тоесть redirect на
		// страницу ввода пароля
		setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler() {

			@Override
			public void onAuthenticationFailure(final HttpServletRequest request,
					final HttpServletResponse response, final AuthenticationException exception)
					throws IOException, ServletException {
				super.setDefaultFailureUrl("/login.jsp?error=true");
				super.onAuthenticationFailure(request, response, exception);

			}

		});

		return this.getAuthenticationManager().authenticate(authRequest);
	}
}