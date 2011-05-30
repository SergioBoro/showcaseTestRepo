package ru.curs.showcase.model;

/**
 * Тип события в SAX парсере.
 * 
 * @author den
 * 
 */
public enum SaxEventType {
	/**
	 * Начало тэга.
	 */
	STARTELEMENT,
	/**
	 * Конец тэга.
	 */
	ENDELEMENT,
	/**
	 * Содержимое тэга.
	 */
	CHARACTERS

}
