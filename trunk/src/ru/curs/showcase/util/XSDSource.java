package ru.curs.showcase.util;

import java.io.File;

/**
 * Источник XSD схем.
 * 
 * @author den
 * 
 */
interface XSDSource {

	/**
	 * Возвращает файл схемы.
	 * 
	 * @param fileName
	 *            - имя файла.
	 * @return - файл.
	 */
	File getSchema(String fileName);
}
