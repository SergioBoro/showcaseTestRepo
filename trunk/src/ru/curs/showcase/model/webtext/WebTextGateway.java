package ru.curs.showcase.model.webtext;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.HTMLBasedElementRawData;

/**
 * Шлюз для получения данных, необходимых для построения элемента панели типа
 * WebText.
 * 
 * @author den
 * 
 */
public interface WebTextGateway {

	/**
	 * Основная функция получения данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - xml данные.
	 */
	HTMLBasedElementRawData getRawData(CompositeContext context, DataPanelElementInfo element);
}
