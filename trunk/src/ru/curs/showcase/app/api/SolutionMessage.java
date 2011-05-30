package ru.curs.showcase.app.api;

/**
 * Сообщение для пользователя решения, созданного с помощью Showcase.
 * 
 * @author den
 * 
 */
public class SolutionMessage implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7453520039334421681L;

	/**
	 * Идентификатор сообщения.
	 */
	private String id;

	/**
	 * Текст сообщения.
	 */
	private String text;

	/**
	 * Тип сообщения.
	 */
	private MessageType type;

	public String getText() {
		return text;
	}

	public void setText(final String aText) {
		text = aText;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(final MessageType aType) {
		type = aType;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}
}
