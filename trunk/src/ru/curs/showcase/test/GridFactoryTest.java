package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.ElementRawData;
import ru.curs.showcase.model.grid.*;

/**
 * Тестовый класс для фабрики гридов.
 * 
 * @author den
 * 
 */
public class GridFactoryTest extends AbstractTestBasedOnFiles {

	static final String GRIDBAL_TEST_PROPERTIES = "gridbal.test.properties";

	/**
	 * Основной тест для фабрики гридов.
	 */
	@Test
	public void testGetData() throws GeneralServerException, IOException {
		final int colCount = 26;
		final int pagesCount = 6;
		final int pageSize = 15;
		final Integer autoSelectRecord = 15;
		final String precision = "2";

		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "2", "2");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, element, null);

		assertNotNull(context.getSession());
		assertNotNull(grid);
		assertNotNull(grid.getDataSet());
		assertTrue(!grid.getHeader().isEmpty());
		assertTrue(grid.getHeader().contains("class=\"testStyle\""));
		assertTrue(grid.getFooter().isEmpty());
		assertNotNull(grid.getEventManager().getEvents());
		assertTrue(grid.getEventManager().getEvents().size() > 0);

		assertNotNull(grid.getDataSet().getRecordSet());
		assertNotNull(grid.getDataSet().getColumnSet());
		assertEquals(colCount, grid.getDataSet().getColumnSet().getColumns().size());
		Column col = grid.getDataSet().getColumnSet().getColumns().get(0);
		assertEquals(HorizontalAlignment.LEFT, col.getHorizontalAlignment());
		assertNull(col.getFormat());
		col = grid.getDataSet().getColumnSet().getColumns().get(1);
		assertEquals(HorizontalAlignment.CENTER, col.getHorizontalAlignment());
		assertEquals(GridValueType.IMAGE, col.getValueType());
		assertEquals("resources/imagesingrid/test.jpg", grid.getDataSet().getRecordSet()
				.getRecords().get(0).getValue(col));
		col = grid.getDataSet().getColumnSet().getColumns().get(2);
		assertEquals(HorizontalAlignment.RIGHT, col.getHorizontalAlignment());
		assertEquals(precision, col.getFormat());

		assertEquals(grid.getDataSet().getRecordSet().getPageSize(), grid.getDataSet()
				.getRecordSet().getRecordsCount());
		assertEquals(2, grid.getDataSet().getRecordSet().getPageNumber());
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pagesCount, grid.getDataSet().getRecordSet().getPagesTotal());

		assertNotNull(grid.getDefaultAction());
		Action firstCellAction =
			grid.getEventManager()
					.getEventForCell(
							autoSelectRecord.toString(),
							grid.getDataSet().getColumnSet().getColumnsByIndex().iterator().next()
									.getId(), InteractionType.SINGLE_CLICK).getAction();
		assertEquals(firstCellAction, grid.getActionForDependentElements());
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, grid.getDefaultAction()
				.getDataPanelActionType());
		assertEquals(context.getMain(), grid.getDefaultAction().getDataPanelLink().getContext()
				.getMain());
		assertNull(grid.getDefaultAction().getDataPanelLink().getContext().getSession());
		assertNotNull(grid.getAutoSelectRecord());
		assertEquals(autoSelectRecord.toString(), grid.getAutoSelectRecord().getId());

		assertTrue(grid.getUISettings().isSelectOnlyRecords());
		assertFalse(grid.getUISettings().isSingleClickBeforeDoubleClick());
		assertNotNull(grid.getEventManager().getEventForCell(autoSelectRecord.toString(), null,
				InteractionType.SELECTION));
	}

	/**
	 * Тест на обновление грида с измененными параметрами постраничной выборки.
	 */
	@Test
	public void testWithRequestedSettings() throws GeneralServerException, IOException {
		final int maxColIndex = 5;
		final int pageSize = maxColIndex;
		final int pageNum = 10;
		final String firstColName = "3кв. 2005г.";
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "2", "2");

		GridRequestedSettings settings = new GridRequestedSettings();
		settings.setPageNumber(pageNum);
		settings.setPageSize(pageSize);
		addSortedColumn(settings, "3кв. 2007г.", maxColIndex);
		addSortedColumn(settings, "3кв. 2006г.", 1);
		addSortedColumn(settings, firstColName, 0);
		assertNull(settings.getCurrentColumnId());
		assertNull(settings.getCurrentRecordId());
		assertEquals(0, settings.getSelectedRecordIds().size());
		settings.setCurrentColumnId(firstColName);
		settings.setCurrentRecordId("1");
		settings.getSelectedRecordIds().add("1");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, element, settings);

		assertNotNull(context.getSession());
		assertEquals(pageNum, grid.getDataSet().getRecordSet().getPageNumber());
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getRecordsCount());
		assertEquals(firstColName, settings.getSortedColumns().iterator().next().getId());
		assertEquals(firstColName, settings.getCurrentColumnId());
		assertEquals("1", settings.getCurrentRecordId());
		assertEquals(1, settings.getSelectedRecordIds().size());
		assertEquals("1", settings.getSelectedRecordIds().get(0));
	}

	/**
	 * Тестирует задание профайла настроек из хранимой процедуры.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProfileSelection() throws Exception {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "2", "4");

		GridGateway gateway = new GridDBGateway();
		ElementRawData raw = gateway.getFactorySource(context, element);
		GridDBFactory factory = new GridDBFactory(raw, null);
		Grid grid = factory.build();
		assertEquals(GRIDBAL_TEST_PROPERTIES, factory.getProfile());

		assertEquals(1, grid.getDataSet().getRecordSet().getPageNumber());

		GridProps gp = new GridProps(GRIDBAL_TEST_PROPERTIES);

		Boolean defSelectRecord =
			gp.stdReadBoolGridValue(DefaultGridUIStyle.DEF_SELECT_WHOLE_RECORD);
		assertEquals(defSelectRecord, grid.getUISettings().isSelectOnlyRecords());
		final int fontWidth = 27;
		assertEquals(fontWidth, grid.getDataSet().getRecordSet().getRecords().get(0).getFontSize());
	}

	private void addSortedColumn(final GridRequestedSettings settings, final String name,
			final int index) {
		Column col = new Column();
		col.setId(name);
		col.setSorting(Sorting.ASC);
		col.setIndex(index);
		settings.getSortedColumns().add(col);
	}
}
