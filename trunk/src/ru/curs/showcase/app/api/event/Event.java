package ru.curs.showcase.app.api.event;

import ru.curs.showcase.app.api.*;

/**
 * Обобщенный класс события в UI, от которого наследуются события грида,
 * графика... Событие служит для привязки действия к экранному элементу. Событие
 * идентифицируется по типу взаимодействия и нескольким (на данный момент двум)
 * идентификаторам. Например, для грида эти идентификаторы - это идентификаторы
 * строки и столбца. Смысл идентификаторов разъяснен в их описании.
 * 
 * @author den
 * 
 */
public abstract class Event implements SerializableElement {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3610656112304171914L;

	/**
	 * Тип взаимодействия пользователя с элементом UI.
	 */
	private InteractionType interactionType;

	/**
	 * Действие, вызываемое по наступлению события.
	 */
	private Action action;

	/**
	 * Первый идентификатор для события. Является обязательным!
	 */
	private String id1;

	/**
	 * Второй идентификатор для события. Не является обязательным - если он не
	 * задан, событие определяется одной "координатой" либо событие может
	 * происходить при любом значении второй "координаты". Координатами
	 * являются, к примеру, строка и столбец в гриде.
	 */
	private String id2;

	public final InteractionType getInteractionType() {
		return interactionType;
	}

	public final void setInteractionType(final InteractionType aInteractionType) {
		interactionType = aInteractionType;
	}

	public final Action getAction() {
		return action;
	}

	public final void setAction(final Action aAction) {
		action = aAction;
	}

	public final String getId1() {
		return id1;
	}

	public final void setId1(final String aId1) {
		id1 = aId1;
	}

	public final String getId2() {
		return id2;
	}

	public final void setId2(final String aId2) {
		id2 = aId2;
	}
}
