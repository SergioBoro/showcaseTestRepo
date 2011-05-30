package ru.curs.showcase.util;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.SettingsFileType;

/**
 * Получает проперти приложения из файла properties.
 * 
 */
public final class AppProps {
	/**
	 * Имя файла с настройками путей приложения. Пути рекомендуется задавать
	 * абсолютно, т.к. относительный путь отсчитывается либо от папки с eclipse,
	 * либо от папки с Tomcat и не является постоянным. При задании пути нужно
	 * использовать двойной обратный слэш в качестве разделителя.
	 */
	public static final String PATH_PROPERTIES = "path.properties";

	/**
	 * SHOWCASE_USER_DATA_DEFAULT.
	 */
	public static final String SHOWCASE_USER_DATA_DEFAULT = "default";

	/**
	 * Параметр в файле настроек путей приложения, содержащий путь к
	 * пользовательским данным.
	 */
	public static final String USER_DATA = "user.data";
	/**
	 * Имя файла с настройками приложения.
	 */
	public static final String PROPFILENAME = "app.properties";
	/**
	 * XSLTTRANSFORMSDIR.
	 */
	public static final String XSLTTRANSFORMSDIR = "xslttransforms";
	/**
	 * XSLTTRANSFORMSFORGRIDDIR.
	 */
	public static final String XSLTTRANSFORMSFORGRIDDIR = "xslttransformsforgrid";
	/**
	 * GRIDDATAXSL.
	 */
	public static final String GRIDDATAXSL = "GridData.xsl";

	/**
	 * Каталог для шаблонов XForms и тестовых данных XForms.
	 */
	public static final String XFORMS_DIR = "xforms";

	/**
	 * Каталог со схемами Showcase - внутренними и пользовательскими.
	 */
	public static final String SCHEMASDIR = "schemas";

	/**
	 * Properties с настройками приложения.
	 */
	private static Properties props = null;
	/**
	 * Путь к каталогу с пользовательскими данными.
	 */
	private static String userDataCatalog = null;

	private AppProps() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Универсальная функция загрузки внутренних ресурсов Web-приложения по
	 * относительному пути используя Java ClassLoader (например, файлов
	 * конфигурации). Загрузка идет из папки classes.
	 * 
	 * @param fileName
	 *            - путь к загружаемому файлу
	 * @return поток с файлом.
	 */
	public static InputStream loadResToStream(final String fileName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		InputStream result = classLoader.getResourceAsStream(fileName);
		return result;
	}

	/**
	 * Универсальная функция получения URL внутренних ресурсов Web-приложения по
	 * относительному пути используя Java ClassLoader (например, файлов
	 * конфигурации). Загрузка идет из папки classes.
	 * 
	 * @param fileName
	 *            - путь к загружаемому ресурсу
	 * @return URL ресурса
	 */
	public static URL getResURL(final String fileName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		URL result = classLoader.getResource(fileName);
		return result;
	}

	/**
	 * Функция загрузки пользовательских ресурсов Web-приложения используя
	 * стандартный java IO API. Загрузка идет из каталога пользовательских
	 * ресурсов, определенном в файле path.properties.
	 * 
	 * @param fileName
	 *            - путь к загружаемому файлу в каталоге пользовательских
	 *            данных.
	 * @return поток с файлом.
	 * @throws IOException
	 */
	public static InputStream loadUserDataToStream(final String fileName) throws IOException {
		initPaths();
		FileInputStream result = new FileInputStream(userDataCatalog + File.separator + fileName);
		return result;
	}

	/**
	 * Получает значение обязательного параметра по его имени.
	 * 
	 * @param propName
	 *            - имя параметра.
	 * @return - значение параметра.
	 */
	public static String getRequiredValueByName(final String propName) {
		String result = generalReadFunc(propName);
		if (result == null) {
			throw new SettingsFileRequiredPropException(PROPFILENAME, propName,
					SettingsFileType.APP_PROPERTIES);
		}
		return result;
	}

	/**
	 * Получает значение необязательного параметра по его имени.
	 * 
	 * @param propName
	 *            - имя параметра.
	 * @return - значение параметра.
	 */
	public static String getOptionalValueByName(final String propName) {
		return generalReadFunc(propName);
	}

	private static String generalReadFunc(final String propName) {
		try {
			init();
			String result = props.getProperty(propName);
			if (result != null) {
				result = result.trim();
			}
			return result;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, PROPFILENAME, SettingsFileType.APP_PROPERTIES);
		}
	}

	private static void init() throws IOException {
		initPaths();

		if (props == null) {
			props = new Properties();
			props.load(new InputStreamReader(loadUserDataToStream(PROPFILENAME), "UTF8"));
		}
	}

	private static void initPaths() throws IOException {
		if (userDataCatalog == null) {
			Properties paths = new Properties();
			paths.load(new InputStreamReader(loadResToStream(PATH_PROPERTIES), "UTF8"));
			userDataCatalog = paths.getProperty(USER_DATA);
			if (userDataCatalog == null) {
				throw new SettingsFileRequiredPropException(PATH_PROPERTIES, USER_DATA,
						SettingsFileType.PATH_PROPERTIES);
			}
		}
	}

	/**
	 * Возвращает текущий каталог с данными пользователя.
	 * 
	 * @return - каталог.
	 */
	public static String getUserDataCatalog() {
		try {
			initPaths();
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, PATH_PROPERTIES,
					SettingsFileType.PATH_PROPERTIES);
		}
		return userDataCatalog;
	}

	public static void setUserDataCatalog(final String aUserDataCatalog) {
		userDataCatalog = aUserDataCatalog;
	}
}
