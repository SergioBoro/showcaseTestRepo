package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.model.datapanel.DataPanelXMLGateway;
import ru.curs.showcase.security.SecurityParamsFactory;
import ru.curs.showcase.util.*;

/**
 * Тесты класса AppProps.
 */
public class AppPropsTest extends AbstractTestBasedOnFiles {

	/**
	 * Тест ф-ции loadResToStream.
	 */
	@Test
	public final void testLoadResToStream() {
		assertNotNull(AppProps.loadResToStream(AppProps.PATH_PROPERTIES));
	}

	/**
	 * Тест ф-ции loadUserDataToStream и получение пути к каталогу с
	 * пользовательскими данными.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testLoadUserDataToStream() throws IOException {
		assertNotNull(AppProps.loadUserDataToStream(AppProps.PROPFILENAME));
	}

	/**
	 * Тест ф-ции getValueByName.
	 * 
	 */
	@Test
	public final void testGetValueByName() {
		AppProps.getRequiredValueByName(SecurityParamsFactory.AUTH_SERVER_URL_PARAM);
		AppProps.getRequiredValueByName(ConnectionFactory.CONNECTION_URL_PARAM);
		assertNotNull(AppProps.getOptionalValueByName(ConnectionFactory.CONNECTION_URL_PARAM));
	}

	/**
	 * Тест ф-ции getValueByName.
	 * 
	 */
	@Test
	public final void testDirExists() {
		checkDir(AppProps.XSLTTRANSFORMSDIR);
		checkDir(AppProps.XSLTTRANSFORMSFORGRIDDIR);
		checkDir(DataPanelXMLGateway.DP_STORAGE_PARAM_NAME);
		checkDir(NAVIGATORSTORAGE);
		checkDir(AppProps.SCHEMASDIR);
		checkDir(AppProps.XFORMS_DIR);

		assertTrue((new File(AppProps.getResURL(AppProps.SCHEMASDIR).getFile())).exists());
	}

	private void checkDir(final String dirName) {
		File dir = new File(AppProps.getUserDataCatalog() + File.separator + dirName);
		assertTrue(dir.exists());
	}
}
