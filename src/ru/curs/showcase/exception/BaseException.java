package ru.curs.showcase.exception;

import org.slf4j.*;

import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Базовый класс серверных исключений Showcase. Наследуется от RuntimeException,
 * а не просто Exception, как можно было бы ожидать, по следующей причине.
 * Exception является контролируемым исключением, которое разработчики Java
 * рекомендуют использовать для обработки ошибок в пользовательских данных. Но
 * при широком использовании контролируемых исключений совместно с
 * использованием наследования определения функций очень разрастаются. К тому же
 * это приводит к ситуациям, когда исключения дочернего класса приходится
 * объявлять в родительском, что не есть хорошо. Поэтому было принято решение
 * минимизировать число контролируемых исключений в серверной части Showcase.
 * 
 * @author den
 * 
 */
public abstract class BaseException extends RuntimeException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6824298988092385401L;

	static final String ERROR_CAPTION = "Сообщение об ошибке";

	/**
	 * Тип исключения.
	 */
	private ExceptionType type;
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseException.class);

	public BaseException(final ExceptionType aType, final Throwable aCause) {
		super(aCause);
		type = aType;
		logAll(this);
	}

	public BaseException(final ExceptionType aType) {
		super();
		type = aType;
		logAll(this);
	}

	public BaseException(final ExceptionType aType, final String aMessage) {
		super(aMessage);
		type = aType;
		logAll(this);
	}

	public BaseException(final ExceptionType aType, final String aMessage, final Throwable aCause) {
		super(aMessage, aCause);
		type = aType;
		logAll(this);
	}

	/**
	 * Выводит в лог полную информацию об исключении.
	 * 
	 * @param e
	 *            - исключение.
	 */
	public void logAll(final Throwable e) {
		String formatedMes = ERROR_CAPTION;
		LOGGER.error(formatedMes, e);
	}

	public ExceptionType getType() {
		return type;
	}

	public void setType(final ExceptionType aType) {
		type = aType;
	}

	/**
	 * Возвращает текст оригинальной ошибки в случае, если ошибка Showcase
	 * базируется на другой ошибке.
	 * 
	 * @return - текст ошибки.
	 */
	public String getOriginalMessage() {
		if (getCause() != null) {
			return getCause().getLocalizedMessage();
		}
		return null;
	}

	public String getMainMessage() {
		return getLocalizedMessage();
	}
}
