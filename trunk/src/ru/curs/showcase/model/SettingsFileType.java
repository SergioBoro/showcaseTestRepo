package ru.curs.showcase.model;

/**
 * Тип Файла настроек приложения.
 * 
 * @author den
 * 
 */
public enum SettingsFileType {
	/**
	 * Файл с путями к другим ресурсам.
	 */
	PATH_PROPERTIES("Файл с путями к другим ресурсам"),
	/**
	 * Файл с общими настройками решения.
	 */
	APP_PROPERTIES("Файл с общими настройками решения"),
	/**
	 * проФайл настроек грида.
	 */
	GRID_PROPERTIES("ПроФайл настроек грида"),
	/**
	 * Файл версии приложения.
	 */
	VERSION("Файл версии приложения"),
	/**
	 * Файл инф. панели.
	 */
	DATAPANEL("Файл инф. панели"),
	/**
	 * Файл навигатора.
	 */
	NAVIGATOR("Файл навигатора"),
	/**
	 * шаблон XForm.
	 */
	XFORM("Файл шаблона XForm"),
	/**
	 * XSL трансформация.
	 */
	XSLT("Файл XSL трансформации"),
	/**
	 * Файл с сообщениями решения для пользователя.
	 */
	SOLUTION_MESSAGES("Файл с сообщениями решения для пользователя"),
	/**
	 * Файл XSD схемы.
	 */
	SCHEMA("Файл XSD схемы");

	/**
	 * Название файла.
	 */
	private String name;

	SettingsFileType(final String aName) {
		name = aName;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}
}
