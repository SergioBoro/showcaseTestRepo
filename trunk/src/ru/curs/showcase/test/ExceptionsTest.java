package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import ru.curs.showcase.app.api.MessageType;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.model.webtext.*;
import ru.curs.showcase.util.*;

/**
 * Тесты для серверных исключений.
 * 
 * @author den
 * 
 */
public class ExceptionsTest extends AbstractTestBasedOnFiles {
	/**
	 * Имя несуществующей схемы.
	 */
	private static final String PHANTOM_XSD = "phantom26052011.xsd";

	/**
	 * Тест на считывание несуществующего параметра из файла настроек.
	 * 
	 */
	@Test(expected = SettingsFileRequiredPropException.class)
	public final void testReadNotExistingValue() {
		AppProps.getRequiredValueByName("blabla");
	}

	/**
	 * Тест на считывание параметра в неверном формате из файла настроек.
	 * 
	 */
	@Test(expected = SettingsFilePropValueFormatException.class)
	public final void testReadWrongValue() {
		GridProps gp = new GridProps(AbstractGridFactory.GRID_DEFAULT_PROFILE);
		gp.stdReadIntGridValue("def.column.hor.align");
	}

	/**
	 * Тест на несуществующую информационную панель.
	 * 
	 */
	@Test(expected = SettingsFileOpenException.class)
	public final void testWrongDP() {
		DataPanelGateway gateway = new DataPanelXMLGateway();
		gateway.getXML("verysecretandhidden.xml");
	}

	/**
	 * Проверка GeneralServerException, вызванного
	 * DataPanelFileNotFoundException.
	 */
	@Test
	public final void testWrongDPByServiceLayer() {
		Action action = new Action();
		action.setDataPanelActionType(DataPanelActionType.RELOAD_PANEL);
		action.setNavigatorActionType(NavigatorActionType.DO_NOTHING);
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("verysecretandhidden.xml");
		dpLink.setTabId("1");
		action.setDataPanelLink(dpLink);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getDataPanel(action);
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(SettingsFileOpenException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			assertNotNull(((GeneralServerException) e).getOriginalTrace());
			assertNotNull(((GeneralServerException) e).getOriginalMessage());
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за несуществующей хранимой процедуры.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWrongChartSP() throws IOException {
		CompositeContext context = getContext("tree_multilevel.v2.xml", 1, 0);
		DataPanelElementInfo element = getDPElement("test2.xml", "3", "31");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getChart(context, element);
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(DBQueryException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			assertTrue(((GeneralServerException) e).getOriginalMessage().indexOf(
					"chart_pas_phantom") > -1);
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за хранимой процедуры, не вернувшей данные.
	 * 
	 */
	@Test
	public final void testWrongChartSPWithNoResult() throws IOException {
		CompositeContext context = getContext("tree_multilevel.v2.xml", 1, 0);
		DataPanelElementInfo element = getDPElement("test2.xml", "3", "32");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getChart(context, element);
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(DBQueryException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			assertTrue(e.getMessage().indexOf(SPCallHelper.NO_RESULTSET_ERROR) > -1);
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за хранимой процедуры, не вернувшей данные.
	 */
	@Test
	public final void testWrongChartSPForSubmission() {

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.handleSQLSubmission("no_exist_proc", "fake_data");
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(DBQueryException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			assertTrue(((GeneralServerException) e).getOriginalMessage().indexOf("no_exist_proc") > -1);
			return;
		}
		fail();
	}

	/**
	 * Проверка на ошибку при передаче WebText с неполной информацией.
	 * 
	 */
	@Test
	public final void testWrongElement() throws IOException {
		CompositeContext context = getContext("tree_multilevel.v2.xml", 1, 0);
		DataPanelElementInfo element = new DataPanelElementInfo();
		element.setId("11");
		element.setType(DataPanelElementType.WEBTEXT);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getWebText(context, element);
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(IncorrectElementException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			return;
		}
		fail();
	}

	/**
	 * Тест на срабатывание проверки на ввод неверного autoSelectRecordId.
	 * 
	 * @throws Exception
	 */
	@Test(expected = InconsistentSettingsFromDBException.class)
	public void testInconsistentSettings() throws Exception {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "3", "5");

		GridGateway gateway = new GridDBGateway();
		ElementRawData raw = gateway.getFactorySource(context, element);
		GridDBFactory factory = new GridDBFactory(raw, null);
		factory.build();
	}

	/**
	 * Тест проверки схемы XSD для неверного элемента.
	 * 
	 */
	@Test(expected = XSDValidateException.class)
	public void testXSDValidateException() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "3", "6");

		WebTextGateway gateway = new WebTextDBGateway();
		gateway.getRawData(context, element);
	}

	/**
	 * Пытается проверить XML несуществующей пользовательской схемой.
	 */
	@Test(expected = SettingsFileOpenException.class)
	public void testUserXSDNotFoundException() {
		XMLUtils.xsdValidateUserData(AppProps.loadResToStream("log4j.xml"), PHANTOM_XSD);
	}

	/**
	 * Пытается проверить XML несуществующей системной схемой.
	 */
	@Test(expected = SettingsFileOpenException.class)
	public void testXSDNotFoundException() {
		XMLUtils.xsdValidateUserData(AppProps.loadResToStream("log4j.xml"), PHANTOM_XSD);
	}

	/**
	 * Функция проверки функционала SolutionDBException.
	 */
	@Test
	public void testSolutionException() {
		SQLException exc = new SQLException(SolutionDBException.SOL_MES_PREFIX);
		assertFalse(SolutionDBException.isSolutionDBException(exc));
		exc =
			new SQLException(String.format("%stest1%s", SolutionDBException.SOL_MES_PREFIX,
					SolutionDBException.SOL_MES_SUFFIX));
		assertTrue(SolutionDBException.isSolutionDBException(exc));
		SolutionDBException solEx = new SolutionDBException(exc);
		assertNotNull(solEx.getSolutionMessage());
		assertEquals("test1", solEx.getSolutionMessage().getId());
		assertEquals(MessageType.ERROR, solEx.getSolutionMessage().getType());
		assertEquals("Ошибка", solEx.getSolutionMessage().getText());
		exc =
			new SQLException(String.format("%stest2%s", SolutionDBException.SOL_MES_PREFIX,
					SolutionDBException.SOL_MES_SUFFIX));
		solEx = new SolutionDBException(exc);
		assertEquals("Предупреждение", solEx.getSolutionMessage().getText());
	}

	/**
	 * Проверка случая, когда из БД приходит ссылка на несуществующее сообщение
	 * решения.
	 */
	@Test(expected = SettingsFileRequiredPropException.class)
	public void testSolutionExceptionMesNotFound() {
		SQLException exc =
			new SQLException(String.format("%stestN%s", SolutionDBException.SOL_MES_PREFIX,
					SolutionDBException.SOL_MES_SUFFIX));
		new SolutionDBException(exc);
	}

	/**
	 * Проверка обработки SolutionDBException на сервисном уровне.
	 */
	@Test
	public void testSolutionExceptionBySL() {
		SQLException exc =
			new SQLException(String.format("%stest1%s", SolutionDBException.SOL_MES_PREFIX,
					SolutionDBException.SOL_MES_SUFFIX));
		SolutionDBException solEx = new SolutionDBException(exc);
		GeneralServerException gse =
			new GeneralServerException(solEx, "", solEx.getSolutionMessage());
		assertTrue(gse.isSolutionMessage());
		assertEquals("Ошибка", solEx.getSolutionMessage().getText());
	}

}
