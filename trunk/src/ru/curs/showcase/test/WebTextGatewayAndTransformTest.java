package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.exception.IncorrectElementException;
import ru.curs.showcase.model.HTMLBasedElementRawData;
import ru.curs.showcase.model.webtext.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Тест для WebTextDBGateway.
 * 
 * @author den
 * 
 */
public class WebTextGatewayAndTransformTest extends AbstractTestBasedOnFiles {
	/**
	 * Основной тест для проверки работы WebTextDBGateway.
	 * 
	 */
	@Test
	public void testGetData() throws GeneralServerException, IOException {
		CompositeContext context = getContext("tree_multilevel.v2.xml", 1, 0);
		DataPanelElementInfo element = getDPElement("test2.xml", "1", "1");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		WebText wt = serviceLayer.getWebText(context, element);

		assertNotNull(context.getSession());
		assertEquals(0, wt.getEventManager().getEvents().size());
		assertNull(wt.getDefaultAction());
		assertNotNull(wt.getData());
	}

	/**
	 * Тест на выборку событий и действия по умолчанию из БД.
	 * 
	 */
	@Test
	public void testEventsAndDefAction() throws GeneralServerException, IOException {
		CompositeContext context = getContext("tree_multilevel.v2.xml", 1, 0);
		DataPanelElementInfo element = getDPElement("test2.xml", "1", "3");
		CompositeContext clonedContext = context.gwtClone();

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		WebText wt = serviceLayer.getWebText(clonedContext, element);

		assertEquals(1, wt.getEventManager().getEvents().size());
		assertEquals("0", wt.getEventManager().getEvents().get(0).getId1());

		Action action = wt.getEventManager().getEvents().get(0).getAction();
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		stdCheckAction(context, action);

		action = wt.getDefaultAction();
		assertNotNull(action);
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		stdCheckAction(context, action);

		assertNotNull(wt.getData());
	}

	private void stdCheckAction(final CompositeContext context, final Action action) {
		assertEquals(NavigatorActionType.DO_NOTHING, action.getNavigatorActionType());
		assertEquals(context, action.getDataPanelLink().getContext());
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals(context, action.getDataPanelLink().getElementLinks().get(0).getContext());
		assertNull(action.getDataPanelLink().getContext().getSession());
	}

	/**
	 * Тест для случая, когда не задана хранимая процедура, возвращающая данные.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testGetStaticDataByXSLT() throws GeneralServerException {
		DataPanelElementInfo el = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		CompositeContext context = new CompositeContext();
		el.setTransformName("bal_test.xsl");
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		WebText wt = serviceLayer.getWebText(context, el);

		assertTrue(wt.getData().startsWith("<h3>Здесь находится просто статический текст</h3>"));
		assertTrue(wt.getData().endsWith(
				"<p>Коля у Светы спёр кассеты, а Света у Коли уперла костет</p>"));
	}

	/**
	 * Тест на случай, когда не задано преобразование.
	 * 
	 */
	@Test
	public void testGetStaticDataBySP() throws IOException {
		String prefix = "<root>";
		CompositeContext context = getContext("tree_multilevel.v2.xml", 1, 0);
		DataPanelElementInfo element = getDPElement("test2.xml", "1", "2");

		WebTextGateway wtgateway = new WebTextDBGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(context, element);
		String out = XMLUtils.xsltTransform(rawWT.getData(), null);
		new WebText(out);
		assertTrue(out.startsWith(prefix));
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement1() {
		DataPanelElementInfo element =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);

		WebTextGateway wtgateway = new WebTextDBGateway();
		wtgateway.getRawData(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement2() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", null);
		element.setProcName("proc");

		WebTextGateway wtgateway = new WebTextDBGateway();
		wtgateway.getRawData(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement3() {
		WebTextGateway wtgateway = new WebTextDBGateway();
		wtgateway.getRawData(null, null);
	}
}
