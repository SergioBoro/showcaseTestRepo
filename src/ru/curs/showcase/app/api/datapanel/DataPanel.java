package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import ru.curs.showcase.app.api.SerializableElement;
import ru.curs.showcase.app.api.event.Action;

/**
 * Класс информационной панели. Панель содержит набор вкладок, каждая из которых
 * содержит набор элементов.
 * 
 * @author den
 * 
 */
public class DataPanel implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1182249077845843177L;

	/**
	 * Идентификатор панели. В случае хранения панели в файле - имя файла без
	 * расширения.
	 */
	private String id;

	/**
	 * Набор вкладок панели.
	 */
	private List<DataPanelTab> tabs = new ArrayList<DataPanelTab>();

	public DataPanel(final String aId) {
		super();
		id = aId;
	}

	public DataPanel() {
		super();
	}

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
	 * @param tabId
	 *            - id вкладки.
	 * @param tabNamr
	 *            - наименование вкладки.
	 * @return - вкладка.
	 */
	public DataPanelTab add(final String tabId, final String tabNamr) {
		DataPanelTab res = new DataPanelTab();
		res.setId(tabId);
		res.setName(tabNamr);
		res.setDataPanel(this);
		res.setPosition(tabs.size());
		tabs.add(res);
		return res;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

}
