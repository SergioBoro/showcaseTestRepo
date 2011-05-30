package ru.curs.showcase.model.webtext;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;

/**
 * Реализация шлюза к БД для получения данных типа WebText.
 * 
 * @author den
 * 
 */
public class WebTextDBGateway extends HTMLBasedSPCallHelper implements WebTextGateway {
	public static final String OUTPUT_COLUMNNAME = "webtextsettings";
	static final String DATA_COLUMNNAME = "webtextdata";
	protected static final String WEBTEXTSETTINGS_XSD = "webtextsettings.xsd";

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		setElementInfo(elementInfo);
		setContext(context);

		return stdGetData();
	}

	@Override
	protected String getSqlTemplate() {
		return "{call [dbo].[%s](?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	public String getOutSettingsParam() {
		return OUTPUT_COLUMNNAME;
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.WEBTEXT;
	}

	@Override
	public String getDataParam() {
		return DATA_COLUMNNAME;
	}

	@Override
	protected String getSettingsSchema() {
		return WEBTEXTSETTINGS_XSD;
	}
}
