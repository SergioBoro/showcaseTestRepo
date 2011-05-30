package ru.curs.showcase.test;

import static org.junit.Assert.assertTrue;

import java.io.*;

import org.junit.BeforeClass;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.model.navigator.NavigatorFactory;
import ru.curs.showcase.util.AppProps;

/**
 * Класс абстрактного теста, использующего тестовые файлы с данными.
 * 
 * @author den
 * 
 */
public class AbstractTestBasedOnFiles extends GeneralXMLHelper {

	/**
	 * Идентификатор сессии для модульных тестов.
	 */
	protected static final String TEST_SESSION = "testSession";
	/**
	 * Название каталога, содержащего описания навигаторов в формате XML для
	 * тестовых целей.
	 */
	static final String NAVIGATORSTORAGE = "navigatorstorage";

	/**
	 * Действия, которые должны выполняться перед запуском любых тестовых
	 * классов.
	 */
	@BeforeClass
	public static void beforeClass() {
		AppInitializer.initialize();
		if (AppInfoSingleton.getAppInfo().getUserdatas().size() == 0) {
			AppInitializer.readPathProperties();
		}
		initTestSession();
	}

	private static void initTestSession() {
		AppInfoSingleton.getAppInfo().clearSessions();
		AppInfoSingleton.getAppInfo().addSession(TEST_SESSION);
	}

	public AbstractTestBasedOnFiles() {
		super();
	}

	/**
	 * Возвращает элемент информационной панели для тестов.
	 * 
	 * @param fileName
	 *            - файл панели.
	 * @param tabID
	 *            - идентификатор вкладки.
	 * @param elID
	 *            - идентификатор элемента.
	 * @return элемент.
	 */
	protected DataPanelElementInfo getDPElement(final String fileName, final String tabID,
			final String elID) {
		DataPanelGateway gateway = new DataPanelXMLGateway();
		DataFile<InputStream> file = gateway.getXML(fileName);
		DataPanelFactory dpFactory = new DataPanelFactory();
		DataPanel panel = dpFactory.fromStream(file);
		DataPanelElementInfo element = panel.getTabById(tabID).getElementInfoById(elID);
		assertTrue(element.isCorrect());
		return element;
	}

	/**
	 * Возвращает контекст для тестов из файла навигатора.
	 * 
	 * @param groupID
	 *            - номер группы в файле.
	 * @param elID
	 *            - номер элемента в группе.
	 * @param fileName
	 *            - имя файла с навигатором.
	 * @return - контекст.
	 * @throws IOException
	 */
	protected CompositeContext
			getContext(final String fileName, final int groupID, final int elID)
					throws IOException {
		CompositeContext context =
			getAction(fileName, groupID, elID).getDataPanelLink().getContext();
		return context;
	}

	/**
	 * Возвращает действие для тестов из файла навигатора.
	 * 
	 * @param groupID
	 *            - номер группы в файле.
	 * @param elID
	 *            - номер элемента в группе.
	 * @param fileName
	 *            - имя файла с навигатором.
	 * @return - контекст.
	 * @throws IOException
	 */
	protected Action getAction(final String fileName, final int groupID, final int elID)
			throws IOException {
		InputStream stream1 = AppProps.loadUserDataToStream(NAVIGATORSTORAGE + "\\" + fileName);
		NavigatorFactory navFactory = new NavigatorFactory();
		Navigator nav = navFactory.fromStream(stream1);
		Action action = nav.getGroups().get(groupID).getElements().get(elID).getAction();
		return action;
	}

}