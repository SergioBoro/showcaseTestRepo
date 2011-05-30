package ru.curs.showcase.app.api;

/**
 * Тип сообщения.
 * 
 * @author den
 * 
 */
public enum MessageType implements SerializableElement {
	/**
	 * Информационное сообщение.
	 */
	INFO,
	/**
	 * Предупреждение.
	 */
	WARNING,
	/**
	 * Ошибка.
	 */
	ERROR
}
