package ru.curs.showcase.app.api.services;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Асинхронный "двойник" основного GWT-RPC интерфейса приложения - DataService.
 */
public interface DataServiceAsync {

	/**
	 * Асинхронная версия.
	 * 
	 * @param callback
	 *            - callback.
	 */
	void getNavigator(AsyncCallback<Navigator> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param callback
	 *            - callback.
	 * @param action
	 *            - действие.
	 */
	void getDataPanel(Action action, AsyncCallback<DataPanel> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 */
	void getWebText(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<WebText> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param settings
	 *            - настройки.
	 * @param callback
	 *            - callback.
	 */
	void getGrid(CompositeContext context, DataPanelElementInfo element,
			GridRequestedSettings settings, AsyncCallback<Grid> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 */
	void getChart(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<Chart> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param cs
	 *            - набор столбцов.
	 * @param callback
	 *            - callback.
	 */
	void saveColumnSet(ColumnSet cs, AsyncCallback<Void> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 */
	void getGeoMap(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<GeoMap> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param currentData
	 *            - данные.
	 * @param callback
	 *            - callback.
	 */
	void getXForms(CompositeContext context, DataPanelElementInfo element, String currentData,
			AsyncCallback<XForms> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 * @param data
	 *            - данные.
	 */
	void saveXForms(CompositeContext context, DataPanelElementInfo element, String data,
			AsyncCallback<CommandResult> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param callback
	 *            - callback.
	 */
	void getServerCurrentState(AsyncCallback<ServerCurrentState> callback);

}
