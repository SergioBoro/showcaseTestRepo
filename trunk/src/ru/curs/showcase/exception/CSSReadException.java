package ru.curs.showcase.exception;

/**
 * Ошибка при загрузке или разборе CSS.
 * 
 * @author den
 * 
 */
public class CSSReadException extends AbstractShowcaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Ошибка при загрузке или разборе CSS";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7795606248492441994L;

	public CSSReadException(final Throwable aCause) {
		super(ERROR_MES, aCause);
	}

}
