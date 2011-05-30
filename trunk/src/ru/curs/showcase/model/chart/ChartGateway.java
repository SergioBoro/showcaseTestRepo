package ru.curs.showcase.model.chart;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.ElementRawData;

/**
 * Шлюз для получения "сырых" данных для построения графика.
 * 
 * @author den
 * 
 */
public interface ChartGateway {
	/**
	 * Основной метод, возвращающий данные для построения графика. Данные
	 * возвращаются в виде фабрики, уже готовой к построению графика.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - фабрика.
	 */
	ElementRawData getFactorySource(CompositeContext context, DataPanelElementInfo element);
}
