package ru.curs.showcase.app.client.api;

import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.app.client.*;

/**
 * @author anlug
 * 
 *         Класс реализующий функции обратного вызова из графика (Chart).
 * 
 */
public final class ChartPanelCallbacksEvents {

	private ChartPanelCallbacksEvents() {

	}

	/**
	 * 
	 * Событие одинарного клика на графике (на Chart).
	 * 
	 * @param chartDivId
	 *            - Id графика (ID тэга div для графика)
	 * @param seriesName
	 *            - имя серии графика
	 * @param index
	 *            - индекс (порядковый номер) точки или бара графика на котором
	 *            был совершен клик
	 */
	public static void chartPanelClick(final String chartDivId, final String seriesName,
			final int index) {

		Chart ch =
			((ChartPanel) ActionExecuter.getElementPanelById(chartDivId.substring(0,
					chartDivId.length() - Constants.CHART_DIV_ID_SUFFIX.length()))).getChart();

		ChartEvent chev = ch.getEventManager().getEventForValue(seriesName, index);
		if (chev != null) {
			AppCurrContext.getInstance().setCurrentAction(chev.getAction());
			ActionExecuter.execAction();

		}

	}
}
