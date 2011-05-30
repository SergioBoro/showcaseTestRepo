package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.XForms;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.HTMLBasedElementRawData;
import ru.curs.showcase.model.xform.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Тесты для фабрики XForms.
 * 
 * @author den
 * 
 */
public class XFormsFactoryTest extends AbstractTestBasedOnFiles {

	/**
	 * Тест на создание фабрики.
	 * 
	 */
	@Test
	public void testXFormsFactory() throws IOException {
		createFactory();
	}

	/**
	 * Test method for
	 * {@link ru.curs.showcase.model.xform.XFormsDBFactory#build()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuild() throws Exception {
		XFormsDBFactory factory = createFactory();
		factory.build();
	}

	/**
	 * Тест функции получения XForms из сервисного уровня.
	 * 
	 */
	@Test
	public void testServiceLayer() throws IOException, GeneralServerException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "08");

		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		XForms xforms = sl.getXForms(context, element, null);

		assertNotNull(context.getSession());
		Action action = xforms.getActionForDependentElements();
		assertNotNull(action);
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId());
		assertEquals("xforms default action", action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getAdditional());

		assertEquals(2, xforms.getEventManager().getEvents().size());
		action = xforms.getEventManager().getEvents().get(0).getAction();
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId());
		assertEquals("save click on xforms (with filtering)", action.getDataPanelLink()
				.getElementLinks().get(0).getContext().getAdditional());

		assertNotNull(xforms.getXFormParts());
		assertTrue(xforms.getXFormParts().size() > 0);
	}

	/**
	 * Проверяет сериализацию текущего контекста в XML.
	 */
	@Test
	public void testContextToXML() {
		CompositeContext context = CompositeContext.createCurrent();
		Document doc = XMLUtils.objectToXML(context);
		assertEquals("context", doc.getDocumentElement().getNodeName());
		final int contextsCount = 2;
		assertEquals(contextsCount, doc.getDocumentElement().getChildNodes().getLength());
		assertEquals("additional", doc.getDocumentElement().getChildNodes().item(0).getNodeName());
		assertEquals("main", doc.getDocumentElement().getChildNodes().item(1).getNodeName());
		CompositeContext context2 =
			(CompositeContext) XMLUtils.xmlToObject(doc.getDocumentElement(),
					CompositeContext.class);
		assertTrue(context.equals(context2));
	}

	/**
	 * Тестирования сериализации информации об элементе панели в XML.
	 * 
	 */
	@Test
	public void testDPElementInfoToXML() {
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "08");
		Document doc = XMLUtils.objectToXML(element);
		assertEquals("element", doc.getDocumentElement().getNodeName());
		DataPanelElementInfo el2 =
			(DataPanelElementInfo) XMLUtils.xmlToObject(doc.getDocumentElement(),
					DataPanelElementInfo.class);
		assertTrue(element.equals(el2));
	}

	private XFormsDBFactory createFactory() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "08");

		XFormsGateway gateway = new XFormsDBGateway();
		HTMLBasedElementRawData raw = gateway.getInitialData(context, element);
		return new XFormsDBFactory(raw);
	}
}
