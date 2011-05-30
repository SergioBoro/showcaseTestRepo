package ru.curs.showcase.app.api.geomap;

import ru.curs.showcase.app.api.element.*;

/**
 * Класс карты - элемента информационной панели. На карте отображаются
 * географические данные.
 * 
 * @author den
 * 
 */
public final class GeoMap extends DataPanelElementWithLegend {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3147374218653835207L;

	/**
	 * Динамические данные Java.
	 */
	private GeoMapData javaDynamicData;

	public GeoMap(final GeoMapData aGeoMapData) {
		javaDynamicData = aGeoMapData;
	}

	public GeoMap() {
		super();
	}

	@Override
	protected EventManager initEventManager() {
		return new GeoMapEventManager();
	}

	@Override
	public GeoMapEventManager getEventManager() {
		return (GeoMapEventManager) super.getEventManager();
	}

	@Override
	public GeoMapData getJavaDynamicData() {
		return javaDynamicData;
	}

	@Override
	public void resetJavaDynamicData() {
		javaDynamicData = null;
	}

	public void setJavaDynamicData(final GeoMapData aJavaDynamicData) {
		javaDynamicData = aJavaDynamicData;
	}
}
