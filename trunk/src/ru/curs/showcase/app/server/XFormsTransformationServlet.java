package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.util.TextUtils;

/**
 * Сервлет, обрабатывающий xslt-преобразование из XForms.
 */
public class XFormsTransformationServlet extends HttpServlet {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 382470453045525219L;

	static final String XSLTFILE_PARAM_ERROR =
		"В XFormsTransformationServlet не передан обязательный параметр xsltfile";

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		String xsltFile = request.getParameter("xsltfile");
		if (xsltFile == null) {
			throw new ServletException(XSLTFILE_PARAM_ERROR);
		}
		String content = ServletUtils.getRequestAsString(request);

		response.setContentType("text/html");
		response.setCharacterEncoding(TextUtils.DEF_ENCODING);

		try {
			ServiceLayerDataServiceImpl sl =
				new ServiceLayerDataServiceImpl(request.getSession().getId());
			String res = sl.handleXSLTSubmission(xsltFile, content);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().append(res);
		} catch (GeneralServerException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().append(e.getOriginalMessage());
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().append(e.getMessage());
		}
		response.getWriter().close();
	}

}
