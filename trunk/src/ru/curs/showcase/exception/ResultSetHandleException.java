package ru.curs.showcase.exception;

/**
 * Ошибка при работе с полученным из БД ResultSet. Может быть вызвана потерей
 * соединения с сервером.
 * 
 * @author den
 * 
 */
public class ResultSetHandleException extends AbstractShowcaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Ошибка при работе с полученными из БД данными";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4089202125257954531L;

	public ResultSetHandleException() {
		super(ERROR_MES);
	}

	public ResultSetHandleException(final Throwable aCause) {
		super(ERROR_MES, aCause);
	}

	public ResultSetHandleException(final String aString) {
		super(aString);
	}

}
