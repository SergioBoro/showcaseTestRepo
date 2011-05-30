package ru.curs.showcase.exception;

/**
 * Класс внутренней ошибки Showcase. Используется для перехвата исключений,
 * которых "не должно быть".
 * 
 * @author den
 * 
 */
public class AppInternalError extends AbstractShowcaseError {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 4193893671079202405L;

	public AppInternalError() {
		super();
	}

	public AppInternalError(final String aMessage) {
		super(aMessage);
	}

	public AppInternalError(final Exception ex) {
		super(ex);
	}

}
