package ru.curs.showcase.exception;

/**
 * Исключение, вызванное тем, что на сервер из клиентской части или из слоя
 * связи с данными передан элемент с некорректным состоянием.
 * 
 * @author den
 * 
 */
public class IncorrectElementException extends AbstractShowcaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES =
		"Получено описание элемента с некорректным состоянием или неподходящий по типу элемент";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2762191427245015158L;

	public IncorrectElementException() {
		super(ERROR_MES);
	}

	public IncorrectElementException(final String message) {
		super(ERROR_MES + ": " + message);
	}

}
