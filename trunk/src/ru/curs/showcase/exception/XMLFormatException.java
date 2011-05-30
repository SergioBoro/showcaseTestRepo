package ru.curs.showcase.exception;

/**
 * Ошибка получения XML документа из XML поля.
 * 
 * @author den
 * 
 */
public class XMLFormatException extends AbstractShowcaseException {

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = "Ошибка при разборе XML данных";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7758790066616498408L;

	public XMLFormatException() {
		super(ERROR_MES);
	}

	public XMLFormatException(final String fileName, final Throwable cause) {
		super(ERROR_MES + ": " + fileName, cause);
	}

	public XMLFormatException(final String fileName) {
		super(ERROR_MES + ": " + fileName);
	}
}
