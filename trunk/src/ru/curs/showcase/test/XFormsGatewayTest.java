package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Test;
import org.w3c.dom.*;

import ru.curs.showcase.app.api.CommandResult;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.exception.XSDValidateException;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.xform.*;
import ru.curs.showcase.util.*;

/**
 * Тест для шлюзов XForms.
 * 
 * @author den
 * 
 */
public class XFormsGatewayTest extends AbstractTestBasedOnFiles {
	/**
	 * Тест для чтения из файла.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFileGateWay() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "07");

		XFormsGateway gateway = new XFormsFileGateway();
		gateway.getInitialData(context, element);
	}

	/**
	 * Тестируем обновление через FileGateway.
	 * 
	 */
	@Test
	public void testFileGatewayUpdate() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "07");

		XFormsGateway gateway = new XFormsFileGateway();
		String content = getNewContentBasedOnExisting(context, element, gateway);
		gateway = new XFormsFileGateway();
		CommandResult res = gateway.saveData(context, element, content);
		assertTrue(res.getSuccess());
		File file =
			new File(String.format("%s/%s/%s_updated.xml", AppProps.getUserDataCatalog(),
					AppProps.XFORMS_DIR, element.getProcName()));
		assertTrue(file.exists());
	}

	/**
	 * Тест на трансформацию шаблона XForms с данными.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFileGateWayWithTransform() throws Exception {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "07");

		XFormsGateway gateway = new XFormsFileGateway();
		HTMLBasedElementRawData raw = gateway.getInitialData(context, element);

		DocumentBuilder db = XMLUtils.createBuilder();
		InputStream stream =
			AppProps.loadUserDataToStream(String.format("%s/%s", AppProps.XFORMS_DIR,
					element.getTemplateName()));
		Document doc = db.parse(stream);
		XFormProducer.getHTML(doc, raw.getData(), element.getId());
	}

	/**
	 * Проверка шлюза к БД.
	 * 
	 */
	@Test
	public void testDBgateway() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "08");

		XFormsGateway gateway = new XFormsDBGateway();
		gateway.getInitialData(context, element);
	}

	/**
	 * Тест сохранения данных через XFormsDBGateway.
	 * 
	 */
	@Test
	public void testDBGatewayUpdate() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "08");

		XFormsGateway gateway = new XFormsDBGateway();
		String content = getNewContentBasedOnExisting(context, element, gateway);
		gateway = new XFormsDBGateway();
		CommandResult res = gateway.saveData(context, element, content);
		assertTrue(res.getSuccess());
	}

	/**
	 * Тест на сохранение данных, который должен вернуть ошибку.
	 * 
	 */
	@Test
	public void testDBGatewayUpdateWithError() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "09");

		XFormsGateway gateway = new XFormsDBGateway();
		String content = getNewContentBasedOnExisting(context, element, gateway);
		gateway = new XFormsDBGateway();
		CommandResult res = gateway.saveData(context, element, content);
		assertFalse(res.getSuccess());
		assertEquals(1, res.getErrorCode().intValue());
		assertEquals("Неуловимая ошибка из БД, связанная с триггерами и блокировками",
				res.getErrorMessage());
	}

	private String getNewContentBasedOnExisting(final CompositeContext context,
			final DataPanelElementInfo element, final XFormsGateway gateway) {
		HTMLBasedElementRawData raw = gateway.getInitialData(context, element);
		Element newChild = raw.getData().createElement("new");
		raw.getData().getDocumentElement().appendChild(newChild);
		String content = XMLUtils.documentToString(raw.getData());
		return content;
	}

	/**
	 * Функция тестирования работы XFormsDBGateway.handleSubmission.
	 * 
	 */
	@Test
	public void testSQLSubmission() {
		String data = "<data>test</data>";
		XFormsGateway gateway = new XFormsDBGateway();
		RequestResult res = gateway.handleSubmission("xforms_submission1", data);
		assertTrue(res.getSuccess());
		assertEquals(data, res.getData());
	}

	/**
	 * Функция тестирования работы SQL Submission через ServiceLayer.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testSQLSubmissionBySL() throws GeneralServerException {
		String data = "<data>test</data>";
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		RequestResult res = sl.handleSQLSubmission("xforms_submission1", data);
		assertTrue(res.getSuccess());
		assertEquals(data, res.getData());
	}

	/**
	 * Функция тестирования работы XSLT Submission через ServiceLayer.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testXSLTSubmissionBySL() throws GeneralServerException {
		String data = "<data>test</data>";
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String res = sl.handleXSLTSubmission("xformsxslttransformation_test.xsl", data);
		assertNotNull(res);
	}

	/**
	 * Проверка файлового шлюза для скачивания данных.
	 * 
	 */
	@Test
	public void testXFormsFileGatewayDownload() {
		XFormsGateway gateway = new XFormsFileGateway();
		final String linkId = "log4j.xml";
		DataFile<ByteArrayOutputStream> file = gateway.downloadFile(null, null, linkId, null);
		assertNotNull(file);
		assertNotNull(file.getData());
		assertEquals(linkId, file.getName());
	}

	/**
	 * Проверка скачивания файла для XForms через ServiceLayer.
	 * 
	 * @throws GeneralServerException
	 * @throws IOException
	 */
	@Test
	public void testXFormsFileDownloadBySL() throws GeneralServerException, IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "09");
		String linkId = "proc4";
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		DataFile<ByteArrayOutputStream> file =
			serviceLayer.getDownloadFile(context, element, linkId, null);
		final int navigatorXMLLen = 193238;
		assertNotNull(context.getSession());
		assertEquals(navigatorXMLLen, file.getData().size());
	}

	/**
	 * Проверка файлового шлюза для закачивания данных.
	 * 
	 */
	@Test
	public void testXFormsFileGatewayUpload() {
		XFormsGateway gateway = new XFormsFileGateway();
		final String linkId = "log4j.xml";
		DataFile<InputStream> file =
			new DataFile<InputStream>(AppProps.loadResToStream(linkId), linkId);
		gateway.uploadFile(null, null, linkId, null, file);
	}

	/**
	 * Проверка закачивания файла из XForms через ServiceLayer.
	 * 
	 * @throws GeneralServerException
	 * @throws IOException
	 */
	@Test
	public void testXFormsFileUploadBySL() throws GeneralServerException, IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "09");
		String linkId = "proc5";
		final String fileName = "log4j.xml";
		DataFile<ByteArrayOutputStream> file = getTestFile(fileName);
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		serviceLayer.uploadFile(context, element, linkId, null, file);
		assertNotNull(context.getSession());
	}

	private DataFile<ByteArrayOutputStream> getTestFile(final String linkId) throws IOException {
		DataFile<ByteArrayOutputStream> file =
			new DataFile<ByteArrayOutputStream>(StreamConvertor.inputToOutputStream(AppProps
					.loadResToStream(linkId)), linkId);
		return file;
	}

	/**
	 * Проверка загрузки на сервер правильного XML.
	 * 
	 * @throws IOException
	 * @throws GeneralServerException
	 */
	@Test
	public void testXFormsXMLUploadGood() throws IOException, GeneralServerException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "09");
		String linkId = "proc7";
		final String fileName = "ru/curs/showcase/test/TestTextSample.xml";
		DataFile<ByteArrayOutputStream> file = getTestFile(fileName);
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		serviceLayer.uploadFile(context, element, linkId, null, file);
		assertNotNull(context.getSession());
	}

	/**
	 * Проверка загрузки на сервер не соответствующего схеме XML.
	 * 
	 * @throws IOException
	 */
	@Test(expected = XSDValidateException.class)
	public void testXFormsXMLUploadBad() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo elementInfo = getDPElement("test1.1.xml", "2", "09");
		String linkId = "proc8";
		final String fileName = "ru/curs/showcase/test/TestTextSample.xml";
		DataFile<ByteArrayOutputStream> file = getTestFile(fileName);

		UserXMLTransformer transformer =
			new UserXMLTransformer(file, elementInfo.getProcs().get(linkId));
		transformer.checkAndTransform();

		XFormsGateway gateway = new XFormsDBGateway();
		gateway.uploadFile(context, elementInfo, linkId, null, transformer.getInputStreamResult());
	}

	/**
	 * Проверка скачивания XML файла для XForms через ServiceLayer.
	 * 
	 * @throws GeneralServerException
	 * @throws IOException
	 */
	@Test
	public void testXFormsXMLDownloadGood() throws GeneralServerException, IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "09");
		String linkId = "proc6";
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		serviceLayer.getDownloadFile(context, element, linkId, null);
	}

	/**
	 * Проверка скачивания XML файла для XForms через ServiceLayer.
	 * 
	 * @throws IOException
	 */
	@Test(expected = XSDValidateException.class)
	public void testXFormsXMLDownloadBad() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo elementInfo = getDPElement("test1.1.xml", "2", "09");
		String linkId = "proc10";

		XFormsGateway gateway = new XFormsDBGateway();
		DataFile<ByteArrayOutputStream> file =
			gateway.downloadFile(context, elementInfo, linkId, null);

		UserXMLTransformer transformer =
			new UserXMLTransformer(file, elementInfo.getProcs().get(linkId));
		transformer.checkAndTransform();
	}

	/**
	 * Тест сохранения данных через XFormsDBGateway c проверкой схемы и
	 * трансформацией.
	 * 
	 */
	@Test
	public void testDBGatewayUpdateWithTransform() throws IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo elementInfo = getDPElement("test1.1.xml", "2", "10");

		XFormsGateway gateway = new XFormsDBGateway();
		String content = getNewContentBasedOnExisting(context, elementInfo, gateway);

		UserXMLTransformer transformer =
			new UserXMLTransformer(content, elementInfo.getSaveProc());
		transformer.checkAndTransform();
		gateway = new XFormsDBGateway();
		CommandResult res = gateway.saveData(context, elementInfo, transformer.getStringResult());

		assertTrue(res.getSuccess());
	}

	/**
	 * Тест сохранения данных через XFormsDBGateway, приводящий к ошибке
	 * проверки XSD.
	 * 
	 */
	@Test(expected = XSDValidateException.class)
	public void testDBGatewayUpdateWithUnSuccessTransform() throws IOException {
		DataPanelElementInfo elementInfo = getDPElement("test1.1.xml", "2", "11");

		String content = "<test>";
		UserXMLTransformer transformer =
			new UserXMLTransformer(content, elementInfo.getSaveProc());
		transformer.checkAndTransform();
	}
}
