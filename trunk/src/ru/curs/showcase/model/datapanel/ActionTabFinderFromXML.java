package ru.curs.showcase.model.datapanel;

import ru.curs.showcase.app.api.event.DataPanelLink;
import ru.curs.showcase.model.ActionTabFinder;

/**
 * Реализация интерфейса поиска вкладки для действия в инф. панели, хранимой в
 * XML.
 * 
 * @author den
 * 
 */
public class ActionTabFinderFromXML extends ActionTabFinder {

	@Override
	public String getFirstFromStorage(final DataPanelLink link) {
		DataPanelGateway dpGateway = new DataPanelXMLGateway();
		return dpGateway.getFirstTabId(link.getDataPanelId());
	}

}
