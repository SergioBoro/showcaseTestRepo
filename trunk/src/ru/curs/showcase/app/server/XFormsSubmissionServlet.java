package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.model.RequestResult;
import ru.curs.showcase.util.TextUtils;

/**
 * Сервлет, обрабатывающий submission из XForms.
 */
public class XFormsSubmissionServlet extends HttpServlet {

	static final String PROC_PARAM_ERROR =
		"В XFormsSubmissionServlet не передан обязательный параметр proc";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -1387485389229827545L;

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse response)
			throws ServletException, IOException {
		String procName = req.getParameter("proc");
		if (procName == null) {
			throw new ServletException(PROC_PARAM_ERROR);
		}
		String content = ServletUtils.getRequestAsString(req);

		response.setContentType("text/html");
		response.setCharacterEncoding(TextUtils.DEF_ENCODING);
		try {
			ServiceLayerDataServiceImpl sl =
				new ServiceLayerDataServiceImpl(req.getSession().getId());
			RequestResult res = sl.handleSQLSubmission(procName, content);
			if (res.getSuccess()) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().append(res.getData());
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().append(res.generateStandartErrorMessage());
			}
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
