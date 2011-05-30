package ru.curs.showcase.app.api.chart;

import java.util.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Динамические данные для графика, передаваемые непосредственно в функцию
 * отрисовки графика. Для графика может быть задан либо набор подписей labelsY,
 * либо функция labelYfunc. Приоритет имеет набор подписей.
 * 
 * @author den
 * 
 */
public class ChartData implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2570277058263361565L;

	/**
	 * Ширина графика.
	 */
	private Integer width;

	/**
	 * Высота графика.
	 */
	private Integer height;

	/**
	 * Набор серий с данными, подписями и некоторыми дополнительными
	 * настройками.
	 */
	private List<ChartSeries> series = new ArrayList<ChartSeries>();

	/**
	 * Набор подписей для оси X.
	 */
	private List<ChartLabel> labelsX = new ArrayList<ChartLabel>();

	/**
	 * Набор подписей для оси Y.
	 */
	private List<ChartLabel> labelsY = new ArrayList<ChartLabel>();

	public final void setLabelsY(final List<ChartLabel> aLabelsY) {
		labelsY = aLabelsY;
	}

	public final Integer getWidth() {
		return width;
	}

	public final void setWidth(final Integer aWidth) {
		width = aWidth;
	}

	public final Integer getHeight() {
		return height;
	}

	public final void setHeight(final Integer aHeight) {
		height = aHeight;
	}

	public final List<ChartSeries> getSeries() {
		return series;
	}

	public final void setSeries(final List<ChartSeries> aSeries) {
		series = aSeries;
	}

	public final List<ChartLabel> getLabelsX() {
		return labelsX;
	}

	public final void setLabelsX(final List<ChartLabel> aLabelsX) {
		labelsX = aLabelsX;
	}

	public final List<ChartLabel> getLabelsY() {
		return labelsY;
	}

	/**
	 * Возвращает объект подписи для оси Y по его значению.
	 * 
	 * @param aY
	 *            - значение Y.
	 * @return - подпись.
	 */
	public ChartLabel getLabelsYByValue(final double aY) {
		Iterator<ChartLabel> iterator = labelsY.iterator();
		while (iterator.hasNext()) {
			ChartLabel cur = iterator.next();
			if (aY == cur.getValue().doubleValue()) {
				return cur;
			}
		}
		return null;
	}

	/**
	 * Возвращает серию по ее ID.
	 * 
	 * @param seriesName
	 *            - ID серии.
	 * @return - серию или null.
	 */
	public ChartSeries getSeriesById(final String seriesName) {
		if (seriesName == null) {
			return null;
		}
		Iterator<ChartSeries> iterator = series.iterator();
		while (iterator.hasNext()) {
			ChartSeries current = iterator.next();
			if (seriesName.equals(current.getName())) {
				return current;
			}
		}
		return null;
	}
}
