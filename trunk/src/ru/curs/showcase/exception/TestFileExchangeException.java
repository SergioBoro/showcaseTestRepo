package ru.curs.showcase.exception;

/**
 * Ошибка при чтении тестового файла.
 * 
 * @author den
 * 
 */
public final class TestFileExchangeException extends AbstractShowcaseException {

	static final String ERROR_MES = "Ошибка при чтении тестового файла: ";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6810662604700277735L;

	public TestFileExchangeException(final String error, final Throwable aCause) {
		super(ERROR_MES + error, aCause);
	}

}
