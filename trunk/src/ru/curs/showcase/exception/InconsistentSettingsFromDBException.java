package ru.curs.showcase.exception;


/**
 * Исключение, возникающее при несогласованных настройках элементов, переданных
 * из БД.
 * 
 * @author den
 * 
 */
public final class InconsistentSettingsFromDBException extends AbstractShowcaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Из БД переданы некорректные настройки: ";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 5664191473699810949L;

	public InconsistentSettingsFromDBException(final String aString) {
		super(ERROR_MES + aString);
	}

}
