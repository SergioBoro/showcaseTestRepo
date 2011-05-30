package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Возможные типы элементов информационной панели.
 * 
 * @author den
 * 
 */
public enum DataPanelElementType implements SerializableElement {
	/**
	 * Не элемент панели.
	 */
	NON_DP_ELEMENT,
	/**
	 * Грид.
	 */
	GRID,
	/**
	 * График.
	 */
	CHART,
	/**
	 * Текст с активными элементами.
	 */
	WEBTEXT,
	/**
	 * Карта.
	 */
	GEOMAP,
	/**
	 * XForms - форма для редактирования или фильтрации.
	 */
	XFORMS
}
