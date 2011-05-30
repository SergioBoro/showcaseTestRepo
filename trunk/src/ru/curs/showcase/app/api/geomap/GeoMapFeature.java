package ru.curs.showcase.app.api.geomap;

import java.util.*;

/**
 * Класс ГИС объекта (feature) на карте. Применяется для описания точки и
 * полигона, а также любых других типов объектов, которые появятся в будущем.
 * Для объекта может быть задан либо идентификатор его геометрии - в этом случае
 * определение геометрии зашито в шаблоне, либо явное описание его геометрии.
 * 
 * @author den
 * 
 */
public class GeoMapFeature extends GeoMapObject {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -3726515877064440608L;

	/**
	 * Описание геометрии объекта.
	 */
	private GeoMapGeometry geometry = null;

	/**
	 * Идентификатор геометрии объекта.
	 */
	private String geometryId = null;

	/**
	 * Клас CSS стиля, который будет использован для отображения данного
	 * объекта. Если одновременно имеются ненулевые styleClass и style, то будет
	 * использоваться style.
	 */
	private String styleClass = null;

	/**
	 * Набор свойств ГИС объекта. Как минимум, содержит набор пар <ID
	 * показателя>:<Значение показателя для объекта>.
	 */
	private Map<String, Double> properties = new TreeMap<String, Double>();

	public GeoMapGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(final GeoMapGeometry aGeometry) {
		geometry = aGeometry;
	}

	public GeoMapFeature() {
		super();
	}

	public GeoMapFeature(final String aId, final String aName) {
		super(aId, aName);
	}

	public Map<String, Double> getProperties() {
		return properties;
	}

	public void setProperties(final Map<String, Double> aProperties) {
		properties = aProperties;
	}

	/**
	 * Устанавливает значение показателя для объекта.
	 * 
	 * @param indId
	 *            - идентификатор показателя.
	 * @param value
	 *            - значение показателя.
	 */
	public void setValue(final String indId, final Double value) {
		properties.put(indId, value);
	}

	/**
	 * Возвращает значение показателя для переданного объекта.
	 * 
	 * @param ind
	 *            - объект.
	 * @return - значение.
	 */
	public Double getValueForIndicator(final GeoMapIndicator ind) {
		if (ind == null) {
			return null;
		}
		Iterator<String> iterator = properties.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (ind.getId().equals(key)) {
				return properties.get(key);
			}
		}
		return null;
	}

	public String getGeometryId() {
		return geometryId;
	}

	public void setGeometryId(final String aGeometryId) {
		geometryId = aGeometryId;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(final String aStyleClass) {
		styleClass = aStyleClass;
	}
}
