package ru.curs.showcase.app.api.grid;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Типы экспорта в Excel.
 * 
 * @author den
 * 
 */
public enum GridToExcelExportType implements SerializableElement {
	/**
	 * Только текущую страницу.
	 */
	CURRENTPAGE,
	/**
	 * Весь грид.
	 */
	ALL
}
