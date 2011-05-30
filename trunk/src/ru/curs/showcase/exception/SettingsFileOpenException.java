package ru.curs.showcase.exception;

import ru.curs.showcase.model.SettingsFileType;

/**
 * Исключение при чтении файла с настройками приложения.
 * 
 * @author den
 * 
 */
public class SettingsFileOpenException extends AbstractShowcaseException {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 519101136526014887L;

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = "%s '%s' не найден или поврежден";

	/**
	 * Имя файла.
	 */
	private final String fileName;

	/**
	 * Тип файла.
	 */
	private final SettingsFileType type;

	public SettingsFileOpenException(final Throwable cause, final String aFileName,
			final SettingsFileType aType) {
		super(generateMessage(aFileName, aType), cause);
		fileName = aFileName;
		type = aType;
	}

	public SettingsFileOpenException(final String aFileName, final SettingsFileType aType) {
		super(generateMessage(aFileName, aType));
		fileName = aFileName;
		type = aType;
	}

	private static String generateMessage(final String aFileName,
			final SettingsFileType aType) {
		return String.format(ERROR_MES, aType.getName(), aFileName);
	}

	public String getFileName() {
		return fileName;
	}

	public SettingsFileType getType() {
		return type;
	}
}
