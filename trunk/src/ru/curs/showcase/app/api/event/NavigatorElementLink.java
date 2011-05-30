package ru.curs.showcase.app.api.event;

import ru.curs.showcase.app.api.*;

/**
 * Ссылка на элемента навигатора. Поля id и refresh могут быть заданы как
 * одновременно, так и только какое-либо одно из них.
 * 
 * @author den
 * 
 */
public class NavigatorElementLink implements SerializableElement, GWTClonable {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 748495253205916621L;
	/**
	 * Идентификатор элемента навигатора, который нужно выделить.
	 */
	private String id;

	/**
	 * Признак того, что навигатор нужно обновить.
	 */
	private Boolean refresh = false;

	public final String getId() {
		return id;
	}

	public final void setId(final String aId) {
		this.id = aId;
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public NavigatorElementLink gwtClone() {
		NavigatorElementLink res = new NavigatorElementLink();
		res.id = id;
		res.refresh = refresh;
		return res;
	}

	public Boolean getRefresh() {
		return refresh;
	}

	public void setRefresh(final Boolean aRefresh) {
		refresh = aRefresh;
	}
}
