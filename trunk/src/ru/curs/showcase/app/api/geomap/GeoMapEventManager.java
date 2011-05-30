package ru.curs.showcase.app.api.geomap;

import ru.curs.showcase.app.api.element.EventManager;
import ru.curs.showcase.app.api.event.*;

/**
 * Менеджер события для карты.
 * 
 * @author den
 * 
 */
public class GeoMapEventManager extends EventManager {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3329856250556569988L;

	/**
	 * Функция возвращает нужный обработчик события по переданному ей
	 * идентификатору объекта на карте. Мы не разделяем события на одном и том
	 * же объекте для разных слоев.
	 * 
	 * @param featureId
	 *            - идентификатор строки.
	 * @return - событие или NULL в случае его отсутствия.
	 */
	public GeoMapEvent getEventForFeature(final String featureId) {
		return (GeoMapEvent) getEventByIds(featureId, null, InteractionType.SINGLE_CLICK);
	}
}
