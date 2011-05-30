package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Тип обновления данных на панели.
 * 
 * @author den
 * 
 */
public enum DataPanelRefreshMode implements SerializableElement {
	/**
	 * При каждом переключении вкладок панели.
	 */
	EVERY_TIME,
	/**
	 * При первой загрузке элементов вкладки (только).
	 */
	FIRST_LOAD_TIME,
	/**
	 * По таймеру.
	 */
	BY_TIMER
}
