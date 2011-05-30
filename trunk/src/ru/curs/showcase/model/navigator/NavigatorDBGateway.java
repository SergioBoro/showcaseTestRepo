package ru.curs.showcase.model.navigator;

import java.io.InputStream;
import java.sql.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPCallHelper;
import ru.curs.showcase.util.*;

/**
 * Шлюз к хранимой процедуре в БД, возвращающей данные для навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorDBGateway extends SPCallHelper implements NavigatorGateway {
	static final String NAVIGATOR_PROCNAME_PARAM = "navigator.proc.name";
	static final String DEF_USERNAME_PARAM = "main.def.username";

	@Override
	public InputStream getData(final CompositeContext context) {
		setDb(ConnectionFactory.getConnection());
		String procName = AppProps.getRequiredValueByName(NAVIGATOR_PROCNAME_PARAM);
		try {
			String sql = String.format(getSqlTemplate(), procName);
			CallableStatement cs = getDb().prepareCall(sql);
			cs.setString(SESSION_CONTEXT_PARAM, context.getSession());
			cs.registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
			cs.execute();
			InputStream stream = cs.getSQLXML(getOutSettingsParam()).getBinaryStream();
			return stream;
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	public String getOutSettingsParam() {
		return NAVIGATOR_TAG;
	}

	@Override
	protected String getSqlTemplate() {
		return "exec [dbo].[%s] ?, ?";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return null;
		// для навигатора не имеет смысл. В будущем неплохо бы ввести особый тип
		// или изменить иерархию наследования.
	}

	@Override
	protected String getSettingsSchema() {
		// TODO перенести проверку схемы в шлюз
		return null;
	}
}
