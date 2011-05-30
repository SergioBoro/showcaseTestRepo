package ru.curs.showcase.model.navigator;

import java.io.InputStream;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Интерфейс шлюза данных для навигатора. На основе данного интерфейса может
 * быть сделаны реализация, возвращающая данные из БД или тестовая заглушка.
 * 
 * @author den
 * 
 */
public interface NavigatorGateway {

	/**
	 * Функция возврата данных для навигатора.
	 * 
	 * @return - данные в формате xml.
	 * @param aContext
	 *            - контекст.
	 */
	InputStream getData(CompositeContext aContext);

	/**
	 * Функция принудительно освобождает ресурсы, используемые шлюзом для
	 * получения данных. Должна быть вызвана после работы фабрики по построению
	 * навигатора.
	 * 
	 */
	void releaseResources();
}