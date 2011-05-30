package ru.curs.showcase.model;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.*;

/**
 * Абстрактная фабрика с шаблонным методом построения сложных объектов -
 * наследников FormattedDataPanelElement. Используется для построения таких
 * объектов, как грид, график, карта.
 * 
 * @author den
 * 
 */
public abstract class TemplateMethodFactory extends GeneralXMLHelper {

	public TemplateMethodFactory(final ElementRawData aSource) {
		source = aSource;
	}

	protected final InputStream getSettings() {
		return source.getProperties();
	}

	public final CompositeContext getCallContext() {
		return source.getCallContext();
	}

	/**
	 * Исходные сырые данные для построения элемента.
	 */
	private final ElementRawData source;

	public ElementRawData getSource() {
		return source;
	}

	/**
	 * Функция, возвращающая результат работы фабрики.
	 * 
	 * @return - результат работы.
	 */
	public abstract DataPanelElement getResult();

	/**
	 * Основная функция построения объекта на основе данных, уже содержащихся в
	 * фабрике.
	 * 
	 * @return - грид.
	 */
	public DataPanelElement build() throws Exception {
		initResult();
		prepareData();
		prepareSettings();
		releaseResources();
		setupDynamicSettings();
		fillResultByData();
		getResult().actualizeActions(getCallContext());
		correctSettingsAndData();
		return getResult();
	}

	/**
	 * Загружает динамические настройки результата из settings.
	 * 
	 */
	protected abstract void setupDynamicSettings();

	/**
	 * Метод, в котором происходит загрузка в результат динамических данных.
	 * 
	 * @throws SQLException
	 */
	protected abstract void fillResultByData() throws SQLException;

	/**
	 * Выполняет настройку свойств результата работы фабрики на основании
	 * динамических данных, загруженных в методе fillResultByData. По умолчанию
	 * ничего не делает - т.к. данный функционал не всегда нужен.
	 */
	protected void correctSettingsAndData() {

	}

	/**
	 * Функция для инициализации результата работы фабрики. В данной функции
	 * создается объект, а также настраиваются его свойства по умолчанию.
	 * 
	 */
	protected abstract void initResult();

	/**
	 * Функция подготовки данных для создания объекта.
	 * 
	 * @throws ResultSetHandleException
	 * @throws DBQueryException
	 */
	protected abstract void prepareData();

	/**
	 * Функция подготовки настроек для создания объекта.
	 * 
	 */
	protected abstract void prepareSettings();

	/**
	 * Функция освобождения ресурсов, необходимых для создания объекта.
	 * 
	 */
	protected abstract void releaseResources();

	public DataPanelElementInfo getElementInfo() {
		return source.getElementInfo();
	}
}
