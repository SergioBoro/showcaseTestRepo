package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.util.TextUtils;

/**
 * Сервлет для проверки того, доступен ли гео-модуль. Гео-модуль отключен в open
 * source версии. При проверке возвращаем строку "ок", а не код возврата, чтобы
 * вывести в лог цивильное сообщение для пользователя.
 * 
 * @author den
 * 
 */
public class GeoCheckServlet extends HttpServlet {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2507880112801078333L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		ServletUtils.doNoCasheResponse(response);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html");
		response.setCharacterEncoding(TextUtils.DEF_ENCODING);

		File file = new File(getServletContext().getRealPath("js/internalGeo.js"));
		if (file.exists()) {
			response.getWriter().append("ok");
		}
	}
}
