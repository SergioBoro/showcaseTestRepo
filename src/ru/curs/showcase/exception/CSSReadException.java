package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Ошибка при загрузке или разборе CSS.
 * 
 * @author den
 * 
 */
public class CSSReadException extends BaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Ошибка при загрузке или разборе CSS";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7795606248492441994L;

	public CSSReadException(final Throwable aCause) {
		super(ExceptionType.SOLUTION, ERROR_MES, aCause);
	}

}
