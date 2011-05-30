package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с картой и легендой.
 */
public class MapPanel extends BasicElementPanelBasis {

	public MapPanel(final CompositeContext context1, final DataPanelElementInfo element1) {
		this.setContext(context1);
		this.elementInfo = element1;
		setIsFirstLoading(true);

		generalMapPanel = new VerticalPanel();
		generalHp = new HorizontalPanel();
		generalMapPanel.add(new HTML(Constants.PLEASE_WAIT_MAP_DATA_ARE_LOADING));

		dataService = GWT.create(DataService.class);

		setMapPanel();

	}

	public MapPanel(final DataPanelElementInfo element1) {

		// я бы убрал этот код-начало
		this.elementInfo = element1;
		generalHp = new HorizontalPanel();
		this.setContext(null);
		setIsFirstLoading(true);
		// я бы убрал этот код-конец

		generalMapPanel = new VerticalPanel();
		generalMapPanel.add(new HTML(Constants.PLEASE_WAIT_MAP_DATA_ARE_LOADING));
	}

	private void setMapPanel() {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getGeoMap(getContext(), elementInfo, new GWTServiceCallback<GeoMap>(
				Constants.ERROR_OF_MAP_DATA_RETRIEVING_FROM_SERVER) {

			@Override
			public void onSuccess(final GeoMap aGeoMap) {

				geoMap = aGeoMap;
				if (geoMap != null) {
					fillMapPanel(aGeoMap);
				}
			}
		});

	}

	/**
	 * 
	 * Заполняет виджет карты содержимым.
	 * 
	 * @param aGeoMap
	 *            GeoMap
	 */
	protected void fillMapPanel(final GeoMap aGeoMap) {

		final String divIdMap = elementInfo.getId() + Constants.MAP_DIV_ID_SUFFIX;
		final String divIdLegend = elementInfo.getId() + Constants.MAP_LEGEND_DIV_ID_SUFFIX;

		final String htmlForMap = "<div id='" + divIdMap + "'></div>";

		final String htmlForLegend = "<div id='" + divIdLegend + "'></div>";

		footerHTML = new HTML(aGeoMap.getFooter());

		headerHTML = new HTML(aGeoMap.getHeader());

		mapHTML = new HTML(htmlForMap);

		legendHTML = new HTML(htmlForLegend);
		generalMapPanel.clear();
		generalHp.clear();
		generalMapPanel.add(headerHTML);

		switch (aGeoMap.getLegendPosition()) {
		case LEFT:

			generalMapPanel.add(generalHp);
			generalHp.add(legendHTML);
			generalHp.add(mapHTML);
			break;

		case RIGHT:

			generalMapPanel.add(generalHp);
			generalHp.add(mapHTML);
			generalHp.add(legendHTML);
			break;

		case TOP:

			generalMapPanel.add(legendHTML);
			generalMapPanel.add(generalHp);
			generalHp.add(mapHTML);
			break;

		case BOTTOM:

			generalMapPanel.add(generalHp);
			generalHp.add(mapHTML);
			generalMapPanel.add(legendHTML);
			break;

		default:
			break;

		}
		generalMapPanel.add(footerHTML);

		final String paramMap1 = aGeoMap.getJsDynamicData();

		final String paramMap2 = aGeoMap.getTemplate();

		drawMap(divIdMap, paramMap1, paramMap2);
		checkForDefaultAction();
	}

	/**
	 * VerticalPanel на которой отображена карта и легенда.
	 */
	private final VerticalPanel generalMapPanel;

	/**
	 * HorizontalPanel на которой отображена карта и легенда.
	 */
	private final HorizontalPanel generalHp;

	/**
	 * GeoMap geoMap.
	 */
	private GeoMap geoMap = null;

	/**
	 * @return Возвращает текущий объект типа Map - данные карты.
	 */
	public GeoMap getMap() {
		return geoMap;
	}

	/**
	 * Устанавливает текущий объект типа GeoMap - данные карты.
	 * 
	 * @param aGeoMap
	 *            - объект типа GeoMap
	 */
	public void setMap(final GeoMap aGeoMap) {
		this.geoMap = aGeoMap;
	}

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * HTML виждет для подписи карты в нижней части.
	 */
	private HTML footerHTML = null;

	/**
	 * HTML виждет для подписи карты в заголовной части.
	 */
	private HTML headerHTML = null;

	/**
	 * HTML виждет для карты.
	 */
	private HTML mapHTML = null;

	/**
	 * HTML виждет для легенды карты.
	 */
	private HTML legendHTML = null;

	/**
	 * DataPanelElementInfo.
	 */
	private DataPanelElementInfo elementInfo;

	/**
	 * Ф-ция, возвращающая панель с картой и легендой, если она необходима.
	 * 
	 * @return - Панель с картой и легендой.
	 */
	@Override
	public VerticalPanel getPanel() {
		return generalMapPanel;
	}

	/**
	 * 
	 * Процедура прорисовки карты с помощью библиотеки dojo.
	 * 
	 * @param divIdMap
	 *            - ID для div карты
	 * 
	 * @param jsonStr1
	 *            - JSON строка с данными карты
	 * @param jsonStr2
	 *            - JSON строка с настройками карты
	 */
	public native void
			drawMap(final String divIdMap, final String jsonStr1, final String jsonStr2) /*-{
		$wnd.gwtMapFunc =                   
		@ru.curs.showcase.app.client.api.MapPanelCallbacksEvents::mapPanelClick(Ljava/lang/String;Ljava/lang/String;);

		$wnd.dojo.require("course.geo");
		// course.geo.makeMap("map", optionSet1, optionSet2, mapConvertorFunc);
		$wnd.course.geo.makeMap(divIdMap, jsonStr1, jsonStr2, $wnd.mapConvertorFunc);
	}-*/;

	@Override
	public void reDrawPanel(final CompositeContext context1, final Boolean refreshContextOnly) {

		this.setContext(context1);

		if ((!getIsFirstLoading()) && refreshContextOnly) {
			geoMap.updateAddContext(context1);
		} else {

			generalMapPanel.clear();
			generalMapPanel.add(new HTML(Constants.PLEASE_WAIT_MAP_DATA_ARE_LOADING));
			if (dataService == null) {
				dataService = GWT.create(DataService.class);
			}

			dataService.getGeoMap(getContext(), elementInfo, new GWTServiceCallback<GeoMap>(
					Constants.ERROR_OF_MAP_DATA_RETRIEVING_FROM_SERVER) {

				@Override
				public void onSuccess(final GeoMap aGeoMap) {

					geoMap = aGeoMap;
					if (geoMap != null) {
						fillMapPanel(aGeoMap);
						if (getIsFirstLoading() && refreshContextOnly) {
							geoMap.updateAddContext(context1);
						}
						setIsFirstLoading(false);
					}
				}
			});
		}
	}

	@Override
	public void hidePanel() {
		generalMapPanel.setVisible(false);

	}

	@Override
	public void showPanel() {
		generalMapPanel.setVisible(true);

	}

	@Override
	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aelement) {
		this.elementInfo = aelement;
	}

	private void checkForDefaultAction() {
		if (geoMap.getActionForDependentElements() != null) {
			AppCurrContext.getInstance().setCurrentAction(geoMap.getActionForDependentElements());
			ActionExecuter.execAction();
		}
	}

	@Override
	public DataPanelElement getElement() {
		return geoMap;
	}
}
