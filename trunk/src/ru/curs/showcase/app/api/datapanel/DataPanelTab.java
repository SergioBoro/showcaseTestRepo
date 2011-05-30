package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import javax.xml.bind.annotation.XmlTransient;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.*;

/**
 * Класс вкладки информационной панели.
 * 
 * @author den
 * 
 */
public class DataPanelTab extends VisualElement {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -560927756003899524L;
	/**
	 * Позиция вкладки на панели. Нумерация начинается с 0.
	 */
	private Integer position;
	/**
	 * Набор элементов на вкладке панели.
	 */
	private List<DataPanelElementInfo> elements = new ArrayList<DataPanelElementInfo>();

	/**
	 * Родительская панель.
	 */
	@ExcludeFromSerialization
	@XmlTransient
	private DataPanel dataPanel;

	public final List<DataPanelElementInfo> getElements() {
		return elements;
	}

	public DataPanelTab(final Integer aPosition, final DataPanel aDataPanel) {
		super();
		position = aPosition;
		dataPanel = aDataPanel;
	}

	public DataPanelTab() {
		super();
	}

	public final void setElements(final List<DataPanelElementInfo> aElements) {
		this.elements = aElements;
	}

	public final Integer getPosition() {
		return position;
	}

	public final void setPosition(final Integer aPosition) {
		position = aPosition;
	}

	public final DataPanel getDataPanel() {
		return dataPanel;
	}

	public final void setDataPanel(final DataPanel aDataPanel) {
		dataPanel = aDataPanel;
	}

	/**
	 * Возвращает элемент по его id.
	 * 
	 * @param id
	 *            - id.
	 * @return - элемент.
	 */
	public DataPanelElementInfo getElementInfoById(final String id) {
		Iterator<DataPanelElementInfo> iterator = elements.iterator();
		while (iterator.hasNext()) {
			DataPanelElementInfo current = iterator.next();
			if (current.getId().equals(id)) {
				return current;
			}
		}
		return null;
	}

	/**
	 * Функция, которая возвращает действие, соответствующее выбору вкладки
	 * панели.
	 * 
	 * @return - действие.
	 */
	public Action getAction() {
		Action res = new Action();
		res.setDataPanelLink(new DataPanelLink());
		res.getDataPanelLink().setDataPanelId(CanBeCurrent.CURRENT_ID);
		res.getDataPanelLink().setTabId(getId());
		res.getDataPanelLink().setContext(CompositeContext.createCurrent());
		res.determineState();
		return res;
	}
}
