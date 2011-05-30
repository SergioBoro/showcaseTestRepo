package ru.curs.showcase.app.api.chart;

import ru.curs.showcase.app.api.event.Event;

/**
 * Адаптер класса события для графика.
 * 
 * @author den
 * 
 */
public final class ChartEvent extends Event {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6740690372017535475L;

	public String getSeriesId() {
		return getId1();
	}

	/**
	 * Устанавливает идентификатор серии у события.
	 * 
	 * @param seriesId
	 *            - идентификатор серии.
	 */
	public void setSeriesId(final String seriesId) {
		setId1(seriesId);
	}

	public Integer getX() {
		return Integer.valueOf(getId2());
	}

	/**
	 * Устанавливает x у события.
	 * 
	 * @param x
	 *            - x.
	 */
	public void setX(final Integer x) {
		setId2(x.toString());
	}
}
