package ru.curs.showcase.security;

import java.io.InputStream;
import java.net.*;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.*;
import org.xml.sax.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Набор утилит (функций) для работы с сервлетами сервера аутентификации.
 */
public final class AuthServerUtils {
	public static final String APP_PROP_READ_ERROR =
		"Не удалось считать security.authserverurl из app.properties";

	static final String LOGOUT_WARN = "Не удалось разлогиниться с AuthServer";
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerUtils.class);

	public static final String AUTH_SERVER_DATA_ERROR =
		"Ошибка при разборе данных, возвращенных AuthServer: ";
	/**
	 * Экземпляр-псевдоним псевдоним для работы с сервлетами сервера
	 * аутентификации.
	 */
	private static AuthServerUtils theAuthServerAlias;

	/**
	 * Адрес сервера аутентификации.
	 */
	private final String authServerURL;

	private AuthServerUtils(final String url) {
		this.authServerURL = url;
	}

	/**
	 * Возвращает экземпляр-псевдоним для работы с сервером аутентификации (если
	 * таковой имеется).
	 * 
	 * @return theAuthServerAlias
	 * 
	 */
	public static AuthServerUtils getTheAuthServerAlias() {
		return theAuthServerAlias;
	}

	/**
	 * Производит аутентификацию по заданному логину и паролю.
	 * 
	 * @param sesid
	 *            Идентификатор сессии
	 * @param login
	 *            Логин
	 * @param pwd
	 *            Переданный пароль
	 * @return true, если аутентификация удалась
	 */
	public boolean login(final String sesid, final String login, final String pwd) {
		if (authServerURL == null) {
			return false;
		}
		try {
			URL server =
				new URL(authServerURL
						+ String.format("/login?sesid=%s&login=%s&pwd=%s", sesid, login, pwd));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.connect();
			return c.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Производит выход из сессии аутентификации.
	 * 
	 * @param sesid
	 *            Идентификатор сессии.
	 */
	public void logout(final String sesid) {
		if (authServerURL != null) {
			try {
				URL server = new URL(authServerURL + String.format("/logout?sesid=%s", sesid));
				HttpURLConnection c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod("GET");
				c.connect();
				c.getResponseCode();
			} catch (Exception e) {
				// Do nothing, not our problems.
				LOGGER.warn(LOGOUT_WARN);
			}
		}
	}

	/**
	 * Проверяет валидность соединения и работоспособность сервера
	 * аутентификации.
	 * 
	 * @return true если сервер аутентификации работоспособен и доступен.
	 */
	public boolean checkServer() {
		if (authServerURL == null) {
			return false;
		}
		try {
			URL server = new URL(authServerURL + "/authentication.gif");
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.connect();
			return c.getResponseCode() == HttpURLConnection.HTTP_OK
					&& c.getContentType().startsWith("image/gif");
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Фабричный метод генерации клиента менеджера аутентификации.
	 * 
	 * @param url
	 *            URL соединения с менеджером аутентификации, настроенным на
	 *            взаимодействие с LDAP.
	 */
	public static void init(final String url) {

		// На этапе создания проверяется только правильность URL.
		if (url != null) {
			try {
				new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		theAuthServerAlias = new AuthServerUtils(url);
	}

	/**
	 * Проверяет введённое имя пользователя и возвращает SID.
	 * 
	 * @param sesid
	 *            Идентификатор сессии
	 * @param login
	 *            Проверяемый логин
	 * @return UserData
	 */
	public UserData checkUser(final String sesid, final String login) {
		if (authServerURL == null) {
			return null;
		}
		try {
			URL server =
				new URL(authServerURL
						+ String.format("/checkname?sesid=%s&name=%s", sesid, login, login));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.setDoInput(true);
			c.connect();
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				List<UserData> l = UserInfo.parseStream(c.getInputStream());
				if (l.size() > 0) {
					return l.get(0);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Возвращает информацию о текущем пользователе, если сессия
	 * аутентифицирована, и null, если сессия не аутентифицирована.
	 * 
	 * @param sesid
	 *            текущая сессия
	 * @return UserData
	 */
	public UserData isAuthenticated(final String sesid) {
		if (authServerURL == null) {
			return null;
		}
		try {
			URL server =
				new URL(authServerURL + String.format("/isauthenticated?sesid=%s", sesid));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.setDoInput(true);
			c.connect();
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				List<UserData> l = UserInfo.parseStream(c.getInputStream());
				if (l.size() > 0) {
					return l.get(0);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Возвращает URL сервера аутентификации.
	 * 
	 * @return authServerURL
	 */
	public String getUrl() {
		return authServerURL;
	}

	/**
	 * Информация о пользователе, получаемая из сервера аутентификации.
	 */
	private static final class UserInfo implements UserData {

		/**
		 * Login пользователя.
		 */
		private final String login;

		/**
		 * Идентификатор пользователя sid.
		 */
		private final String sid;

		/**
		 * Имя пользователя.
		 */
		private final String name;

		/**
		 * Электронный адрес пользователя.
		 */
		private final String email;

		/**
		 * Телефон пользователя.
		 */
		private final String phone;

		UserInfo(final String alogin, final String asid, final String aname, final String aemail,
				final String aphone) {
			this.login = alogin;
			this.sid = asid;
			this.name = aname;
			this.email = aemail;
			this.phone = aphone;
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
			TransformerFactory.newInstance().newTransformer()
					.transform(new StreamSource(is), new SAXResult(ch));
			return result;
		}

		/**
		 * SID пользователя.
		 */
		@Override
		public String getSid() {
			return sid;
		}

		/**
		 * Email пользователя.
		 */
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
