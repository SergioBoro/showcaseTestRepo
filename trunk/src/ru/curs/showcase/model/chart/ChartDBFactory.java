package ru.curs.showcase.model.chart;

import java.sql.*;
import java.util.Iterator;

import javax.sql.RowSet;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.exception.ResultSetHandleException;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.SQLUtils;

/**
 * Фабрика для создания графика на основе данных из БД.
 * 
 * @author den
 * 
 */
public class ChartDBFactory extends AbstractChartFactory {
	/**
	 * SQL ResultSet с данными грида.
	 */
	private RowSet sql;

	/**
	 * Признак того, что в RecordSet заданы события.
	 */
	private boolean eventsDefined = false;

	/**
	 * Номер записи, содержащей данные о событии. Используется только в случае
	 * транспонированных данных.
	 */
	private Integer eventRowNumber = 0;

	public ChartDBFactory(final ElementRawData aSource) {
		super(aSource);
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
	protected void prepareSettings() {
		getSource().prepareSettings();
	}

	@Override
	protected void releaseResources() {
		getSource().releaseResources();
	}

	private void resetRowPosition() {
		try {
			sql.beforeFirst();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	@Override
	protected void fillLabelsX() {
		if (!isFlip()) {
			fillLabelsXIfNotFlip();
		} else {
			fillLabelsXIfFlip();
			resetRowPosition();
		}
	}

	private void fillLabelsXIfFlip() {
		addZeroLabelForX();
		try {
			int counter = 1;
			while (sql.next()) {
				String value = sql.getString(getSelectorColumn());
				if (PROPERTIES_SQL_TAG.equals(value)) {
					eventsDefined = true;
					eventRowNumber = counter;
					continue;
				}
				ChartLabel curLabel = new ChartLabel();
				curLabel.setValue(counter++);
				curLabel.setText(value);
				getResult().getJavaDynamicData().getLabelsX().add(curLabel);
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void fillLabelsXIfNotFlip() {
		ResultSetMetaData md;
		try {
			md = sql.getMetaData();
		} catch (SQLException e1) {
			throw new ResultSetHandleException(e1);
		}
		try {
			addZeroLabelForX();
			int counter = 1;
			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (getSelectorColumn().equals(md.getColumnLabel(i))) {
					continue;
				}
				if (PROPERTIES_SQL_TAG.equals(md.getColumnLabel(i))) {
					eventsDefined = true;
					continue;
				}
				ChartLabel curLabel = new ChartLabel();
				curLabel.setValue(counter++);
				curLabel.setText(md.getColumnLabel(i));
				getResult().getJavaDynamicData().getLabelsX().add(curLabel);
			}
		} catch (SQLException e2) {
			throw new ResultSetHandleException(e2);
		}
	}

	private void addZeroLabelForX() {
		ChartLabel curLabel;
		curLabel = new ChartLabel();
		curLabel.setValue(0);
		curLabel.setText("");
		getResult().getJavaDynamicData().getLabelsX().add(curLabel);
	}

	@Override
	protected void fillSeries() {
		if (!isFlip()) {
			fillSeriesIfNotFlip();
		} else {
			fillSeriesIfFlip();
		}
	}

	private void fillSeriesIfFlip() {
		ResultSetMetaData md;
		try {
			md = sql.getMetaData();

			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (getSelectorColumn().equals(md.getColumnLabel(i))) {
					continue;
				}
				ChartSeries series = new ChartSeries();
				series.setName(md.getColumnLabel(i));
				while (sql.next()) {
					String value = sql.getString(md.getColumnLabel(i));
					if (sql.getRow() != eventRowNumber) {
						addValueToSeries(series, value);
					} else {
						readEvents(series, value);
					}
				}
				getResult().getJavaDynamicData().getSeries().add(series);
				resetRowPosition();
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void addValueToSeries(final ChartSeries series, final String value) {
		if (value != null) {
			series.addValue(Double.valueOf(value));
		} else {
			series.addValue(null);
		}
	}

	private void fillSeriesIfNotFlip() {
		try {
			while (sql.next()) {
				ChartSeries series = new ChartSeries();
				series.setName(sql.getString(getSelectorColumn()));

				Iterator<ChartLabel> iterator =
					getResult().getJavaDynamicData().getLabelsX().iterator();
				skipZeroLabelForX(iterator);
				while (iterator.hasNext()) {
					ChartLabel label = iterator.next();
					String value = sql.getString(label.getText());
					addValueToSeries(series, value);
				}
				if (eventsDefined) {
					readEvents(series, sql.getString(PROPERTIES_SQL_TAG));
				}
				getResult().getJavaDynamicData().getSeries().add(series);
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void readEvents(final ChartSeries series, final String value) {
		EventFactory<ChartEvent> factory = new EventFactory<ChartEvent>(ChartEvent.class);
		factory.initForGetSubSetOfEvents(X_TAG, VALUE_TAG, CHART_PROPERTIES_XSD);
		SAXTagHandler colorHandler = new SAXTagHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				series.setColor(attrs.getValue(VALUE_TAG));
				return series;
			}

			@Override
			public void handleEndTag(final String aNamespaceURI, final String aLname,
					final String aQname) {
			}

			@Override
			public void handleCharacters(final char[] aArg0, final int aArg1, final int aArg2) {
			}

			@Override
			public boolean canHandle(final String aTagName, final SaxEventType aSaxEventType) {
				return (COLOR_TAG.equalsIgnoreCase(aTagName));
			}
		};
		factory.addHandler(colorHandler);
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(series.getName(), value));
	}

	private void skipZeroLabelForX(final Iterator<ChartLabel> iterator) {
		if (iterator.hasNext()) {
			iterator.next();
		}
	}
}
