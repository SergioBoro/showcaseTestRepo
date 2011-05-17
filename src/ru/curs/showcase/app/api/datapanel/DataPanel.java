package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.Action;

/**
 * Класс информационной панели. Панель содержит набор вкладок, каждая из которых
 * содержит набор элементов.
 * 
 * @author den
 * 
 */
public class DataPanel implements SerializableElement {
	public static final int DEF_TIMER_INTERVAL = 600;

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1182249077845843177L;

	/**
	 * Набор вкладок панели.
	 */
	private List<DataPanelTab> tabs = new ArrayList<DataPanelTab>();

	/**
	 * Режим обновления данных на панели. По умолчанию - при каждом
	 * переключении.
	 */
	private DataPanelRefreshMode refreshMode = DataPanelRefreshMode.EVERY_TIME;

	/**
	 * Интервал обновления панели в секундах. Используется только в режиме
	 * DataPanelRefreshMode.BY_TIMER.
	 */
	private Integer refreshInterval = DEF_TIMER_INTERVAL;

	/**
	 * Возвращает активную вкладку для переданного объекта действия. Если в
	 * действии не задана вкладка панели - открывается первая вкладка.
	 * 
	 * @param action
	 *            - действие.
	 * @return - вкладка.
	 */
	public DataPanelTab getActiveTabForAction(final Action action) {
		if (action.getDataPanelLink() != null) {
			return getTabById(action.getDataPanelLink().getTabId());
		} else {
			return tabs.iterator().next();
		}
	}

	/**
	 * Возвращает вкладку по id.
	 * 
	 * @param aTabId
	 *            - id.
	 * @return вкладку.
	 */
	public DataPanelTab getTabById(final String aTabId) {
		Iterator<DataPanelTab> iterator = tabs.iterator();
		while (iterator.hasNext()) {
			DataPanelTab current = iterator.next();
			if (current.getId().equals(aTabId)) {
				return current;
			}
		}
		return null;
	}

	public final List<DataPanelTab> getTabs() {
		return tabs;
	}

	public final void setTabs(final List<DataPanelTab> aTabs) {
		this.tabs = aTabs;
	}

	/**
	 * Добавляет и инициализирует вкладку к панели.
	 * 
	 * @param id
	 *            - id вкладки.
	 * @param name
	 *            - наименование вкладки.
	 * @return - вкладка.
	 */
	public DataPanelTab add(final String id, final String name) {
		DataPanelTab res = new DataPanelTab();
		res.setId(id);
		res.setName(name);
		res.setDataPanel(this);
		res.setPosition(tabs.size());
		tabs.add(res);
		return res;
	}

	public DataPanelRefreshMode getRefreshMode() {
		return refreshMode;
	}

	public void setRefreshMode(final DataPanelRefreshMode aRefreshMode) {
		refreshMode = aRefreshMode;
	}

	public Integer getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(final Integer aRefreshInterval) {
		refreshInterval = aRefreshInterval;
	}
}
