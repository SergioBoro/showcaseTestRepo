package ru.curs.showcase.app.api.element;

import java.util.Iterator;

import ru.curs.showcase.app.api.SerializableElement;
import ru.curs.showcase.app.api.event.*;

/**
 * Базовый класс для элементов информационной панели.
 * 
 * @author den
 * 
 */
public abstract class DataPanelElement implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8787932721898449225L;

	/**
	 * Действие по умолчанию - для сокрытия зависимых элементов при условии, что
	 * в элементе не выделено ни одной записи.
	 */
	private Action defaultAction;

	/**
	 * Менеджер событий в гриде.
	 */
	private EventManager eventManager = initEventManager();

	public final Action getDefaultAction() {
		return defaultAction;
	}

	public final void setDefaultAction(final Action aAction) {
		defaultAction = aAction;
	}

	protected final void setEventManager(final EventManager aEventManager) {
		eventManager = aEventManager;
	}

	/**
	 * Инициализирует менеджер событий.
	 * 
	 * @return - менеджер событий.
	 */
	protected abstract EventManager initEventManager();

	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * Возвращает действие для отрисовки зависимого элемента.
	 * 
	 * @return - действие.
	 */
	public Action getActionForDependentElements() {
		return defaultAction;
	}

	/**
	 * Функция для актуализации состояния действий для всех событий в элемента,
	 * а также события по умолчанию на основе переданного контекста.
	 * 
	 * @param callContext
	 *            - контекст.
	 */
	public void actualizeActions(final CompositeContext callContext) {
		Iterator<Event> iterator = getEventManager().getEvents().iterator();
		while (iterator.hasNext()) {
			Action action = iterator.next().getAction();
			action.actualizeBy(callContext);
		}

		if (defaultAction != null) {
			defaultAction.actualizeBy(callContext);
		}
	}

	/**
	 * Обновляет дополнительный контекст у всех событий элемента и у действия по
	 * умолчанию.
	 * 
	 * @param context
	 *            - новый контекст.
	 */
	public void updateAddContext(final CompositeContext context) {
		Iterator<Event> eviterator = eventManager.getEvents().iterator();
		while (eviterator.hasNext()) {
			Action action = eviterator.next().getAction();
			action.updateAddContext(context);
		}

		if (defaultAction != null) {
			defaultAction.updateAddContext(context);
		}
	}

}
