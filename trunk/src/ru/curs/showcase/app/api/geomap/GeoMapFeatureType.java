package ru.curs.showcase.app.api.geomap;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Тип ГИС объекта на карте.
 * 
 * @author den
 * 
 */
public enum GeoMapFeatureType implements SerializableElement {
	/**
	 * Точка.
	 */
	POINT,
	/**
	 * Полигон. Представляет собой многоугольник с возможностью исключения из
	 * него нескольких внутренних областей.
	 */
	POLYGON,
	/**
	 * Сложный полигон, состоящий из нескольких простых полигонов.
	 */
	MULTIPOLYGON
}
