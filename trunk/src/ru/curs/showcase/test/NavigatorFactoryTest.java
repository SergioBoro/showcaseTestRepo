package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.navigator.*;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.navigator.NavigatorFactory;
import ru.curs.showcase.util.AppProps;

/**
 * Класс для тестирования фабрики навигаторов.
 * 
 * @author den
 * 
 */
public class NavigatorFactoryTest extends AbstractTestBasedOnFiles {

	/**
	 * Проверка навигатора, созданного обычным конструктором.
	 */
	@Test
	public void testContructor() {
		Navigator nav = new Navigator();
		assertFalse(nav.getHideOnLoad());
		assertNotNull(nav.getGroups());
		assertEquals(0, nav.getGroups().size());
		assertNull(nav.getGroupById("fake"));
		assertNull(nav.getAutoSelectElement());
		assertEquals("300px", nav.getWidth());
	}

	/**
	 * Тест навигатора, построенного на основе данных из БД.
	 * 
	 */
	@Test
	public void testNavigatorFromDBBySL() throws GeneralServerException {
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Navigator nav = serviceLayer.getNavigator();
		assertFalse(nav.getHideOnLoad());
		assertEquals("300px", nav.getWidth());
	}

	/**
	 * Число элементов в первой группе навигатора.
	 */
	private static final int FIRST_GRP_ELEMENTS_COUNT = 16;

	/**
	 * Тест навигатора, построенного на основе данных из файла XML.
	 * 
	 */
	@Test
	public void testNavigatorFromFile() throws IOException {
		NavigatorFactory factory = new NavigatorFactory();
		InputStream stream =
			AppProps.loadUserDataToStream(NAVIGATORSTORAGE + "/tree_multilevel.xml");

		Navigator nav = factory.fromStream(stream);
		assertEquals("200px", nav.getWidth());
		assertTrue(nav.getHideOnLoad());
		assertEquals(nav.getGroups().get(0).getElements().get(1), nav.getAutoSelectElement());
		final NavigatorGroup firstGroup = nav.getGroupById("1");
		assertEquals("C6CC2BA4-A6ED-4630-8E58-CBBFA9C8C0A9", firstGroup.getElements().get(1)
				.getId());
		assertEquals("1", firstGroup.getId());
		assertEquals("Балансы зерна", firstGroup.getName());
		assertEquals("resources/group_icon_default.png", firstGroup.getImageId());
		assertEquals(FIRST_GRP_ELEMENTS_COUNT, firstGroup.getElements().size());
		assertEquals(0, firstGroup.getElements().get(0).getElements().size());
		assertEquals(1, firstGroup.getElements().get(1).getElements().size());
		assertNotNull(firstGroup.getElements().get(0).getAction());
		NavigatorElement testEl = firstGroup.getElements().get(1);
		assertNotNull(testEl.getAction());
		assertEquals(DataPanelActionType.RELOAD_PANEL, testEl.getAction().getDataPanelActionType());
		assertEquals(NavigatorActionType.DO_NOTHING, testEl.getAction().getNavigatorActionType());
		assertEquals("C6CC2BA4-A6ED-4630-8E58-CBBFA9C8C0A8", testEl.getElements().get(0).getId());
		assertEquals("Вывоз, включая экспорт - Ноябрь", testEl.getElements().get(0).getName());
		assertNotNull(testEl.getElements().get(0).getAction());
		DataPanelLink link = testEl.getElements().get(0).getAction().getDataPanelLink();
		assertNotNull(link);
		assertEquals("test.xml", link.getDataPanelId());
		assertEquals("1", link.getTabId());
		assertNotNull(testEl.getElements().get(0).getAction().getDataPanelLink().getContext());
		assertEquals("Вывоз, включая экспорт - Ноябрь", link.getContext().getMain());
		assertNull(link.getContext().getAdditional());
		assertEquals(1, link.getElementLinks().size());
		assertEquals("1", link.getElementLinks().get(0).getId());
		assertNotNull(link.getElementLinks().get(0).getContext());
		assertEquals("Вывоз, включая экспорт - Ноябрь", link.getElementLinks().get(0).getContext()
				.getMain());
		assertNull(link.getElementLinks().get(0).getContext().getSession());
		assertEquals("(1=1)", link.getElementLinks().get(0).getContext().getAdditional());
	}
}
