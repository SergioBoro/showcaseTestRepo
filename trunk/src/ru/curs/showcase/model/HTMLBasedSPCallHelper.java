package ru.curs.showcase.model;

import java.io.InputStream;
import java.sql.*;

import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;

/**
 * Вспомогательный класс для работы с хранимыми процедурами получения данных для
 * построения основанных на HTML элементах.
 * 
 * @author den
 * 
 */
public abstract class HTMLBasedSPCallHelper extends SPCallHelper {
	/**
	 * Возвращает имя OUT параметра с данными элемента. Необходим только для
	 * HTML-based элементов.
	 * 
	 * @return - имя параметра.
	 */
	public abstract String getDataParam();

	@Override
	protected void prepareStdStatement() throws SQLException {
		super.prepareStdStatement();
		getCs().registerOutParameter(getDataParam(), java.sql.Types.SQLXML);
	}

	/**
	 * Возвращает данные для элемента, основанного на HTML.
	 * 
	 * @return - сырые данные
	 */
	protected HTMLBasedElementRawData stdGetData() {
		check(getElementInfo());
		if (getElementInfo().getProcName() == null) {
			return new HTMLBasedElementRawData(getElementInfo());
		}

		try {
			try {
				prepareStdStatement();
				Document data = null;
				getCs().execute();
				SQLXML xml = getCs().getSQLXML(getDataParam());
				if (xml != null) {
					DOMSource domSource = xml.getSource(DOMSource.class);
					data = (Document) domSource.getNode();
				}
				InputStream validatedSettings = getSettingsStream();
				return new HTMLBasedElementRawData(data, validatedSettings, getElementInfo(),
						getContext());
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
		return null;
	}

}
