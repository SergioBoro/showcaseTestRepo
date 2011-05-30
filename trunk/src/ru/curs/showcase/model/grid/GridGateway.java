package ru.curs.showcase.model.grid;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridRequestedSettings;
import ru.curs.showcase.model.ElementRawData;

/**
 * Интерфейс шлюза к уровню данных для грида. Возвращает данные и метаданные для
 * грида.
 * 
 * @author den
 * 
 */
public interface GridGateway {
	/**
	 * Основная функция, возвращающая данные о гриде.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент грида.
	 * @param settings
	 *            - настройки грида.
	 * @return - данные.
	 * 
	 */
	ElementRawData getFactorySource(CompositeContext context, DataPanelElementInfo element,
			GridRequestedSettings settings);

	/**
	 * Упрощенная функция для возвращения данных о гриде - без указания
	 * требуемых настроек.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - данные.
	 */
	ElementRawData getFactorySource(CompositeContext context, DataPanelElementInfo element);
}
