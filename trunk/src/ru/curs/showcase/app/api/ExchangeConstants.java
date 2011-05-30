package ru.curs.showcase.app.api;

/**
 * Константы, используемые при обмене информацией между клиентом и сервером.
 * 
 * @author den
 * 
 */
public final class ExchangeConstants implements SerializableElement {

	private ExchangeConstants() {
		throw new UnsupportedOperationException();
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6722656736605297948L;

	/**
	 * Префикс для параметров сервлета, содержащих данные файла.
	 */
	public static final String FILE_DATA_PARAM_PREFIX = "@@filedata@@";
}
