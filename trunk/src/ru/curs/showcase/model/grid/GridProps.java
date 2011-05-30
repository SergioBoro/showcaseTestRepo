package ru.curs.showcase.model.grid;

import java.io.InputStreamReader;
import java.util.Properties;

import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.SettingsFileType;
import ru.curs.showcase.util.AppProps;

/**
 * Получает настройки грида из профайла.
 * 
 */
public class GridProps {

	/**
	 * Каталог для профайлов грида.
	 */
	private static final String GRIDPROPERTIES = "gridproperties";

	/**
	 * Properties с настройками грида.
	 */
	private final Properties props = new Properties();

	/**
	 * Путь к профайлу грида.
	 */
	private final String profileName;

	public GridProps(final String aProfile) {
		profileName = GRIDPROPERTIES + "/" + aProfile;
		try {
			props.load(new InputStreamReader(AppProps.loadUserDataToStream(profileName)));
		} catch (Exception e) {
			throw new SettingsFileOpenException(e, profileName, SettingsFileType.GRID_PROPERTIES);
		}

	}

	/**
	 * Получает значение настройки грида по ее имени.
	 * 
	 * @param propName
	 *            Название настройки
	 * 
	 * @return Значение настройки
	 * 
	 */
	public String getValueByNameForGrid(final String propName) {

		String result = props.getProperty(propName);
		if (result != null) {
			result = result.trim();
		}
		return result;

	}

	/**
	 * Стандартная функция для чтения Integer значения из файла настроек для
	 * грида.
	 * 
	 * @param paramName
	 *            - имя параметра в файле.
	 * @return - значение параметра.
	 */
	public Integer stdReadIntGridValue(final String paramName) {
		Integer result = null;
		String value = null;
		value = getValueByNameForGrid(paramName);
		if (value != null) {
			try {
				result = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new SettingsFilePropValueFormatException(e, profileName, paramName,
						SettingsFileType.GRID_PROPERTIES);
			}
		}
		return result;
	}

	/**
	 * Стандартная функция для чтения Boolean значения из файла настроек для
	 * грида.
	 * 
	 * @param paramName
	 *            - имя параметра в файле.
	 * @return - значение параметра.
	 */
	public Boolean stdReadBoolGridValue(final String paramName) {
		Boolean result = null;
		String value = null;
		value = getValueByNameForGrid(paramName);
		if (value != null) {
			if (!(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
				throw new SettingsFilePropValueFormatException(profileName, paramName,
						SettingsFileType.GRID_PROPERTIES);
			}
			result = Boolean.valueOf(value);
		}
		return result;
	}

}
