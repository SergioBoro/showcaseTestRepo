package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Исключение, генерируемое в случае, когда вместо XML файла на сервер приходят
 * данные не в XML формате.
 * 
 * @author den
 * 
 */
public class NotXMLException extends BaseException {

	static final String ERROR_MES = "Файл '%s' не является файлом в формате XML";

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7588997404313831091L;

	/**
	 * Имя файла.
	 */
	private final String fileName;

	public String getFileName() {
		return fileName;
	}

	public NotXMLException(final Throwable throwable, final String aFileName) {
		super(ExceptionType.USER, String.format(ERROR_MES, aFileName));
		fileName = aFileName;
	}
}
