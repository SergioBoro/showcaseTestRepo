package ru.curs.showcase.app.api.html;

import ru.curs.showcase.app.api.event.Event;


/**
 * Адаптер класса события для HTML based элементов.
 * 
 * @author den
 * 
 */
public final class HTMLEvent extends Event {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6740690372017535475L;

	public String getLinkId() {
		return getId1();
	}

	/**
	 * Устанавливает идентификатор серии у события.
	 * 
	 * @param seriesId
	 *            - идентификатор серии.
	 */
	public void setLinkId(final String seriesId) {
		setId1(seriesId);
	}
}
