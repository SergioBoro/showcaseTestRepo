package ru.curs.showcase.exception;

/**
 * Базовый интерфейс для собственных ошибок и исключений Showcase.
 * 
 * @author den
 * 
 */
public interface AbstractShowcaseThrowable {

	/**
	 * Возвращает текст оригинальной ошибки в случае, если ошибка Showcase
	 * базируется на другой ошибке.
	 * 
	 * @return - текст ошибки.
	 */
	String getOriginalMessage();

	/**
	 * Выводит в лог полную информацию об исключении.
	 * 
	 * @param e
	 *            - исключение.
	 */
	void logAll(final Throwable e);
}
