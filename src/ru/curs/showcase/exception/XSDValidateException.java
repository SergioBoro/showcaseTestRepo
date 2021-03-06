package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Класс исключений, генерируемых при выполнении XSD-проверки
 * XMLUtils.xsdValidate.
 */
public class XSDValidateException extends BaseException {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4848505926999363486L;

	/**
	 * Расширенный текст ошибки.
	 */
	private static final String EXT_ERROR_MES = "Документ '%s' не соответствует схеме '%s'";

	/**
	 * Обычный текст ошибки.
	 */
	private static final String ERROR_MES = "Документ не соответствует схеме '%s'";

	public XSDValidateException(final Throwable cause, final String subject, final String schema) {
		super(ExceptionType.SOLUTION, String.format(EXT_ERROR_MES, subject, schema), cause);
	}

	public XSDValidateException(final Throwable cause, final String schema) {
		super(ExceptionType.SOLUTION, String.format(ERROR_MES, schema), cause);
	}

	public XSDValidateException(final ExceptionType exType, final Throwable cause,
			final String subject, final String schema) {
		super(exType, String.format(EXT_ERROR_MES, subject, schema), cause);
	}

	public XSDValidateException(final ExceptionType exType, final Throwable cause,
			final String schema) {
		super(exType, String.format(ERROR_MES, schema), cause);
	}
}
