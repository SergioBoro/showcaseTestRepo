package ru.curs.showcase.model;

import java.io.ByteArrayOutputStream;

/**
 * Класс файла Excel. Включает содержимое файла и его имя.
 * 
 * @author den
 * 
 */
public class ExcelFile extends DataFile<ByteArrayOutputStream> {
	/**
	 * Имя результирующего файла по умолчанию.
	 */
	private static final String DEF_FILENAME = "table";

	/**
	 * Расширение генерируемого Excel файла. Используем xls вместо xml для
	 * корректной обработки браузерами и Windows на компьютерах обычных
	 * пользователей.
	 */
	private static final String FILEEXT = "xls";

	public ExcelFile(final ByteArrayOutputStream aData) {
		super(aData, String.format("%s.%s", DEF_FILENAME, FILEEXT));
	}

}
