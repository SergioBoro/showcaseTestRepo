package ru.curs.showcase.model;

import org.xml.sax.Attributes;

/**
 * Интерфейс обработчика дополнительных тэгов для SAX парсера.
 * 
 * @author den
 * 
 */
public interface SAXTagHandler {
	/**
	 * Определяет, может ли обработать тэг данный обработчик. Функция может
	 * использоваться как клиентом, так и внутри handleXXX функций объекта.
	 * 
	 * @param tagName
	 *            - имя тэга.
	 * @param saxEventType
	 *            - тип события.
	 * @return - результат проверки.
	 */
	boolean canHandle(String tagName, SaxEventType saxEventType);

	/**
	 * Обработчик начала тэга.
	 * 
	 * @param namespaceURI
	 *            - пространство имен.
	 * @param lname
	 *            - локальное имя (без префикса).
	 * @param qname
	 *            - полное имя (с префиксом) - рекомендуется для использования.
	 * @param attrs
	 *            - атрибуты тэга.
	 * @return - null или объект, созданный при обработке тэга.
	 */
	Object handleStartTag(String namespaceURI, String lname, String qname, Attributes attrs);

	/**
	 * Обработчик начала тэга.
	 * 
	 * @param namespaceURI
	 *            - пространство имен.
	 * @param lname
	 *            - локальное имя (без префикса).
	 * @param qname
	 *            - полное имя (с префиксом) - рекомендуется для использования.
	 */
	void handleEndTag(String namespaceURI, String lname, String qname);

	/**
	 * Обработчик содержимого тэга.
	 * 
	 * @param arg0
	 *            - текст.
	 * @param arg1
	 *            - начальная позиция в тексте.
	 * @param arg2
	 *            - длина текста.
	 */
	void handleCharacters(final char[] arg0, final int arg1, final int arg2);
}
