package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.Record;
import ru.curs.gwt.datagrid.selection.*;
import ru.curs.showcase.app.api.element.EventManager;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Класс для тестирования событий (Event).
 * 
 * @author den
 * 
 */
public class EventsTest extends AbstractTestBasedOnFiles {

	static final String ROW4 = "row4";
	static final String COL2 = "col2";
	static final String ROW2 = "row2";
	static final String ROW3 = "row3";
	static final String COL3 = "col3";
	static final String COL1 = "col1";
	static final String ROW1 = "row1";

	/**
	 * Тестирование функции очистки событий от мусорных.
	 */
	@Test
	public void testEventsClean() {
		Set<String> ids1 = new HashSet<String>();
		Set<String> ids2 = new HashSet<String>();
		String[] ids1Array = { ROW1, ROW2 };
		String[] ids2Array = { COL1, COL2 };
		ids1.addAll(Arrays.asList(ids1Array));
		ids2.addAll(Arrays.asList(ids2Array));
		EventManager mgr = createStdMgr();
		final int initEventsCount = 4;
		assertEquals(initEventsCount, mgr.getEvents().size());
		assertEquals(2, mgr.clean(ids1, ids2));
		assertEquals(2, mgr.getEvents().size());
	}

	/**
	 * Тестирование функции выборки событий по Id.
	 */
	@Test
	public void testGetEvents() {
		GridEventManager mgr = (GridEventManager) createStdMgr();
		Event event = mgr.getEventForCell(ROW1, null, InteractionType.SINGLE_CLICK);
		assertNotNull(event);
		assertEquals(ROW1, event.getId1());
		assertNull(event.getId2());
		event = mgr.getEventForCell(ROW1, COL1, InteractionType.SINGLE_CLICK);
		assertNotNull(event);
		assertEquals(ROW1, event.getId1());
		assertEquals(COL1, event.getId2());
		event = mgr.getEventForCell(ROW1, COL3, InteractionType.SINGLE_CLICK);
		assertNotNull(event);
		assertEquals(ROW1, event.getId1());
		assertNull(event.getId2());
		event = mgr.getEventForCell(ROW4, COL3, InteractionType.SINGLE_CLICK);
		assertNull(event);
	}

	/**
	 * Тест для функции формирования события по выделению 2 записей в гриде.
	 */
	@Test
	public void testSelectionEvents() {
		GridEventManager mgr = new GridEventManager();
		Event event = new GridEvent();
		event.setInteractionType(InteractionType.SELECTION);
		event.setId1(ROW1);
		Action action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		DataPanelElementLink link = new DataPanelElementLink();
		link.setId(ROW1);
		CompositeContext context = CompositeContext.createCurrent();
		context.setAdditional(ROW1);
		link.setContext(context);
		action.getDataPanelLink().getElementLinks().add(link);
		event.setAction(action);
		mgr.getEvents().add(event);
		event = new GridEvent();
		event.setInteractionType(InteractionType.SELECTION);
		event.setId1(ROW2);
		action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		link = new DataPanelElementLink();
		link.setId(ROW2);
		context = CompositeContext.createCurrent();
		context.setAdditional(ROW2);
		link.setContext(context);
		action.getDataPanelLink().getElementLinks().add(link);
		event.setAction(action);
		mgr.getEvents().add(event);
		DataSelection selection = new DataSelection() {
			@Override
			public void setSelectedRecordsById(final List<String> aRecords) {
			}

			@Override
			public void setSelectedRecords(final List<Record> aRecords) {
			}

			@Override
			public void setSelectedCellById(final String aRecordId, final String aColumnId) {
			}

			@Override
			public void setSelectedCell(final DataCell aCell) {
			}

			@Override
			public boolean isCellSelected() {
				return false;
			}

			@Override
			public boolean hasSelectedRecords() {
				return false;
			}

			@Override
			public List<Record> getSelectedRecords() {
				Record rec1 = new Record();
				rec1.setId(ROW1);
				Record rec2 = new Record();
				rec2.setId(ROW2);
				Record[] records = { rec1, rec2 };
				return Arrays.asList(records);
			}

			@Override
			public DataCell getSelectedCell() {
				return null;
			}

			@Override
			public void clearSelectedRecords() {
			}

			@Override
			public void clearSelectedCell() {
			}

			@Override
			public HandlerRegistration addListener(final DataSelectionListener aListener) {
				return null;
			}
		};
		Action filterAction = mgr.getSelectionActionForDependentElements(selection);
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, filterAction.getDataPanelActionType());
		assertEquals(2, filterAction.getDataPanelLink().getElementLinks().size());
		assertEquals("<filter><context>" + ROW1 + "</context></filter>", filterAction
				.getDataPanelLink().getElementLinkById(ROW1).getContext().getFilter());
		assertNull(filterAction.getDataPanelLink().getElementLinkById(ROW1).getContext()
				.getAdditional());
		assertEquals("<filter><context>" + ROW2 + "</context></filter>", filterAction
				.getDataPanelLink().getElementLinkById(ROW2).getContext().getFilter());
	}

	private EventManager createStdMgr() {
		EventManager mgr = new GridEventManager();
		Event event = new GridEvent();
		event.setInteractionType(InteractionType.SINGLE_CLICK);
		event.setId1(ROW1);
		mgr.getEvents().add(event);
		event = new GridEvent();
		event.setInteractionType(InteractionType.SINGLE_CLICK);
		event.setId1(ROW1);
		event.setId2(COL1);
		mgr.getEvents().add(event);
		event = new GridEvent();
		event.setInteractionType(InteractionType.SINGLE_CLICK);
		event.setId1(ROW3);
		mgr.getEvents().add(event);
		event = new GridEvent();
		event.setInteractionType(InteractionType.SINGLE_CLICK);
		event.setId1(ROW2);
		event.setId2(COL3);
		mgr.getEvents().add(event);
		return mgr;
	}

}
