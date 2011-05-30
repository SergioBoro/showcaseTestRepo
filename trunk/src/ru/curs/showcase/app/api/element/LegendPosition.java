package ru.curs.showcase.app.api.element;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Позиция легенды графика относительно объекта информационной панели. Может
 * задаваться для графика и карты.
 * 
 * @author den
 * 
 */
public enum LegendPosition implements SerializableElement {
	/**
	 * Слева.
	 */
	LEFT,
	/**
	 * Справа.
	 */
	RIGHT,
	/**
	 * Сверху.
	 */
	TOP,
	/**
	 * Снизу.
	 */
	BOTTOM
}
