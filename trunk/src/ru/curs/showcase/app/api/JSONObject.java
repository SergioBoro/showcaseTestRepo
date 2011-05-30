package ru.curs.showcase.app.api;

/**
 * Интерфейс абстрактного Java объекта, из которого будет получен JSON объект.
 * 
 * @author den
 * 
 */
public interface JSONObject {

	/**
	 * Возвращает Java объект с динамическими данными.
	 * 
	 * @return Java объект.
	 */
	Object getJavaDynamicData();

	/**
	 * Очищает Java объект с динамическими данными.
	 */
	void resetJavaDynamicData();

	/**
	 * Устанавливает JSON данные, соответствующие getJavaDynamicData().
	 * 
	 * @param data
	 *            - строка с JSON.
	 */
	void setJsDynamicData(String data);
}
