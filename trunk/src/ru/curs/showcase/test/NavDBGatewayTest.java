package ru.curs.showcase.test;

import static org.junit.Assert.assertEquals;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.GeneralXMLHelper;
import ru.curs.showcase.model.navigator.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Класс тестов для шлюза навигатора.
 * 
 * @author den
 * 
 */
public class NavDBGatewayTest extends AbstractTestBasedOnFiles {

	/**
	 * Тест функции получения данных для навигатора.
	 * 
	 */
	@Test
	public void testGetData() throws SAXException, IOException {
		DocumentBuilder builder = XMLUtils.createBuilder();
		Document doc = null;
		NavigatorGateway gw = new NavigatorDBGateway();
		try {
			InputStream xml = gw.getData(new CompositeContext());
			doc = builder.parse(xml);
		} finally {
			gw.releaseResources();
		}
		assertEquals(GeneralXMLHelper.NAVIGATOR_TAG, doc.getDocumentElement().getNodeName());
	}
}
