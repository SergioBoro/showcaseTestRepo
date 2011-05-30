package ru.curs.showcase.exception;

/**
 * Исключение, возникающее при отсутствии необходимого параметра при GET или
 * POST запросе к серверу.
 * 
 * @author den
 * 
 */
public class HTTPRequestRequiredParamAbsentException extends AbstractShowcaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Не передан обязательный параметр: ";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -3430283606302382887L;

	public HTTPRequestRequiredParamAbsentException(final String param) {
		super(ERROR_MES + param);
	}

}
