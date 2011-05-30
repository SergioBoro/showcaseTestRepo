package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.util.*;

/**
 * Тесты класса XMLProcessor.
 */
public class XMLUtilsTest extends AbstractTestBasedOnFiles {

	/**
	 * Получает выходной SQLXML по входному.
	 * 
	 * @param connection
	 *            - соединение.
	 * @param sqlxmlIn
	 *            - входной SQLXML.
	 * @return - выходной SQLXML.
	 * @throws SQLException
	 */
	private static SQLXML
			getOutputByInputSQLXML(final Connection connection, final SQLXML sqlxmlIn)
					throws SQLException {

		Statement st = connection.createStatement();

		String stmt = "DROP PROCEDURE [dbo].[_DebugXMLProcessor2]";
		try {
			st.executeUpdate(stmt);
		} catch (Exception e) {
			stmt = "";
		}
		stmt =
			"CREATE PROCEDURE [dbo].[_DebugXMLProcessor2] @par1 xml, @par2 xml Output AS set @par2 = @par1";
		st.executeUpdate(stmt);

		stmt = "{call _DebugXMLProcessor2(?,?)}";
		CallableStatement cs = connection.prepareCall(stmt);
		cs.setSQLXML(1, sqlxmlIn);
		cs.registerOutParameter(2, java.sql.Types.SQLXML);
		cs.execute();

		SQLXML sqlxmlOut = cs.getSQLXML(2);
		return sqlxmlOut;
	}

	/**
	 * Тест1 ф-ции xsltTransform.
	 */
	@Test
	public final void test1XsltTransform() throws SAXException, IOException, SQLException,
			TransformerException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"));

		Connection connection = ConnectionFactory.getConnection();

		SQLXML sqlxmlIn = XMLUtils.getDOMToSQLXML(doc, connection);

		SQLXML sqlxmlOut = getOutputByInputSQLXML(connection, sqlxmlIn);

		String xsltFileName = "test_good.xsl";

		String out = XMLUtils.xsltTransform(sqlxmlOut, xsltFileName);

		assertTrue(out.indexOf(">II. Индикаторы задач проекта</td>") > -1);

		assertTrue(out.indexOf("учреждениях") > -1);

	}

	/**
	 * Тест2 ф-ции xsltTransform.
	 * 
	 * @throws TransformerException
	 * @throws SQLException
	 */
	@Test(expected = XSLTTransformException.class)
	public final void test2XsltTransformForCheckSAXON() throws SAXException, IOException,
			SQLException, TransformerException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"));

		Connection connection = ConnectionFactory.getConnection();

		SQLXML sqlxmlIn = XMLUtils.getDOMToSQLXML(doc, connection);

		SQLXML sqlxmlOut = getOutputByInputSQLXML(connection, sqlxmlIn);

		String xsltFileName = "test_bad.xsl";

		String out = XMLUtils.xsltTransform(sqlxmlOut, xsltFileName);

		assertTrue(out.indexOf(">II. Индикаторы задач проекта</td>") > -1);

		assertTrue(out.indexOf("учреждениях") > -1);

	}

	/**
	 * Тест3 ф-ции xsltTransform.
	 */
	@Test
	public final void test3XsltTransform() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"));

		String xsltFileName = "test_good.xsl";

		String out = XMLUtils.xsltTransform(doc, xsltFileName);

		assertTrue(out.indexOf(">II. Индикаторы задач проекта</td>") > -1);

		assertTrue(out.indexOf("учреждениях") > -1);

	}

	/**
	 * Тест4 ф-ции xsltTransform.
	 */
	@Test
	public final void test4XsltTransform() {
		SAXParser saxParser = XMLUtils.createSAXParser();

		String xsltFileName = "test_good.xsl";

		String out =
			XMLUtils.xsltTransform(saxParser,
					XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"), xsltFileName);

		assertTrue(out.indexOf(">II. Индикаторы задач проекта</td>") > -1);

		assertTrue(out.indexOf("учреждениях") > -1);

	}

	/**
	 * Тест5 ф-ции xsltTransform.
	 */
	@Test
	public final void test5XsltTransform() {

		String xsltFileName = "test_good.xsl";

		String out =
			XMLUtils.xsltTransform(XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"),
					xsltFileName);

		// System.out.println(out);

		assertTrue(out.indexOf(">II. Индикаторы задач проекта</td>") > -1);

		assertTrue(out.indexOf("учреждениях") > -1);

	}

	/**
	 * Тест ф-ции xsltTransformForGrid.
	 */
	@Test
	public final void testXsltTransformForGrid() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream("TestGridSample.xml"));

		ByteArrayOutputStream out = XMLUtils.xsltTransformForGrid(doc);
		assertNotNull(out);

		doc = db.parse(new ByteArrayInputStream(out.toByteArray()));

		NodeList nl = doc.getElementsByTagName("Workbook");
		assertEquals(1, nl.getLength());

		nl = doc.getElementsByTagName("Worksheet");
		assertEquals(1, nl.getLength());

		nl = doc.getElementsByTagName("Styles");
		assertEquals(1, nl.getLength());

	}

	/**
	 * Тест11 ф-ции xsdValidate.
	 */
	@Test
	public final void test11ValidateXSD() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"));

		String xsdFileName = "test_good.xsd";

		XMLUtils.xsdValidateUserData(doc, xsdFileName);

	}

	/**
	 * Тест12 ф-ции xsdValidate.
	 */
	@Test(expected = XSDValidateException.class)
	public final void test12ValidateXSD() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"));

		String xsdFileName = "test_bad.xsd";

		XMLUtils.xsdValidateUserData(doc, xsdFileName);

	}

	/**
	 * Тест13 ф-ции xsdValidate.
	 */
	@Test
	public final void test13ValidateXSD() {
		SAXParser saxParser = XMLUtils.createSAXParser();

		String xsdFileName = "test_good.xsd";

		XMLUtils.xsdValidateUserData(saxParser,
				XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"), xsdFileName);

	}

	/**
	 * Тест14 ф-ции xsdValidate.
	 */
	@Test(expected = XSDValidateException.class)
	public final void test14ValidateXSD() {
		SAXParser saxParser = XMLUtils.createSAXParser();

		String xsdFileName = "test_bad.xsd";

		XMLUtils.xsdValidateUserData(saxParser,
				XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"), xsdFileName);
	}

	/**
	 * Тест15 ф-ции xsdValidate.
	 */
	@Test
	public final void test15ValidateXSD() {

		String xsdFileName = "test_good.xsd";

		XMLUtils.xsdValidateUserData(XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"),
				xsdFileName);

	}

	/**
	 * Тест16 ф-ции xsdValidate.
	 */
	@Test(expected = XSDValidateException.class)
	public final void test16ValidateXSD() {

		String xsdFileName = "test_bad.xsd";

		XMLUtils.xsdValidateUserData(XMLUtilsTest.class.getResourceAsStream("TestTextSample.xml"),
				xsdFileName);

	}

	/**
	 * Тест15 ф-ции xsdValidate.
	 */
	@Test
	public final void testDPGoodValidateXSD() {
		String xsdFileName = DataPanelFactory.DATAPANEL_XSD;

		DataPanelGateway gateway = new DataPanelXMLGateway();
		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(gateway.getXML("test.xml").getData(), "test.xml",
				xsdFileName));
	}

	/**
	 * Тест16 ф-ции xsdValidate.
	 */
	@Test(expected = XSDValidateException.class)
	public final void testDPBadValidateXSD() {
		String xsdFileName = DataPanelFactory.DATAPANEL_XSD;

		DataPanelGateway gateway = new DataPanelXMLGateway();
		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(gateway.getXML("test.bad1.xml").getData(),
				"test.bad1.xml", xsdFileName));
	}

}
