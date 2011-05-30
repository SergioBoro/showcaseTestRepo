package ru.curs.showcase.exception;

import org.slf4j.*;

/**
 * Класс абстрактной ошибки Showcase. Ошибка - это ситуация, которой не должно
 * быть, если бы программа работала как запланировано. Может быть связана с
 * ошибками Java, нехваткой памяти или ошибками разработчиков Showcase.
 * 
 * @author den
 * 
 */
public abstract class AbstractShowcaseError extends RuntimeException implements
		AbstractShowcaseThrowable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6824298988092385401L;

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractShowcaseError.class);

	protected static final String ERROR_CAPTION = "Сообщение об ошибке";

	public AbstractShowcaseError(final Throwable aCause) {
		super(aCause);
		logAll(this);
	}

	public AbstractShowcaseError() {
		super();
		logAll(this);
	}

	public AbstractShowcaseError(final String aMessage) {
		super(aMessage);
		logAll(this);
	}

	public AbstractShowcaseError(final String aMessage, final Throwable aCause) {
		super(aMessage, aCause);
		logAll(this);
	}

	@Override
	public String getOriginalMessage() {
		if (getCause() != null) {
			return getCause().getLocalizedMessage();
		}
		return null;
	}

	@Override
	public void logAll(final Throwable e) {
		String formatedMes = ERROR_CAPTION;
		LOGGER.error(formatedMes, e);
	}

}
