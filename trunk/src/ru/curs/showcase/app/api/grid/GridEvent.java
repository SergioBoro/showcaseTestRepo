package ru.curs.showcase.app.api.grid;

import ru.curs.showcase.app.api.event.Event;

/**
 * Адаптер абстрактного события для грида.
 * 
 * @author den
 */
public final class GridEvent extends Event {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7882082734060929373L;

	public String getRecordId() {
		return getId1();
	}

	/**
	 * Установка идентификатора записи для события.
	 * 
	 * @param aRecordId
	 *            - идентификатор записи.
	 */
	public void setRecordId(final String aRecordId) {
		setId1(aRecordId);
	}

	public String getColId() {
		return getId2();
	}

	/**
	 * Установка идентификатора столбца для события.
	 * 
	 * @param aColId
	 *            - идентификатор столбца.
	 */
	public void setColId(final String aColId) {
		setId2(aColId);
	}

}
