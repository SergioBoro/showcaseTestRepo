package ru.curs.showcase.model.grid;

import java.sql.*;
import java.util.Iterator;

import ru.curs.gwt.datagrid.model.Column;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridRequestedSettings;
import ru.curs.showcase.model.*;

/**
 * Шлюз к БД для грида.
 * 
 * @author den
 * 
 */
public class GridDBGateway extends CompBasedElementSPCallHelper implements GridGateway {
	static final String FIRST_RECORD_TAG = "firstrecord";
	protected static final String SORT_COLUMNNAME = "sortcols";
	public static final String OUTPUT_COLUMNNAME = "gridsettings";
	protected static final String GRIDSETTINGS_XSD = "gridsettings.xsd";

	@Override
	public ElementRawData getFactorySource(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final GridRequestedSettings aSettings) {
		check(elementInfo);
		setElementInfo(elementInfo);
		setContext(context);
		try {
			if (aSettings != null) {
				aSettings.normalize();
			}

			prepareStdStatement();
			setupSorting(getCs(), aSettings);
			getCs().setInt(FIRST_RECORD_TAG, 1);
			getCs().setInt(PAGESIZE_TAG, 0); // TODO перейти на 2 вызова
			stdGetResults();
			return new ElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	private void setupSorting(final CallableStatement cs, final GridRequestedSettings settings)
			throws SQLException {
		if ((settings == null) || (settings.getSortedColumns() == null)
				|| (settings.getSortedColumns().size() == 0)) {
			cs.setString(SORT_COLUMNNAME, "");
		} else {
			String sortStatement = "ORDER BY ";
			Iterator<Column> iterator = settings.getSortedColumns().iterator();
			while (iterator.hasNext()) {
				Column col = iterator.next();
				sortStatement =
					sortStatement + String.format("[%s] %s,", col.getId(), col.getSorting());
			}
			sortStatement = sortStatement.substring(0, sortStatement.length() - 1);
			cs.setString(SORT_COLUMNNAME, sortStatement);
		}
	}

	@Override
	public ElementRawData getFactorySource(final CompositeContext aContext,
			final DataPanelElementInfo aElement) {
		return getFactorySource(aContext, aElement, null);
	}

	@Override
	public String getOutSettingsParam() {
		return OUTPUT_COLUMNNAME;
	}

	@Override
	protected String getSqlTemplate() {
		return "exec [dbo].[%s] ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.GRID;
	}

	@Override
	protected String getSettingsSchema() {
		return GRIDSETTINGS_XSD;
	}
}
