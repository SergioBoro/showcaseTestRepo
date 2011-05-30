package ru.curs.showcase.model.grid;

import java.sql.*;
import java.text.*;
import java.util.*;

import javax.sql.RowSet;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.exception.ResultSetHandleException;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

/**
 * Фабрика для создания гридов инициализированная исходными данными для работы.
 * 
 * @author den
 * 
 */
public class GridDBFactory extends AbstractGridFactory {
	/**
	 * Не локальная Locale по умолчанию :) Используется для передачи данных в
	 * приложение, которые плохо обрабатывают текущую Locale.
	 */
	private static final Locale DEF_NON_LOCAL_LOCALE = Locale.US;
	/**
	 * Тэг столбца события в гриде.
	 */
	private static final String EVENT_COLUMN_TAG = "column";
	/**
	 * Префикс имени события, определяющий событие в ячейке.
	 */
	private static final String CELL_PREFIX = "cell";

	/**
	 * SQL ResultSet с данными грида.
	 */
	private RowSet sql;

	public GridDBFactory(final ElementRawData aSource, final GridRequestedSettings aSettings) {
		super(aSource, aSettings);
	}

	@Override
	protected void releaseResources() {
		getSource().releaseResources();
	}

	@Override
	protected void prepareSettings() {
		getSource().prepareSettings();
	}

	@Override
	protected void prepareData() {
		try {
			ResultSet rs = getSource().getSpCallHelper().getCs().getResultSet();
			sql = SQLUtils.cacheResultSet(rs);
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	@Override
	protected void fillRecordsAndEvents() {
		ColumnSet cs = getResult().getDataSet().getColumnSet();
		RecordSet rs = getResult().getDataSet().getRecordSet();

		try {
			int counter = scrollToRequiredPage(rs);
			int lastNumber = counter + rs.getPageSize();
			while (sql.next() && (counter < lastNumber)) {
				Record curRecord = new Record();
				curRecord.setId(String.valueOf(counter++));
				setupStdRecordProps(curRecord);

				Iterator<Column> iterator = cs.getColumns().iterator();
				String value = null;
				while (iterator.hasNext()) {
					Column col = iterator.next();
					if (col.getValueType() == GridValueType.IMAGE) {
						value =
							String.format("%s/%s",
									AppProps.getRequiredValueByName(IMAGES_IN_GRID_DIR),
									sql.getString(col.getId()));
					} else if (col.getValueType() == GridValueType.LINK) {
						value = sql.getString(col.getId());
						value =
							value.replace("${" + IMAGES_IN_GRID_DIR + "}",
									AppProps.getRequiredValueByName(IMAGES_IN_GRID_DIR));
					} else if (sql.getObject(col.getId()) == null) {
						value = "";
					} else if (col.getValueType().isDate()) {
						value = getStringValueOfDate(col);
					} else if (col.getValueType().isNumber()) {
						value = getStringValueOfNumber(col);
					} else {
						value = sql.getString(col.getId());
					}
					curRecord.setValue(col.getId(), value);
				}
				rs.getRecords().add(curRecord);
				readEvents(curRecord.getId(), sql.getString(PROPERTIES_SQL_TAG));
			}

			calcRecordsCount(rs);
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void readEvents(final String curRecordId, final String data) {
		EventFactory<GridEvent> factory = new EventFactory<GridEvent>(GridEvent.class);
		factory.initForGetSubSetOfEvents(EVENT_COLUMN_TAG, CELL_PREFIX, GRID_PROPERTIES_XSD);
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(curRecordId, data));
	}

	private String getStringValueOfNumber(final Column col) throws SQLException {
		Double value = sql.getDouble(col.getId());
		NumberFormat nf;
		if (useLocalFormatting()) {
			nf = NumberFormat.getNumberInstance();
		} else {
			nf = NumberFormat.getNumberInstance(DEF_NON_LOCAL_LOCALE);
		}
		if (col.getFormat() != null) {
			nf.setMinimumFractionDigits(Integer.parseInt(col.getFormat()));
			nf.setMaximumFractionDigits(Integer.parseInt(col.getFormat()));
		}
		return nf.format(value);
	}

	private String getStringValueOfDate(final Column col) throws SQLException {
		java.util.Date date = null;
		DateFormat df = null;
		String value = getGridProps().getValueByNameForGrid(DEF_DATE_VALUES_FORMAT);
		Integer style = DateFormat.DEFAULT;
		if (value != null) {
			style = DateTimeFormat.valueOf(value).ordinal();
		}
		if (col.getValueType() == GridValueType.DATE) {
			date = sql.getDate(col.getId());
			if (useLocalFormatting()) {
				df = DateFormat.getDateInstance(style);
			} else {
				df = DateFormat.getDateInstance(style, DEF_NON_LOCAL_LOCALE);
			}
		} else if (col.getValueType() == GridValueType.TIME) {
			date = sql.getTime(col.getId());
			if (useLocalFormatting()) {
				df = DateFormat.getTimeInstance(style);
			} else {
				df = DateFormat.getTimeInstance(style, DEF_NON_LOCAL_LOCALE);
			}
		} else if (col.getValueType() == GridValueType.DATETIME) {
			date = sql.getTimestamp(col.getId());
			if (useLocalFormatting()) {
				df = DateFormat.getDateTimeInstance(style, style);
			} else {
				df = DateFormat.getDateTimeInstance(style, style, DEF_NON_LOCAL_LOCALE);
			}
		}
		if (date == null) {
			return "";
		}
		return df.format(date);
	}

	private boolean useLocalFormatting() {
		return (getRequestSettings() == null) || getRequestSettings().getApplyLocalFormatting();
	}

	private void calcRecordsCount(final RecordSet rs) throws SQLException {
		sql.last();
		float recCount = sql.getRow();
		rs.setPagesTotal((int) Math.ceil(recCount / rs.getPageSize()));
	}

	private int scrollToRequiredPage(final RecordSet rs) throws SQLException {
		sql.setFetchSize(rs.getPageSize());
		int recNum = 0;
		while (recNum++ < (rs.getPageNumber() - 1) * rs.getPageSize()) {
			if (!sql.next()) {
				break;
			}
		}
		return --recNum;
	}

	@Override
	protected void fillColumns() {
		ColumnSet cs = getResult().getDataSet().getColumnSet();
		Column curColumn = null;
		ResultSetMetaData md;
		try {
			md = sql.getMetaData();
			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (md.getColumnLabel(i).equals(PROPERTIES_SQL_TAG)) {
					continue;
				}
				String colId = md.getColumnLabel(i);
				curColumn = getResult().getColumnById(colId);
				if (curColumn == null) {
					curColumn = createColumn(colId);
					cs.getColumns().add(curColumn);
				}
				curColumn.setIndex(i - 1);
				determineValueType(curColumn, md.getColumnType(i));
				setupStdColumnProps(curColumn);
				if (getRequestSettings() != null) {
					curColumn.setSorting(getRequestSettings().getSortingForColumn(curColumn));
				}
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void determineValueType(final Column column, final int sqlType) {
		if (column.getValueType() != null) {
			return; // тип задан явно
		}
		if (SQLUtils.isStringType(sqlType)) {
			column.setValueType(GridValueType.STRING);
		} else if (SQLUtils.isIntType(sqlType)) {
			column.setValueType(GridValueType.INT);
		} else if (SQLUtils.isFloatType(sqlType)) {
			column.setValueType(GridValueType.FLOAT);
		} else if (SQLUtils.isDateType(sqlType)) {
			column.setValueType(GridValueType.DATE);
		} else if (SQLUtils.isTimeType(sqlType)) {
			column.setValueType(GridValueType.TIME);
		} else if (SQLUtils.isDateTimeType(sqlType)) {
			column.setValueType(GridValueType.DATETIME);
		} else {
			column.setValueType(GridValueType.STRING);
		}
	}
}
