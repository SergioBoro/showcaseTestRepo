package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.gwt.datagrid.*;
import ru.curs.gwt.datagrid.event.*;
import ru.curs.gwt.datagrid.model.*;
import ru.curs.gwt.datagrid.selection.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.Grid;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.DownloadHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с гридом.
 */
public class DataGridPanel extends BasicElementPanelBasis {

	/**
	 * HorizontalPanel hpHeader.
	 */
	private final HorizontalPanel hpHeader = new HorizontalPanel();

	/**
	 * HorizontalPanel hpButtons.
	 */
	private final HorizontalPanel hpToolbar = new HorizontalPanel();

	/**
	 * PushButton exportToExcelCurrentPage.
	 */
	private final PushButton exportToExcelCurrentPage = new PushButton(new Image(
			Constants.GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE));
	/**
	 * PushButton exportToExcelAll.
	 */
	private final PushButton exportToExcelAll = new PushButton(new Image(
			Constants.GRID_IMAGE_EXPORT_TO_EXCEL_ALL));

	/**
	 * HorizontalPanel hpFooter.
	 */
	private final HorizontalPanel hpFooter = new HorizontalPanel();

	/**
	 * DataGridSettings settingsDataGrid.
	 */
	private final DataGridSettings settingsDataGrid = new DataGridSettings();

	/**
	 * DataGrid dg.
	 */
	private DataGrid dg = null;

	/**
	 * ColumnSet cs.
	 */
	private ColumnSet cs = null;

	/**
	 * Timer selectionTimer.
	 */
	private Timer selectionTimer = null;

	/**
	 * VerticalPanel.
	 */
	private final VerticalPanel p = new VerticalPanel();

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService = null;

	/**
	 * DataPanelElementInfo.
	 */
	private DataPanelElementInfo elementInfo;
	/**
	 * DataGridPanelCallbacks.
	 */
	private final DataGridPanelCallbacks dgpCallbacks;

	/**
	 * GridRequestedSettings.
	 */
	private GridRequestedSettings settings = null;

	/**
	 * Grid.
	 */
	private Grid grid = null;

	/**
	 * Для предотвращения повторного срабатывания обработчиков.
	 */
	private boolean bListenersExit = true;

	/**
	 * Тип обновления грида.
	 */
	private enum UpdateType {
		/**
		 * Полный.
		 */
		FULL,

		/**
		 * Только записи вызовом ф-ции updateRecordSet.
		 */
		RECORDSET_BY_UPDATERECORDSET,

		/**
		 * Только записи вызовом ф-ции showData.
		 */
		RECORDSET_BY_SHOWDATA,

		/**
		 * Обновление при вызове ф-ции redrawGrid.
		 */
		UPDATE_BY_REDRAWGRID

	}

	/**
	 * Ф-ция, возвращающая панель с гридом.
	 * 
	 * @return - Панель с гридом.
	 */
	@Override
	public VerticalPanel getPanel() {
		return p;
	}

	@Override
	public void hidePanel() {
		p.setVisible(false);
	}

	@Override
	public void showPanel() {
		p.setVisible(true);
	}

	@Override
	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aelement) {
		this.elementInfo = aelement;
	}

	/**
	 * Конструктор класса DataGridPanel без начального показа грида.
	 */
	public DataGridPanel(final DataPanelElementInfo element,
			final DataGridPanelCallbacks dgpCallbacks1) {

		setElementInfo(element);
		setContext(null);
		setIsFirstLoading(true);

		this.dgpCallbacks = dgpCallbacks1;

		// --------------

	}

	/**
	 * Конструктор класса DataGridPanel.
	 */
	public DataGridPanel(final CompositeContext context, final DataPanelElementInfo element,
			final DataGridPanelCallbacks dgpCallbacks1) {

		this.setContext(context);
		this.setElementInfo(element);
		setIsFirstLoading(true);

		this.dgpCallbacks = dgpCallbacks1;

		// --------------

		p.add(new HTML(Constants.PLEASE_WAIT_GRID_1));

		setGrid(UpdateType.FULL, false);

	}

	@Override
	public void reDrawPanel(final CompositeContext context, final Boolean refreshContextOnly) {

		setContext(context);
		// --------------

		if ((!getIsFirstLoading()) && refreshContextOnly) {
			grid.updateAddContext(context);
		} else {

			if (getIsFirstLoading()) {
				settings = null;

				p.add(new HTML(Constants.PLEASE_WAIT_GRID_1));

				setGrid(UpdateType.FULL, refreshContextOnly);
			} else {
				hpHeader.clear();
				hpHeader.add(new HTML(Constants.PLEASE_WAIT_GRID_2));

				hpToolbar.setVisible(false);
				dg.setVisible(false);
				hpFooter.setVisible(false);

				setGrid(UpdateType.UPDATE_BY_REDRAWGRID, refreshContextOnly);
			}
		}

	}

	private void resetCurrentSelection() {
		settings.setCurrentColumnId(null);
		settings.setCurrentRecordId(null);
		settings.getSelectedRecordIds().clear();
	}

	private void saveCurrentSelection() {
		if (dg.getClickSelection().getClickedRecord() != null) {
			settings.setCurrentRecordId(dg.getClickSelection().getClickedRecord().getId());
		}

		DataCell cell = dg.getSelection().getSelectedCell();
		if (cell != null) {
			settings.setCurrentRecordId(cell.getRecord().getId());
			settings.setCurrentRecordId(cell.getColumn().getId());
		}

		List<Record> records = dg.getSelection().getSelectedRecords();
		if (records != null) {
			Iterator<Record> iterator = records.iterator();
			while (iterator.hasNext()) {
				settings.getSelectedRecordIds().add(iterator.next().getId());
			}
		}
	}

	private void setGrid(final UpdateType ut, final Boolean refreshContextOnly) {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getGrid(getContext(), elementInfo, settings, new GWTServiceCallback<Grid>(
				"Ошибка при получении данных таблицы с сервера") {

			@Override
			public void onSuccess(final Grid grid1) {
				grid = grid1;

				bListenersExit = true;

				beforeUpdateGrid();

				switch (ut) {
				case FULL:
					updateGridFull();
					break;
				case RECORDSET_BY_UPDATERECORDSET:
					updateGridRecordsetByUpdateRecordset();
					break;
				case RECORDSET_BY_SHOWDATA:
					updateGridRecordsetByShowData();
					break;
				case UPDATE_BY_REDRAWGRID:
					updateGridRedrawGrid();
					break;
				default:
					throw new Error("Неизвестный тип UpdateType");
				}

				afterUpdateGrid(ut);

				if (getIsFirstLoading() && refreshContextOnly) {
					grid.updateAddContext(getContext());
				}
				setIsFirstLoading(false);

				bListenersExit = false;

			}
		});

	}

	private void beforeUpdateGrid() {
		hpHeader.clear();
		HTML header = new HTML();
		header.setHTML(grid.getHeader());
		hpHeader.add(header);

		hpFooter.clear();
		HTML footer = new HTML();
		footer.setHTML(grid.getFooter());
		hpFooter.add(footer);

		settingsDataGrid.assign(grid.getUISettings());
		// все настройки - в т.ч. по умолчанию - устанавливаются сервером
	}

	private void updateGridFull() {

		// -------------------------
		dg = new DataGrid(settingsDataGrid);

		// -------------------------

		p.setSize("100%", "100%");

		hpHeader.setSize("100%", "100%");
		hpFooter.setSize("100%", "100%");
		// dg.setSize("100%", "100%");
		hpToolbar.setHeight("100%");
		dg.setWidth("95%");

		hpToolbar.setSpacing(1);
		if (grid.getUISettings().isVisibleExportToExcelCurrentPage()) {
			exportToExcelCurrentPage.setTitle("Экспорт в Excel текущей страницы");
			exportToExcelCurrentPage.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					exportToExcel(GridToExcelExportType.CURRENTPAGE);
				}
			});
			hpToolbar.add(exportToExcelCurrentPage);
		}
		if (grid.getUISettings().isVisibleExportToExcelAll()) {
			exportToExcelAll.setTitle("Экспорт в Excel всей таблицы");
			exportToExcelAll.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					exportToExcel(GridToExcelExportType.ALL);
				}
			});
			hpToolbar.add(exportToExcelAll);
		}

		p.clear();
		p.add(hpHeader);
		p.add(hpToolbar);
		p.add(dg);
		p.add(hpFooter);

		resetGridSettingsToCurrent();

		cs = grid.getDataSet().getColumnSet();

		dg.addDataGridListener(new GridListener());
		dg.getSelection().addListener(new SelectionListener());
		dg.addDataClickHandler(new DataClickHandler("DataClickHandler1"));

		if ((grid.getDataSet().getColumnSet().getColumns().size() > 0)
				&& (grid.getDataSet().getRecordSet().getRecordsCount() > 0)) {
			dg.showData(grid.getDataSet());
		} else {
			hpToolbar.setVisible(false);
			dg.setVisible(false);
		}

	}

	private void updateGridRecordsetByUpdateRecordset() {
		dg.updateRecordSet(grid.getDataSet().getRecordSet());
	}

	private void updateGridRecordsetByShowData() {
		grid.getDataSet().setColumnSet(cs);
		dg.showData(grid.getDataSet());
	}

	private void updateGridRedrawGrid() {
		cs = grid.getDataSet().getColumnSet();

		if ((grid.getDataSet().getColumnSet().getColumns().size() > 0)
				&& (grid.getDataSet().getRecordSet().getRecordsCount() > 0)) {

			dg.setVisible(true);
			dg.showData(grid.getDataSet());
			hpToolbar.setVisible(true);
		}

		hpFooter.setVisible(true);
	}

	private void afterUpdateGrid(final UpdateType ut) {
		String recId = null;
		String colId = null;
		boolean selectionSaved = false;

		if (grid.getDataSet().getRecordSet().getPagesTotal() > 0) {

			if (grid.getUISettings().isSelectOnlyRecords()) {
				if (grid.getAutoSelectRecord() != null) {
					recId = grid.getAutoSelectRecord().getId();
					dg.getClickSelection().setClickedRecordById(recId);
				}
			} else {
				if ((grid.getAutoSelectRecord() != null) && (grid.getAutoSelectColumn() != null)) {
					recId = grid.getAutoSelectRecord().getId();
					colId = grid.getAutoSelectColumn().getId();
					dg.getSelection().setSelectedCellById(recId, colId);
					// dg.getClickSelection().setClickedRecordById(recId);
				}
			}

			// user settings имеет приоритет
			if (settings != null) {
				recId = settings.getCurrentRecordId();
				colId = settings.getCurrentColumnId();
				if (recId != null) {
					selectionSaved = true;
					if (colId != null) {
						dg.getSelection().setSelectedCellById(recId, colId);
					}
					dg.getClickSelection().setClickedRecordById(recId);
				}
				dg.getSelection().setSelectedRecordsById(settings.getSelectedRecordIds());
			}

		}

		if (ut == UpdateType.FULL) {
			runActionForDepElements();
		} else if ((ut == UpdateType.UPDATE_BY_REDRAWGRID) && (!selectionSaved)) {
			runActionForDepElements();
		} else {
			processClick(recId, colId, InteractionType.SINGLE_CLICK);
		}

		if (ut == UpdateType.UPDATE_BY_REDRAWGRID) {
			resetGridSettingsToCurrent();
		}
	}

	private void runActionForDepElements() {
		Action ac = grid.getActionForDependentElements();
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentAction(ac);

			dgpCallbacks.dataGridPanelClick();
		}
	}

	private void resetGridSettingsToCurrent() {
		settings = new GridRequestedSettings();
		settings.setPageNumber(grid.getDataSet().getRecordSet().getPageNumber());
		settings.setPageSize(grid.getDataSet().getRecordSet().getPageSize());
	}

	private void exportToExcel(final GridToExcelExportType exportType) {
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.clear();

		dh.setErrorCaption(Constants.GRID_ERROR_CAPTION_EXPORT_EXCEL);
		dh.setAction("secured/gridToExcel");

		try {
			dh.addParam(exportType.getClass().getName(), exportType.toString());

			SerializationStreamFactory ssf = dh.getObjectSerializer();
			dh.addStdPostParamsToBody(getContext(), elementInfo);
			dh.addParam(settings.getClass().getName(), settings.toParamForHttpPost(ssf));
			dh.addParam(cs.getClass().getName(), cs.toParamForHttpPost(ssf));

			dh.submit();

		} catch (Exception e) {
			MessageBox
					.showSimpleMessage(Constants.GRID_ERROR_CAPTION_EXPORT_EXCEL, e.getMessage());
		}
	}

	// -------------------------------------------------------

	/**
	 * DataClickHandler.
	 */
	private final class DataClickHandler implements GridClickHandler<DataCell> {

		/**
		 * label.
		 */
		@SuppressWarnings("unused")
		private final String label;

		private DataClickHandler(final String label1) {
			this.label = label1;
		}

		@Override
		public void onClick(final GridClickEvent<DataCell> event) {
			// log("data click (" + label + "): " + event.getClickType() +
			// ", record="
			// + event.getTarget().getRecord().getId() + ", column="
			// + event.getTarget().getColumn().getId());

			// event.preventDefault();

			if (bListenersExit) {
				return;
			}

			InteractionType interactionType;
			switch (event.getClickType()) {
			case SINGLE:
				interactionType = InteractionType.SINGLE_CLICK;
				break;
			case DOUBLE:
				interactionType = InteractionType.DOUBLE_CLICK;
				break;
			case RIGHT:
				interactionType = InteractionType.RIGHT_CLICK;
				break;
			case MIDDLE:
				interactionType = InteractionType.MIDDLE_CLICK;
				break;
			default:
				interactionType = InteractionType.SINGLE_CLICK;
				break;
			}

			processClick(event.getTarget().getRecord().getId(), event.getTarget().getColumn()
					.getId(), interactionType);
		}
	}

	private void processClick(final String rowId, final String colId,
			final InteractionType interactionType) {

		Action ac = null;

		GridEvent ev = grid.getEventManager().getEventForCell(rowId, colId, interactionType);

		if (ev != null) {
			ac = ev.getAction();
		}

		if (ac != null) {
			AppCurrContext.getInstance().setCurrentAction(ac);

			dgpCallbacks.dataGridPanelClick();
		}

	}

	/**
	 * SelectionListener.
	 */
	private class SelectionListener implements DataSelectionListener {
		@Override
		public void selectedRecordsChanged(final DataSelection selection) {

			// log("selectedRecordsChanged: " +
			// selection.hasSelectedRecords() + ", "
			// + recordsToString(selection.getSelectedRecords()));

			if (bListenersExit) {
				return;
			}

			if (selectionTimer != null) {
				selectionTimer.cancel();
			}

			selectionTimer = new Timer() {
				@Override
				public void run() {
					processSelectionRecords();
				}
			};
			selectionTimer.schedule(Constants.GRID_SELECTION_DELAY);

		}

		@Override
		public void selectedCellChanged(final DataSelection selection) {
			// log("selectedCellChanged: " + (selection.isCellSelected()
			// ? "record=" + selection.getSelectedCell().getRecord().getId() +
			// ", column=" + selection.getSelectedCell().getColumn().getId()
			// : "not selected"));
		}
	}

	private void processSelectionRecords() {

		if (bListenersExit) {
			return;
		}

		Action ac =
			grid.getEventManager().getSelectionActionForDependentElements(dg.getSelection());

		if (ac != null) {
			AppCurrContext.getInstance().setCurrentAction(ac);

			dgpCallbacks.dataGridPanelClick();
		}

	}

	/**
	 * GridListener.
	 */
	private class GridListener implements DataGridListener {
		@Override
		public void columnWidthChanged(final Column column) {
			// log("columnWidthChanged, column=" + column.getId() + ", width=" +
			// column.getWidth());

			if (bListenersExit) {
				return;
			}

			cs.getColumns().get(column.getIndex()).setWidth(column.getWidth());

			// log("columnsLayoutChanged");
			// StringBuilder b = new StringBuilder();
			// for (Column c : cs.getColumns()) {
			// b.append(c.getId()).append(": ").append(c.getWidth()).append(" ");
			// }
			// log("(columns width: " + b.toString() + ")");

		}

		@Override
		public void sortingChanged(final List<Column> columns) {

			// StringBuilder b = new StringBuilder();
			// for (Column c : columns) {
			// b.append(c.getId()).append("[").append(c.getSorting()).append("] ");
			// }
			// log("sortingChanged, columns: " + b.toString());

			if (bListenersExit) {
				return;
			}

			settings.setPageNumber(1);
			settings.setSortedColumns(columns);
			setGrid(UpdateType.RECORDSET_BY_UPDATERECORDSET, false);
			// dg.updateRecordSet(grid.getDataSet().getRecordSet());
		}

		@Override
		public void pageNumberChanged(final int newPageNumber) {
			// log("pageNumberChanged: " + newPageNumber);
			// rs.setPageNumber(newPageNumber);
			// r1.setValue("aa", "page no " + newPageNumber);
			// dg.updateRecordSet(rs);

			if (bListenersExit) {
				return;
			}

			settings.setPageNumber(newPageNumber);
			setGrid(UpdateType.RECORDSET_BY_UPDATERECORDSET, false);
			// dg.updateRecordSet(grid.getDataSet().getRecordSet());
		}

		@Override
		public void pageSizeChanged(final int newItemsPerPage) {
			// log("pageSizeChanged: " + newItemsPerPage);
			// rs.setPageSize(newItemsPerPage);
			// r1.setValue("cc", "pagesize is " + newItemsPerPage);
			// dg.updateRecordSet(rs);

			if (bListenersExit) {
				return;
			}

			settings.setPageNumber(1);
			settings.setPageSize(newItemsPerPage);
			setGrid(UpdateType.RECORDSET_BY_SHOWDATA, false);
			// dg.updateRecordSet(grid.getDataSet().getRecordSet());
		}

		@Override
		public void columnsLayoutChanged() {
			// log("columnsLayoutChanged");
			// StringBuilder b = new StringBuilder();
			// for (Column c : cs.getColumns()) {
			// b.append(c.getId()).append(": ").append(c.getWidth()).append(" ");
			// }
			// log("(columns width: " + b.toString() + ")");
			// dg.updateColumnSet(cs);

			if (bListenersExit) {
				return;
			}

			bListenersExit = true;
			dg.updateColumnSet(cs);
			bListenersExit = false;
		}

	}

	@Override
	public void saveSettings(final Boolean reDrawWithSettingsSave) {
		if (!getIsFirstLoading()) {
			if (reDrawWithSettingsSave) {
				resetCurrentSelection();
				saveCurrentSelection();
			} else {
				settings = null;
			}
		}
	}

	@Override
	public DataPanelElement getElement() {
		return grid;
	}

}
