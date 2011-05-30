package ru.curs.showcase.app.api.element;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.*;

/**
 * Абстрактный менеджер событий, от которого наследуются менеджеры события для
 * грида, графика... Менеджер событий хранит набор событий для UI элемента, а
 * также функции для быстрого выбора нужного события из списка.
 * 
 * @author den
 * 
 */
public abstract class EventManager implements SerializableElement {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -464983712459253702L;
	/**
	 * Набор обработчиков для отображаемых в данный момент записей грида.
	 */
	private List<Event> events = new ArrayList<Event>();

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(final List<Event> aEvents) {
		events = aEvents;
	}

	/**
	 * Функция возвращает нужный обработчик события по переданным ей
	 * идентификаторам и типу события. При этом события, у которых заданы 2
	 * идентификатора имеют приоритет перед событиями с одним идентификатором.
	 * 
	 * @param id1
	 *            - идентификатор 1 (обязательный).
	 * @param id2
	 *            - идентификатор 2 (необязательный).
	 * @param interactionType
	 *            - тип взаимодействия.
	 * @return - событие или NULL.
	 */
	protected Event getEventByIds(final String id1, final String id2,
			final InteractionType interactionType) {
		if (id1 == null) {
			return null;
		}
		Iterator<Event> iterator = events.iterator();
		Event forRow = null;
		while (iterator.hasNext()) {
			Event current = iterator.next();
			if (interactionType == current.getInteractionType()) {
				if (id1.equals(current.getId1())) {
					if (current.getId2() == null) {
						forRow = current;
					}
					if ((id2 != null)) {
						if (id2.equals(current.getId2())) {
							return current;
						}
					}
				}
			}
		}
		return forRow;
	}

	/**
	 * Очищает коллекцию событий от мусорных элементов - т.е. элементов, чьи ID
	 * не входят в переданные в процедуру наборы.
	 * 
	 * @param ids1
	 *            - набор возможных id1.
	 * @param ids2
	 *            - набор возможных id2.
	 * @return - число удаленных элементов.
	 */
	public int clean(final Set<String> ids1, final Set<String> ids2) {
		int initSize = events.size();
		Iterator<Event> iterator = events.iterator();
		while (iterator.hasNext()) {
			Event cur = iterator.next();
			if (!ids1.contains(cur.getId1())) {
				iterator.remove();
				continue;
			}
			if ((cur.getId2() != null) && (!ids2.contains(cur.getId2()))) {
				iterator.remove();
				continue;
			}
		}
		return initSize - events.size();
	}
}
