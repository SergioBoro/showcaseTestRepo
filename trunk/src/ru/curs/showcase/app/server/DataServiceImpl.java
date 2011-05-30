package ru.curs.showcase.app.server;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.services.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service. Является декоратором для
 * сервисного слоя приложения.
 */
@SuppressWarnings("serial")
public class DataServiceImpl extends RemoteServiceServlet implements DataService {

	private ServiceLayerDataServiceImpl getServiceLayer() {
		return new ServiceLayerDataServiceImpl(getSessionId());
	}

	@Override
	public Navigator getNavigator() throws GeneralServerException {
		return getServiceLayer().getNavigator();
	}

	private String getSessionId() {
		return perThreadRequest.get().getSession().getId();
	}

	@Override
	public DataPanel getDataPanel(final Action action) throws GeneralServerException {
		return getServiceLayer().getDataPanel(action);
	}

	@Override
	public WebText getWebText(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralServerException {
		return getServiceLayer().getWebText(context, element);
	}

	@Override
	public Grid getGrid(final CompositeContext context, final DataPanelElementInfo element,
			final GridRequestedSettings aSettings) throws GeneralServerException {
		return getServiceLayer().getGrid(context, element, aSettings);
	}

	@Override
	public Chart getChart(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralServerException {
		return getServiceLayer().getChart(context, element);
	}

	@Override
	public void saveColumnSet(final ColumnSet aCs) throws GeneralServerException {
	}

	@Override
	public GeoMap getGeoMap(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralServerException {
		return getServiceLayer().getGeoMap(context, element);
	}

	@Override
	public XForms getXForms(final CompositeContext context, final DataPanelElementInfo element,
			final String currentData) throws GeneralServerException {
		return getServiceLayer().getXForms(context, element, currentData);
	}

	@Override
	public CommandResult saveXForms(final CompositeContext context,
			final DataPanelElementInfo element, final String data) throws GeneralServerException {
		return getServiceLayer().saveXForms(context, element, data);
	}

	@Override
	public ServerCurrentState getServerCurrentState() throws GeneralServerException {
		return getServiceLayer().getServerCurrentState();
	}
}
