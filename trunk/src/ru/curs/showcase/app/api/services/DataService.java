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

import com.google.gwt.user.client.rpc.*;

/**
 * Основной GWT-RPC интерфейс для приложения. Основное назначение - передача
 * данных для отображения в UI.
 */
@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
	/**
	 * Функция, возвращающая невизуальный объект навигатора с данными для
	 * пользователя, совершившего вход в систему.
	 * 
	 * @return - объект навигатора.
	 * @throws GeneralServerException
	 */
	Navigator getNavigator() throws GeneralServerException;

	/**
	 * Возвращает информационную панель по переданному действию. Информационная
	 * панель формируется учитывая все контексты, переданные в действии.
	 * 
	 * @param action
	 *            - действие.
	 * @return - панель.
	 * @throws GeneralServerException
	 */
	DataPanel getDataPanel(Action action) throws GeneralServerException;

	/**
	 * Возвращает данные для отрисовки элемента типа WebText по переданному
	 * контексту и описанию элемента.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - WebText.
	 * @throws GeneralServerException
	 */
	WebText getWebText(CompositeContext context, DataPanelElementInfo element)
			throws GeneralServerException;

	/**
	 * Возвращает данные для отрисовки элемента типа Grid по переданным
	 * контексту, описанию элемента и требуемым настройкам.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @param settings
	 *            - настройки.
	 * @return - грид.
	 * @throws GeneralServerException
	 */
	Grid getGrid(CompositeContext context, DataPanelElementInfo element,
			GridRequestedSettings settings) throws GeneralServerException;

	/**
	 * Возвращает данные для отрисовки графика по переданным контексту и
	 * описанию элемента.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - chart.
	 * @throws GeneralServerException
	 */
	Chart getChart(CompositeContext context, DataPanelElementInfo element)
			throws GeneralServerException;

	/**
	 * Fake функция для того, чтобы заработала сериализация GWT для класса
	 * ColumnSet. Не удалять!
	 * 
	 * @param cs
	 *            - набор столбцов.
	 * @throws GeneralServerException
	 */
	void saveColumnSet(ColumnSet cs) throws GeneralServerException;

	/**
	 * Возвращает данные для отрисовки карты.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @return - невизуальный объект карты.
	 * @throws GeneralServerException
	 */
	GeoMap getGeoMap(CompositeContext context, DataPanelElementInfo element)
			throws GeneralServerException;

	/**
	 * Возвращает данные для отрисовки формы.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @return - логическая форма без данных.
	 * @throws GeneralServerException
	 * @param currentData
	 *            - текущие данные XForms. Они должны заменить собой данные,
	 *            возвращенные хранимой процедурой.
	 */
	XForms getXForms(CompositeContext context, DataPanelElementInfo element, String currentData)
			throws GeneralServerException;

	/**
	 * Сохраняет данные карточки на основе XForms.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @param data
	 *            - данные.
	 * @return - результат сохранения.
	 * @throws GeneralServerException
	 */
	CommandResult saveXForms(CompositeContext context, DataPanelElementInfo element, String data)
			throws GeneralServerException;

	/**
	 * Возвращает информацию о текущем состоянии сервера и о текущем сеансе.
	 * 
	 * @return - объект с информацией.
	 * @throws GeneralServerException
	 */
	ServerCurrentState getServerCurrentState() throws GeneralServerException;
}
