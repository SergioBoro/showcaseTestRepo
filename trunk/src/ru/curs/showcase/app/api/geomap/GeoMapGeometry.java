package ru.curs.showcase.app.api.geomap;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Класс, содержащий описание геометрических данных для ГИС объекта (feature)
 * карты.
 * 
 * @author den
 * 
 */
public class GeoMapGeometry implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3005969147056468688L;

	/**
	 * Координаты точки для карты.
	 */
	private Double[] pointCoordinates;

	/**
	 * Координаты полигона на карте. Для будущего использования.
	 */
	private Double[][][] polygonCoordinates;

	/**
	 * Координаты сложного полигона (MULTIPOLYGON) на карте.
	 */
	private Double[][][][] multiPolygonCoordinates;

	public Double[] getPointCoordinates() {
		return pointCoordinates;
	}

	public void setPointCoordinates(final Double[] aPointCoordinates) {
		pointCoordinates = aPointCoordinates;
	}

	public Double[][][] getPolygonCoordinates() {
		return polygonCoordinates;
	}

	public void setPolygonCoordinates(final Double[][][] aPolygonCoordinates) {
		polygonCoordinates = aPolygonCoordinates;
	}

	public GeoMapGeometry() {
		super();
	}

	/**
	 * Возвращает Latitude для точки на карте.
	 * 
	 * @return - Latitude.
	 */
	public Double getLat() {
		return pointCoordinates[1];
	}

	/**
	 * Возвращает longitude для точки на карте.
	 * 
	 * @return - longitude.
	 */
	public Double getLon() {
		return pointCoordinates[0];
	}

	public Double[][][][] getMultiPolygonCoordinates() {
		return multiPolygonCoordinates;
	}

	public void setMultiPolygonCoordinates(final Double[][][][] aMultiPolygonCoordinates) {
		multiPolygonCoordinates = aMultiPolygonCoordinates;
	}

}
