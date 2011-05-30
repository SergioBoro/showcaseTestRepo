package ru.curs.showcase.model;

import java.lang.reflect.Modifier;

import org.slf4j.*;

import ru.curs.showcase.app.api.JSONObject;

import com.google.gson.*;

/**
 * Класс адаптера для использования объекта графика в JS коде.
 * 
 * @author den
 * 
 */
public class AdapterForJS {
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdapterForJS.class);

	/**
	 * Результат.
	 */
	private JSONObject result;

	/**
	 * Функция подготовки графика для использования в JS коде. Подготовка
	 * заключается в построении JSON объекта по Java объекту ChartData.
	 * 
	 * @param source
	 *            - график для обработки.
	 */
	public void adapt(final JSONObject source) {
		result = source;
		convertToJSON();
		stripChartObject();
	}

	private void stripChartObject() {
		result.resetJavaDynamicData();
	}

	private void convertToJSON() {
		GsonBuilder builder =
			new GsonBuilder().serializeNulls().excludeFieldsWithModifiers(
					Modifier.TRANSIENT + Modifier.STATIC);
		if (LOGGER.isDebugEnabled()) {
			builder = builder.setPrettyPrinting();
		}
		Gson gson = builder.create();
		// вариант по умолчанию
		// Gson gson = new Gson();

		String json = gson.toJson(result.getJavaDynamicData());
		result.setJsDynamicData(json);
	}
}
