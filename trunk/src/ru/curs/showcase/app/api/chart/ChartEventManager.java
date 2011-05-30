package ru.curs.showcase.app.api.chart;

import ru.curs.showcase.app.api.element.EventManager;
import ru.curs.showcase.app.api.event.*;

/**
 * Адаптер абстрактного менеджера событий для графика.
 * 
 * @author den
 * 
 */
public final class ChartEventManager extends EventManager {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3574166433929505612L;

	/**
	 * Функция возвращает нужный обработчик события по переданным ей
	 * идентификаторам значения на графике и типу взаимодействия.
	 * 
	 * @param seriesName
	 *            - идентификатор строки.
	 * @param x
	 *            - идентификатор строки.
	 * @return - событие или NULL.
	 */
	public ChartEvent getEventForValue(final String seriesName, final Integer x) {
		return (ChartEvent) getEventByIds(seriesName, x.toString(), InteractionType.SINGLE_CLICK);
	}
}
