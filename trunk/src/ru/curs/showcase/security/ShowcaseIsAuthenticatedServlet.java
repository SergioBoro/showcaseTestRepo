package ru.curs.showcase.security;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.exception.SettingsFileOpenException;
import ru.curs.showcase.util.TextUtils;

/**
 * Servlet implementation class ShowcaseIsAuthenticatedServlet.
 */
public class ShowcaseIsAuthenticatedServlet extends HttpServlet {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 9152046062107176349L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		String url = null;
		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e) {
			throw new ServletException(AuthServerUtils.APP_PROP_READ_ERROR);
		}

		String sesid;
		try {
			sesid = request.getParameter("sesid");
		} catch (Exception e) {
			sesid = null;
		}

		if (!(url == null)) {
			URL server = new URL(url + String.format("/isauthenticated?sesid=%s", sesid));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.setDoInput(true);
			c.connect();
			UserData ud = null;
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				try {
					List<UserData> l = UserInfo.parseStream(c.getInputStream());
					ud = l.get(0);
				} catch (TransformerException e) {
					throw new ServletException(AuthServerUtils.AUTH_SERVER_DATA_ERROR
							+ e.getMessage());
				}
				if (!(ud == null)) {
					String lgn = ud.getCaption();
					System.out.print(lgn);
					response.reset();
					response.setStatus(c.getResponseCode());
					response.setContentType("text/html");
					response.setCharacterEncoding(TextUtils.DEF_ENCODING);
					response.getWriter().append(
							String.format("{login:'%s', pwd:'%s'}", lgn,
									"9152046062107176349L_default_value"));

					response.getWriter().close();
				}
			}
		}

	}

	/**
	 * Информация о пользователе, полученная из LDAP.
	 * 
	 */
	private static final class UserInfo implements UserData {
		/**
		 * Логин пользователя.
		 */
		private final String login;
		/**
		 * SID пользователя.
		 */
		private final String sid;
		/**
		 * Имя пользователя.
		 */
		private final String name;
		/**
		 * Почтовый адрес пользователя.
		 */
		private final String email;
		/**
		 * Телефон пользователя.
		 */
		private final String phone;

		UserInfo(final String aLogin, final String aSid, final String aName, final String aEmail,
				final String aPhone) {
			this.login = aLogin;
			this.sid = aSid;
			this.name = aName;
			this.email = aEmail;
			this.phone = aPhone;
		}

		static List<UserData> parseStream(final InputStream is) throws TransformerException {
			final List<UserData> result = new LinkedList<UserData>();
			final ContentHandler ch = new DefaultHandler() {
				@Override
				public void startElement(final String uri, final String localName,
						final String prefixedName, final Attributes atts) throws SAXException {
					if ("user".equals(localName)) {
						UserInfo ui =
							new UserInfo(atts.getValue("login"), atts.getValue("SID"),
									atts.getValue("name"), atts.getValue("email"),
									atts.getValue("phone"));
						result.add(ui);
					}
				}
			};
			// TODO нужна ли здесь трансформация или хватит про SAXParser
			TransformerFactory.newInstance().newTransformer()
					.transform(new StreamSource(is), new SAXResult(ch));
			return result;
		}

		@Override
		public String getSid() {
			return sid;
		}

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public String getCaption() {
			return login;
		}

		@Override
		public String getFullName() {
			return name;
		}

		@Override
		public String getPhone() {
			return phone;
		}
	}

}
