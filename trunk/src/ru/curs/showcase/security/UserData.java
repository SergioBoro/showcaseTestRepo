package ru.curs.showcase.security;

/**
 * Набор атрибутов пользовательских данных.
 * 
 */
public interface UserData {

	/**
	 * Возвращает логин пользователя.
	 * 
	 * @return - логин.
	 */
	String getCaption();

	/**
	 * Возвращает полное имя пользователя.
	 * 
	 * @return - полное имя.
	 */
	String getFullName();

	/**
	 * Возвращает телефон пользователя.
	 * 
	 * @return - телефон.
	 */
	String getPhone();

	/**
	 * Возвращает email пользователя.
	 * 
	 * @return - email.
	 */
	String getEmail();

	/**
	 * Возвращает SID пользователя (при наличии).
	 * 
	 * @return SID.
	 */
	String getSid();

}
