package ru.curs.showcase.util;

import java.io.File;

import ru.curs.showcase.exception.SettingsFileOpenException;
import ru.curs.showcase.model.SettingsFileType;

/**
 * Источник схем из userdata.
 * 
 * @author den
 * 
 */
public final class UserDataXSDSource implements XSDSource {

	@Override
	public File getSchema(final String aFileName) {
		String xsdFullFileName =
			String.format("%s/%s/%s", AppProps.getUserDataCatalog(), AppProps.SCHEMASDIR,
					aFileName);
		File file = new File(xsdFullFileName);
		if (!file.exists()) {
			throw new SettingsFileOpenException(xsdFullFileName, SettingsFileType.SCHEMA);
		}
		return file;
	}

}
