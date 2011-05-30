package ru.curs.showcase.model;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Реестр, хранящий различные глобальные объекты, настраиваемые с помощью Spring
 * IoC.
 * 
 * @author den
 * 
 */
public final class AppRegistry {

	private AppRegistry() {
		throw new UnsupportedOperationException();
	}

	/**
	 * String контекст с логикой Showcase.
	 */
	private static ApplicationContext context = new ClassPathXmlApplicationContext("logic.xml");
	/**
	 * Модуль поиска вкладки инф. панели для действия.
	 */
	private static ActionTabFinder actionTabFinder = null;

	/**
	 * Возвращает синглетон-реестр.
	 * 
	 * @return AppRegistry.
	 */
	public static ActionTabFinder getActionTabFinder() {
		if (actionTabFinder == null) {
			actionTabFinder = (ActionTabFinder) context.getBean("actionTabFinder");
		}
		return actionTabFinder;
	}
}
