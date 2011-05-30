package ru.curs.showcase.exception;

/**
 * Специальный класс ошибки для передачи наверх ошибки в обработчике SAX
 * парсера.
 * 
 * @author den
 * 
 */
public final class SAXError extends AbstractShowcaseError {

	public SAXError(final Throwable aCause) {
		super(aCause);
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 5024218668352683986L;

}
