package ru.curs.showcase.exception;

/**
 * Класс исключений, генерируемых при выполнении XSLT-преобразования
 * XMLUtils.xsltTransform.
 */
public class XSLTTransformException extends AbstractShowcaseException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1418394624700606049L;
	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = "Ошибка при выполнении XSLT-преобразования";

	public XSLTTransformException(final Throwable cause) {
		super(ERROR_MES, cause);
	}

	public XSLTTransformException() {
		super(ERROR_MES);
	}

	public XSLTTransformException(final String message, final Throwable cause) {
		super(ERROR_MES + ": " + message, cause);
	}

	public XSLTTransformException(final String message) {
		super(message);
	}
}
