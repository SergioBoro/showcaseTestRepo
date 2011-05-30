package ru.curs.showcase.model.chart;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;

/**
 * Шлюз для получения данных для графика из БД.
 * 
 * @author den
 * 
 */
public class ChartDBGateway extends CompBasedElementSPCallHelper implements ChartGateway {
	public static final String OUTPUT_COLUMNNAME = "chartsettings";
	protected static final String CHARTSETTINGS_XSD = "chartsettings.xsd";

	@Override
	public ElementRawData getFactorySource(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		check(elementInfo);
		setElementInfo(elementInfo);
		setContext(context);
		try {
			prepareStdStatement();
			stdGetResults();
			return new ElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	public String getOutSettingsParam() {
		return OUTPUT_COLUMNNAME;
	}

	@Override
	protected String getSqlTemplate() {
		return "exec [dbo].[%s] ?, ?, ?, ?, ?, ?";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.CHART;
	}

	@Override
	protected String getSettingsSchema() {
		return CHARTSETTINGS_XSD;
	}
}
