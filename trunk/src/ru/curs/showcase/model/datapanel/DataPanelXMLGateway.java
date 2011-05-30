package ru.curs.showcase.model.datapanel;

import java.io.*;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

/**
 * Шлюз для получения данных об информационных панелях из файловой системы.
 * 
 * @author den
 * 
 */
public class DataPanelXMLGateway extends GeneralXMLHelper implements DataPanelGateway {
	/**
	 * Идентификатор первой вкладки.
	 */
	private String firstTabId = null;

	/**
	 * Название параметра в файле настроек приложение, содержащего путь к
	 * каталогу с файлами информационных панелей.
	 */
	public static final String DP_STORAGE_PARAM_NAME = "datapanelstorage";

	@Override
	public String getFirstTabId(final String dataPanelId) {

		DefaultHandler myHandler = new DefaultHandler() {
			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (firstTabId == null) {
					firstTabId = attrs.getValue(ID_TAG);
				}
			}
		};
		DataFile<InputStream> file = getXML(dataPanelId);

		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(file.getData(), myHandler);
		} catch (Throwable e) {
			XMLUtils.stdSAXErrorHandler(e, dataPanelId);
		}

		if (firstTabId == null) {
			throw new XMLFormatException(dataPanelId);
		}
		return firstTabId;
	}

	@Override
	public DataFile<InputStream> getXML(final String dataPanelId) {
		InputStream result;
		try {
			result =
				AppProps.loadUserDataToStream(String.format("%s/%s", DP_STORAGE_PARAM_NAME,
						dataPanelId));
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, dataPanelId, SettingsFileType.DATAPANEL);
		}
		if (result == null) {
			throw new SettingsFileOpenException(dataPanelId, SettingsFileType.DATAPANEL);
		}
		return new DataFile<InputStream>(result, dataPanelId);
	}
}
