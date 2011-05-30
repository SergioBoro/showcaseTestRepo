package ru.curs.showcase.app.api.event;

import java.util.*;

import ru.curs.showcase.app.api.*;

/**
 * Ссылка на информационную панель. Может содержать ссылку на конкретную вкладку
 * панели. Также содержит информацию о контексте, необходимую для заполнения
 * данными элементов вкладок панели.
 * 
 * @author den
 * 
 */
public class DataPanelLink implements CanBeCurrent, SerializableElement, GWTClonable {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 5921173204380210732L;

	/**
	 * Ссылка на информационную панель.
	 */
	private String dataPanelId;

	/**
	 * Ссылка на вкладку панели, которая должна быть активизирована.
	 */
	private String tabId;

	/**
	 * Признак того, что нужно открывать либо первую страницу в случае смены
	 * панели, либо оставаться на текущей.
	 */
	private Boolean firstOrCurrentTab = false;

	/**
	 * Контекст, связанный с данной ссылкой.
	 */
	private CompositeContext context;

	/**
	 * Коллекция элементов информационной панели, для которых нужно
	 * переопределить контекст или которые нужно перерисовать.
	 */
	private List<DataPanelElementLink> elementLinks = new ArrayList<DataPanelElementLink>();

	/**
	 * Проверяет, является ли панель текущей.
	 * 
	 * @return результат проверки.
	 */
	public Boolean isCurrentPanel() {
		return (CURRENT_ID.equals(dataPanelId));
	}

	/**
	 * Проверяет, является ли вкладка текущей.
	 * 
	 * @return результат проверки.
	 */
	public Boolean isCurrentTab() {
		return (CURRENT_ID.equals(tabId));
	}

	/**
	 * Возвращает ссылку на элемент панели, объявленный в действии, по его ID.
	 * 
	 * @param id
	 *            - идентификатор.
	 * @return - ссылка на элемент.
	 */
	public DataPanelElementLink getElementLinkById(final String id) {
		if (id == null) {
			return null;
		}
		Iterator<DataPanelElementLink> iterator = elementLinks.iterator();
		while (iterator.hasNext()) {
			DataPanelElementLink link = iterator.next();
			if (id.equals(link.getId())) {
				return link;
			}
		}
		return null;
	}

	public final List<DataPanelElementLink> getElementLinks() {
		return elementLinks;
	}

	public final void setElementLinks(final List<DataPanelElementLink> aElementLinks) {
		this.elementLinks = aElementLinks;
	}

	public final CompositeContext getContext() {
		return context;
	}

	public final void setContext(final CompositeContext aContext) {
		this.context = aContext;
	}

	public final String getDataPanelId() {
		return dataPanelId;
	}

	public final void setDataPanelId(final String aDataPanelId) {
		this.dataPanelId = aDataPanelId;
	}

	public final String getTabId() {
		return tabId;
	}

	public final void setTabId(final String aTabId) {
		this.tabId = aTabId;
	}

	public Boolean getFirstOrCurrentTab() {
		return firstOrCurrentTab;
	}

	public void setFirstOrCurrentTab(final Boolean aFirstOrCurrentTab) {
		firstOrCurrentTab = aFirstOrCurrentTab;
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public DataPanelLink gwtClone() {
		DataPanelLink res = new DataPanelLink();
		res.dataPanelId = dataPanelId;
		res.tabId = tabId;
		res.firstOrCurrentTab = firstOrCurrentTab;
		if (context != null) {
			res.context = context.gwtClone();
		}
		Iterator<DataPanelElementLink> iterator = elementLinks.iterator();
		while (iterator.hasNext()) {
			res.getElementLinks().add(iterator.next().gwtClone());
		}
		return res;
	}

	/**
	 * Функция, создающая ссылку на "текущую" панель.
	 * 
	 * @return - ссылка на панель.
	 */
	public static DataPanelLink createCurrent() {
		DataPanelLink result = new DataPanelLink();
		result.setDataPanelId(CURRENT_ID);
		result.setTabId(CURRENT_ID);
		result.setContext(CompositeContext.createCurrent());
		return result;
	}
}
