package ru.curs.showcase.model.geomap;

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
public class GeoMapDBGateway extends CompBasedElementSPCallHelper implements GeoMapGateway {
	protected static final String MAPSETTINGS_XSD = "mapsettings.xsd";
	public static final String OUTPUT_COLUMNNAME = "geomapsettings";

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
		return DataPanelElementType.GEOMAP;
	}

	@Override
	protected String getSettingsSchema() {
		return MAPSETTINGS_XSD;
	}
}
