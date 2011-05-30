package ru.curs.showcase.app.server;

import java.io.*;
import java.util.*;

import org.slf4j.*;

import ru.curs.showcase.util.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Инициализатор приложения. Содержит главную функцию initialize, которая должна
 * быть вызвана и при старте TomCat, и при запуске модульных тестов.
 * 
 * @author den
 * 
 */
public final class AppInitializer {

	/**
	 * SHOWCASE_USER_DATA_PARAM.
	 */
	private static final String SHOWCASE_USER_DATA_PARAM = "user.data";

	/**
	 * PATH_PROPERTIES_ERROR.
	 */
	private static final String PATH_PROPERTIES_ERROR = "Ошибка чтения файла "
			+ AppProps.PATH_PROPERTIES;

	/**
	 * USER_DATA_INFO.
	 */
	private static final String USER_DATA_INFO =
		"Добавлен userdata из path.properties с идентификатором '%s' и путем '%s'";

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppAndSessionEventsListener.class);

	private AppInitializer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Основной метод инициализатора.
	 */
	public static void initialize() {
		XMLUtils.setupSchemaFactory();
		XMLUtils.setupTransformer();
	}

	/**
	 * Считывает userdata's из path.properties. Вызывается 1.В ProductionMode,
	 * если в context.xml нет userdata 2.В режиме модульных тестов
	 * 
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static void readPathProperties() {
		Properties paths = new Properties();
		try {
			paths.load(new InputStreamReader(AppProps.loadResToStream(AppProps.PATH_PROPERTIES),
					"UTF8"));

			Enumeration<?> en = paths.keys();
			while (en.hasMoreElements()) {
				String name = en.nextElement().toString();
				if (name.toLowerCase().contains(SHOWCASE_USER_DATA_PARAM.toLowerCase())) {
					String id =
						name.substring(0,
								name.toLowerCase().indexOf(SHOWCASE_USER_DATA_PARAM.toLowerCase()))
								.trim();

					if ("".equals(id)) {
						id = AppProps.SHOWCASE_USER_DATA_DEFAULT;
					} else {
						id = id.substring(0, id.length() - 1);
					}

					String value = paths.getProperty(name);
					AppInfoSingleton.getAppInfo().addUserData(id, value);
					LOGGER.info(String.format(USER_DATA_INFO, id, value));
				}
			}
		} catch (IOException e) {
			LOGGER.error(PATH_PROPERTIES_ERROR);
		}
	}
}
