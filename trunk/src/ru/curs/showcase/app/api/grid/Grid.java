package ru.curs.showcase.app.api.grid;

import java.util.Iterator;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.element.*;
import ru.curs.showcase.app.api.event.*;

/**
 * Класс грида с данными и настройками оформления. Содержит всю информацию,
 * необходимую для отрисовки грида в UI.
 * 
 * @author den
 * 
 */
public class Grid extends DataPanelCompBasedElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -8875148764868361032L;

	/**
	 * Набор данных для грида. Содержит описания столбцов, строк, страниц и
	 * внешнего вида собственно грида.
	 */
	private DataSet dataSet = new DataSet();

	/**
	 * Настройки UI для грида. Как правило, задаются по умолчанию для всех
	 * гридов в файле настроек приложения.
	 */
	private DataGridSettings uiSettings = new DataGridSettings();

	/**
	 * Строка, которая должна быть выделена автоматически при перерисовке грида
	 * с новыми данными. Если null - ничего делать не надо.
	 */
	private Record autoSelectRecord = null;

	/**
	 * Столбец, который должен быть выделен автоматически при перерисовке грида
	 * с новыми данными. Если null - ничего делать не надо.
	 */
	private Column autoSelectColumn = null;

	@Override
	public final GridEventManager getEventManager() {
		return (GridEventManager) super.getEventManager();
	}

	/**
	 * Возвращает столбец по его id.
	 * 
	 * @param id
	 *            - id.
	 * @return - столбец.
	 */
	public Column getColumnById(final String id) {
		Iterator<Column> iterator = getDataSet().getColumnSet().getColumns().iterator();
		while (iterator.hasNext()) {
			Column current = iterator.next();
			if (current.getId().equals(id)) {
				return current;
			}
		}
		return null;
	}

	public final DataSet getDataSet() {
		return dataSet;
	}

	public final void setDataSet(final DataSet aDataSet) {
		dataSet = aDataSet;
	}

	public final Column getAutoSelectColumn() {
		return autoSelectColumn;
	}

	public final void setAutoSelectColumn(final Column aAutoSelectColumn) {
		autoSelectColumn = aAutoSelectColumn;
	}

	public final Record getAutoSelectRecord() {
		return autoSelectRecord;
	}

	public final void setAutoSelectRecord(final Record aAutoSelectRecord) {
		autoSelectRecord = aAutoSelectRecord;
	}

	/**
	 * Возвращает действие для отрисовки зависимого элемента.
	 * 
	 * @return - действие.
	 */
	@Override
	public Action getActionForDependentElements() {
		if (autoSelectRecord != null) {
			Column column = null;

			if (autoSelectColumn == null) {
				column = dataSet.getColumnSet().getColumns().get(0);
			} else {
				column = autoSelectColumn;
			}
			if (column == null) {
				return null; // грид без столбцов - нет смысла
			}

			GridEvent event =
				getEventManager().getEventForCell(autoSelectRecord.getId(), column.getId(),
						InteractionType.SINGLE_CLICK);
			if (event != null) {
				return event.getAction();
			}
			event =
				getEventManager().getEventForCell(autoSelectRecord.getId(), column.getId(),
						InteractionType.DOUBLE_CLICK);
			if (event != null) {
				return event.getAction();
			}
		} else {
			return getDefaultAction();
		}

		return null;
	}

	public final DataGridSettings getUISettings() {
		return uiSettings;
	}

	public final void setUISettings(final DataGridSettings aSettings) {
		uiSettings = aSettings;
	}

	@Override
	protected EventManager initEventManager() {
		return new GridEventManager();
	}
}
