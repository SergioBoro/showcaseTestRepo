package ru.curs.showcase.exception;

/**
 * Ошибка соединения с БД.
 * 
 * @author den
 * 
 */
public class DBConnectException extends AbstractShowcaseException {

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = "Ошибка при соединении с БД";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7586686198028153113L;

	public DBConnectException() {
		super(ERROR_MES);
	}

	public DBConnectException(final Throwable cause) {
		super(ERROR_MES, cause);
	}

}
