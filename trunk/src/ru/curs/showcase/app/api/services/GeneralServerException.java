package ru.curs.showcase.app.api.services;

import ru.curs.showcase.app.api.SolutionMessage;

/**
 * Класс общего серверного исключения. Данное исключение передается на GWT
 * клиент вместо любого конкретного исключения. Сообщение данного исключения
 * копирует
 * 
 * @author den
 * 
 */
public class GeneralServerException extends Exception {
	/**
	 * Разделитель строк для сообщений, показываемых пользователю (используется
	 * разделитель Windows).
	 */
	private static final String LINE_SEPARATOR = "\r\n";

	static final String EXCEPTION_TRACE = "Exception trace:\r\n";

	static final String EXCEPTION_CLASS = "ExceptionClass: ";

	static final String ORIGINAL_MESSAGE = "OriginalMessage: ";

	/**
	 * Заголовок в полном стеке исключения перед описанием исключения-источника.
	 */
	private static final String CAUSE_EXC_CAPTION = "Источник ошибки";

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -5928650256788448347L;

	/**
	 * Класс оригинального исключения.
	 */
	private String originalExceptionClass;

	/**
	 * Стек оригинального исключения.
	 */
	private String originalTrace;

	/**
	 * Оригинальное сообщение об ошибке. Задается в случае, если ошибка Showcase
	 * базируется на другой ошибке.
	 */
	private String originalMessage;

	/**
	 * Сообщение, выдаваемое пользователю.
	 */
	private SolutionMessage solutionMessage;

	public boolean isSolutionMessage() {
		return (solutionMessage != null);
	}

	public final String getOriginalMessage() {
		return originalMessage;
	}

	public final void setOriginalMessage(final String aOriginalMessage) {
		originalMessage = aOriginalMessage;
	}

	public GeneralServerException() {
		super();
	}

	public GeneralServerException(final Throwable original, final String aOriginalMessage,
			final SolutionMessage aSolutionMessage) {
		super(getMessageText(original), original);

		originalExceptionClass = original.getClass().getName();
		originalTrace = getStackText(original);
		originalMessage = aOriginalMessage;
		solutionMessage = aSolutionMessage;
	}

	private static String getMessageText(final Throwable original) {
		if (original.getLocalizedMessage() != null) {
			return original.getLocalizedMessage();
		} else {
			return original.getClass().getName();
		}
	}

	/**
	 * 
	 * Возвращает текст стэка исключения.
	 * 
	 * @param original
	 *            - Throwable
	 * @return String
	 */
	public static String getStackText(final Throwable original) {
		StringBuilder result = new StringBuilder();
		String ls = LINE_SEPARATOR; // System.getProperty("line.separator"); -
									// не
		// работает в gwt

		for (StackTraceElement el : original.getStackTrace()) {
			result.append(el.toString() + ls);
		}
		if (original.getCause() != null) {
			result.append(CAUSE_EXC_CAPTION + ls);
			result.append(original.getCause().getStackTrace()[0].toString() + ls);
			if (original.getCause().getStackTrace().length > 1) {
				result.append(original.getCause().getStackTrace()[1].toString() + ls);
			}
		}
		return result.toString();
	}

	public final String getOriginalExceptionClass() {
		return originalExceptionClass;
	}

	public final void setOriginalExceptionClass(final String aOriginalExceptionClass) {
		originalExceptionClass = aOriginalExceptionClass;
	}

	public final String getOriginalTrace() {
		return originalTrace;
	}

	public final void setOriginalTrace(final String aOriginalTrace) {
		originalTrace = aOriginalTrace;
	}

	/**
	 * Возвращает подробный текст сообщения об ошибке.
	 * 
	 * @return текст сообщения
	 */
	public final String getDetailedTextOfException() {
		String str = null;
		str = ORIGINAL_MESSAGE + this.getOriginalMessage() + LINE_SEPARATOR + LINE_SEPARATOR;
		str =
			str + EXCEPTION_CLASS + this.getOriginalExceptionClass() + LINE_SEPARATOR
					+ LINE_SEPARATOR;
		str = str + EXCEPTION_TRACE + this.getOriginalTrace();
		return str;
	}

	/**
	 * Проверка caught на то что он является экземпляром класса
	 * GeneralServerException и получение подробного текста сообщения об ошибке.
	 * 
	 * @param caught
	 *            - исключение.
	 * @return текст сообщения
	 */
	public static String checkExeptionTypeAndCreateDetailedTextOfException(final Throwable caught) {
		String str = null;
		if (caught instanceof GeneralServerException) {
			return ((GeneralServerException) caught).getDetailedTextOfException();
		} else {
			str = ORIGINAL_MESSAGE + caught.getMessage() + LINE_SEPARATOR + LINE_SEPARATOR;
			str = str + EXCEPTION_CLASS + caught.getClass() + LINE_SEPARATOR + LINE_SEPARATOR;
			str = str + EXCEPTION_TRACE + getStackText(caught);
			return str;
		}

	}

	public SolutionMessage getSolutionMessage() {
		return solutionMessage;
	}

	public void setSolutionMessage(final SolutionMessage aSolutionMessage) {
		solutionMessage = aSolutionMessage;
	}

}
