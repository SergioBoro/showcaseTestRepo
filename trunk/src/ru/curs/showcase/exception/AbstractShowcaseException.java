package ru.curs.showcase.exception;

import org.slf4j.*;

/**
 * Абстрактный класс исключения Showcase. Исключение - это ошибка в
 * пользовательских данных, ошибка работы с внешними файлами или приложениями.
 * Наследуется от RuntimeException, а не просто Exception, как можно было бы
 * ожидать, по следующей причине. Exception является контролируемым исключением,
 * которое разработчики Java рекомендуют использовать для обработки ошибок в
 * пользовательских данных. Но при широком использовании контролируемых
 * исключений совместно с использованием наследования определения функций очень
 * разрастаются. К тому же это приводит к ситуациям, когда исключения дочернего
 * класса приходится объявлять в родительском, что не есть хорошо. Поэтому было
 * принято решение минимизировать число контролируемых исключений в серверной
 * части Showcase.
 * 
 * @author den
 * 
 */
public abstract class AbstractShowcaseException extends RuntimeException implements
		AbstractShowcaseThrowable {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractShowcaseException.class);

	protected static final String ERROR_CAPTION = "Сообщение об ошибке";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7814413593983790922L;

	@Override
	public final String getOriginalMessage() {
		if (getCause() != null) {
			return getCause().getLocalizedMessage();
		}
		return null;
	}

	public AbstractShowcaseException() {
		super();
		logAll(this);
	}

	public AbstractShowcaseException(final String message, final Throwable cause) {
		super(message, cause);
		logAll(this);
	}

	public AbstractShowcaseException(final String message) {
		super(message);
		logAll(this);
	}

	public AbstractShowcaseException(final Throwable cause) {
		super(cause);
		logAll(this);
	}

	@Override
	public void logAll(final Throwable e) {
		String formatedMes = ERROR_CAPTION;
		LOGGER.error(formatedMes, e);
	}
}
