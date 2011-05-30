package ru.curs.showcase.app.api;

/**
 * Интерфейс для "тупого" (ручной перебор всех требуемых атрибутов) клонирования
 * объекта, работающего в gwt. Заглушка до тех пор, пока в GWT не будет
 * официальной реализации clone.
 * 
 * @author den
 * 
 */
public interface GWTClonable {

	/**
	 * Функция клонирования.
	 * 
	 * @return - объект-копия.
	 */
	Object gwtClone();
}
