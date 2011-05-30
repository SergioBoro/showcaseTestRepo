package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.*;

import ru.curs.gwt.datagrid.model.GridValueType;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.grid.GridXMLBuilder;

/**
 * Тесты для функции экспорта в Excel из грида.
 * 
 * @author den
 * 
 */
public class GridExportToExcelTest extends AbstractTestBasedOnFiles {
	/**
	 * Тест экспорта данных из текущей страницы.
	 * 
	 * @throws IOException
	 * @throws GeneralServerException
	 */
	@Test
	public void testExportCurrentPage() throws IOException, GeneralServerException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "2", "2");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, element, null);

		GridXMLBuilder builder = new GridXMLBuilder(grid);
		Document xml = builder.build();

		assertEquals(GridXMLBuilder.TABLE_TAG, xml.getDocumentElement().getNodeName());

		assertEquals(GridXMLBuilder.COLUMN_TAG, xml.getDocumentElement().getFirstChild()
				.getNodeName());
		assertNull(xml.getDocumentElement().getFirstChild().getFirstChild());
		assertNotNull(xml.getDocumentElement().getFirstChild().getAttributes()
				.getNamedItem(GeneralXMLHelper.WIDTH_TAG));

		// header row test above
		NodeList list = xml.getDocumentElement().getElementsByTagName(GridXMLBuilder.ROW_TAG);
		assertTrue(list.getLength() > 0);
		assertEquals(GridValueType.STRING.toStringForExcel(), list.item(0).getFirstChild()
				.getAttributes().getNamedItem(GeneralXMLHelper.TYPE_TAG).getNodeValue());

		assertEquals(GridXMLBuilder.ROW_TAG, xml.getDocumentElement().getLastChild().getNodeName());
		assertEquals(GridXMLBuilder.CELL_TAG, xml.getDocumentElement().getLastChild()
				.getFirstChild().getNodeName());
		assertNotNull(xml.getDocumentElement().getLastChild().getFirstChild().getFirstChild()
				.getNodeValue());
		assertNotNull(xml.getDocumentElement().getLastChild().getFirstChild().getAttributes()
				.getNamedItem(GeneralXMLHelper.TYPE_TAG));
		assertEquals(GridValueType.STRING.toStringForExcel(),
				xml.getDocumentElement().getLastChild().getFirstChild().getAttributes()
						.getNamedItem(GeneralXMLHelper.TYPE_TAG).getNodeValue());
		assertEquals(GridValueType.INT.toStringForExcel(), xml.getDocumentElement().getLastChild()
				.getLastChild().getAttributes().getNamedItem(GeneralXMLHelper.TYPE_TAG)
				.getNodeValue());
	}

	/**
	 * Тест экспорта всех страниц используя ServiceLayer.
	 * 
	 * @throws GeneralServerException
	 * @throws IOException
	 */
	@Test
	public void testServiceForExportAll() throws GeneralServerException, IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "2", "2");
		GridRequestedSettings settings = new GridRequestedSettings();

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		ExcelFile file =
			serviceLayer.generateExcelFromGrid(GridToExcelExportType.ALL, context, element,
					settings, null);
		assertNotNull(context.getSession()); // побочный эффект - нет clone
		assertNotNull(file.getData());
		assertEquals("table.xls", file.getName());
	}

	/**
	 * Тест экспорта текущей страницы используя ServiceLayer.
	 * 
	 * @throws GeneralServerException
	 * @throws IOException
	 */
	@Test
	public void testServiceForExportCurrent() throws GeneralServerException, IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "2", "2");
		GridRequestedSettings settings = new GridRequestedSettings();
		settings.setPageNumber(1);
		settings.setPageSize(2);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		serviceLayer.generateExcelFromGrid(GridToExcelExportType.CURRENTPAGE, context, element,
				settings, null);
		assertNotNull(context.getSession());
	}
}
