package ru.curs.showcase.util;

import java.io.*;
import java.sql.*;

import javax.xml.bind.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.*;
import javax.xml.validation.SchemaFactory;

import net.sf.saxon.om.NamespaceConstant;

import org.w3c.dom.*;
import org.xml.sax.*;

import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.DataFile;

/**
 * Реализует обработку XML (в частности, выполнение XSLT-преобразования).
 * 
 */
public final class XMLUtils {

	/**
	 * Преобразует объект в XML документ.
	 * 
	 * @param object
	 *            - объект.
	 * @return - документ.
	 */
	public static Document objectToXML(final Object object) {
		DocumentBuilder builder = createBuilder();
		Document doc = builder.newDocument();
		try {
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(object, doc);
			return doc;
		} catch (JAXBException e) {
			throw new AppInternalError(e);
		}
	}

	/**
	 * Преобразует XML документ в объект.
	 * 
	 * @param node
	 *            - элемент XML.
	 * @param objectClass
	 *            - класс требуемого объекта.
	 * @return - требуемый объект.
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Object xmlToObject(final Node node, final Class objectClass) {
		try {
			JAXBContext jc = JAXBContext.newInstance(objectClass);
			Unmarshaller um = jc.createUnmarshaller();
			Object res = um.unmarshal(node);
			return res;
		} catch (JAXBException e) {
			throw new AppInternalError(e);
		}
	}

	static final String XSLT_ERROR = "Ошибка при выполнении XSLT-преобразования: ";

	/**
	 * Идентификатор схемы версии 1.1.
	 */
	public static final String XML_SCHEMA_V1_1 = "http://www.w3.org/XML/XMLSchema/v1.1";

	public static final String JAXP_SCHEMA_LANGUAGE =
		"http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	public static final String JAXP_SCHEMA_SOURCE =
		"http://java.sun.com/xml/jaxp/properties/schemaSource";

	private XMLUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Получает "входящий" SQLXML по org.w3c.dom.Document.
	 * 
	 * @param doc
	 *            org.w3c.dom.Document
	 * @param con
	 *            Connection в котором будет создан SQLXML
	 * 
	 * @return SQLXML
	 * 
	 * @throws SQLException
	 *             В случае ошибки
	 * @throws TransformerException
	 *             В случае ошибки
	 */
	public static SQLXML getDOMToSQLXML(final org.w3c.dom.Document doc, final Connection con)
			throws SQLException, TransformerException {
		Transformer tr = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
		SQLXML sqlxml = con.createSQLXML();
		Result result = sqlxml.setResult(null);
		tr.transform(new DOMSource(doc), result);
		return sqlxml;
	}

	/**
	 * Стандартная процедура создания XML Document builder.
	 * 
	 * @return - DocumentBuilder.
	 */
	public static DocumentBuilder createBuilder() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new CreateObjectError(e);
		}
		return builder;
	}

	/**
	 * Стандартная функция для создания SAX XML Parser.
	 * 
	 * @return парсер.
	 */
	public static SAXParser createSAXParser() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		SAXParser parser;
		try {
			factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
			parser = factory.newSAXParser();
		} catch (Throwable e) {
			throw new CreateObjectError(e);
		}
		return parser;
	}

	private static Transformer createTransformer(final String xsltFileName)
			throws TransformerConfigurationException, IOException {
		Transformer tr;
		if (xsltFileName != null) {
			String xsltFullFileName = AppProps.XSLTTRANSFORMSDIR + "/" + xsltFileName;

			tr =
				javax.xml.transform.TransformerFactory.newInstance().newTransformer(
						new StreamSource(AppProps.loadUserDataToStream(xsltFullFileName)));
		} else {
			tr = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
		}
		setupStdTransformerParams(tr, true);
		return tr;
	}

	private static void setupStdTransformerParams(final Transformer tr,
			final boolean excludeXmlDecl) {
		if (excludeXmlDecl) {
			tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		}
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty(OutputKeys.ENCODING, TextUtils.DEF_ENCODING);
		tr.setOutputProperty(OutputKeys.METHOD, "xml");
	}

	/**
	 * Функция сохранения строки в XML файл. Функция сначала преобразует строку
	 * в XML документ в целях проверки.
	 * 
	 * @param content
	 *            - содержимое строки.
	 * @param filename
	 *            - имя файла для сохранения.
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public static void stringToXMLFile(final String content, final String filename)
			throws SAXException, IOException, TransformerException {
		Document doc = stringToDocument(content);
		Transformer tr = createTransformer(null);
		tr.transform(new DOMSource(doc), new StreamResult(new File(filename)));
	}

	/**
	 * Функция создает XML документ по строке.
	 * 
	 * @param content
	 *            - строка.
	 * @return - XML документ.
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document stringToDocument(final String content) throws SAXException, IOException {
		InputSource is = new InputSource(new StringReader(content));
		DocumentBuilder db = createBuilder();
		Document doc = db.parse(is);
		return doc;
	}

	/**
	 * Выполняет XSLT-преобразование.
	 * 
	 * @param sqlxml
	 *            SQLXML для преобразования
	 * @param xsltFileName
	 *            Имя файла XSLT-преобразования
	 * 
	 * @return Преобразованный XML
	 * 
	 */
	public static String xsltTransform(final SQLXML sqlxml, final String xsltFileName) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer tr = createTransformer(xsltFileName);
			tr.transform(sqlxml.getSource(DOMSource.class), new StreamResult(baos));
			return baos.toString(TextUtils.DEF_ENCODING);
		} catch (Exception e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage());
		}
	}

	/**
	 * Выполняет XSLT-преобразование.
	 * 
	 * @param doc
	 *            org.w3c.dom.Document для преобразования
	 * @param xsltFileName
	 *            Имя файла XSLT-преобразования
	 * 
	 * @return Преобразованный XML
	 * 
	 */
	public static String xsltTransform(final org.w3c.dom.Document doc, final String xsltFileName) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer tr = createTransformer(xsltFileName);
			tr.transform(new DOMSource(doc), new StreamResult(baos));
			return baos.toString(TextUtils.DEF_ENCODING);
		} catch (Exception e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage());
		}
	}

	/**
	 * Преобразует документ XML в строку.
	 * 
	 * @param doc
	 *            - исходный документ.
	 * @return - строка.
	 * @throws XSLTTransformException
	 */
	public static String documentToString(final Document doc) {
		return xsltTransform(doc, null);
	}

	/**
	 * Выполняет XSLT-преобразование.
	 * 
	 * @param parser
	 *            SAXParser
	 * 
	 * @param is
	 *            InputStream для преобразования
	 * 
	 * @param xsltFileName
	 *            Имя файла XSLT-преобразования
	 * 
	 * @return Преобразованный XML
	 * 
	 */
	public static String xsltTransform(final SAXParser parser, final InputStream is,
			final String xsltFileName) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer tr = createTransformer(xsltFileName);
			tr.transform(new SAXSource(parser.getXMLReader(), new InputSource(is)),
					new StreamResult(baos));
			return baos.toString(TextUtils.DEF_ENCODING);
		} catch (Exception e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage());
		}
	}

	/**
	 * Выполняет XSLT-преобразование.
	 * 
	 * @param is
	 *            InputStream для преобразования
	 * 
	 * @param xsltFileName
	 *            Имя файла XSLT-преобразования
	 * 
	 * @return Преобразованный XML
	 * 
	 */
	public static String xsltTransform(final InputStream is, final String xsltFileName) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer tr = createTransformer(xsltFileName);
			tr.transform(new StreamSource(is), new StreamResult(baos));
			return baos.toString(TextUtils.DEF_ENCODING);
		} catch (Exception e) {
			throw new XSLTTransformException(XSLT_ERROR + e.getMessage());
		}
	}

	/**
	 * Выполняет XSLT-преобразование для грида.
	 * 
	 * @param doc
	 *            org.w3c.dom.Document для преобразования
	 * 
	 * @return OutputStream с преобразованным XML
	 * 
	 */
	public static ByteArrayOutputStream xsltTransformForGrid(final org.w3c.dom.Document doc) {
		try {
			String xsltFullFileName =
				AppProps.XSLTTRANSFORMSFORGRIDDIR + "/" + AppProps.GRIDDATAXSL;

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			Transformer tr =
				javax.xml.transform.TransformerFactory.newInstance().newTransformer(
						new StreamSource(AppProps.loadUserDataToStream(xsltFullFileName)));
			setupStdTransformerParams(tr, false);

			tr.transform(new DOMSource(doc), new StreamResult(baos));

			return baos;
		} catch (Exception e) {
			throw new XSLTTransformException(
					"Ошибка при выполнении XSLT-преобразования для таблицы: " + e.getMessage());
		}
	}

	/**
	 * Schema full checking feature id
	 * (http://apache.org/xml/features/validation/schema-full-checking).
	 */
	protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID =
		"http://apache.org/xml/features/validation/schema-full-checking";

	/**
	 * Honour all schema locations feature id
	 * (http://apache.org/xml/features/honour-all-schemaLocations).
	 */
	protected static final String HONOUR_ALL_SCHEMA_LOCATIONS_ID =
		"http://apache.org/xml/features/honour-all-schemaLocations";

	/**
	 * Validate schema annotations feature id
	 * (http://apache.org/xml/features/validate-annotations).
	 */
	protected static final String VALIDATE_ANNOTATIONS_ID =
		"http://apache.org/xml/features/validate-annotations";

	/**
	 * Generate synthetic schema annotations feature id
	 * (http://apache.org/xml/features/generate-synthetic-annotations).
	 */
	protected static final String GENERATE_SYNTHETIC_ANNOTATIONS_ID =
		"http://apache.org/xml/features/generate-synthetic-annotations";

	/**
	 * Создает стандартную SchemaFactory.
	 * 
	 * @return - SchemaFactory.
	 */
	static SchemaFactory createSchemaFactory() {
		boolean schemaFullChecking = false;
		boolean honourAllSchemaLocations = false;
		boolean validateAnnotations = false;
		boolean generateSyntheticAnnotations = false;

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XML_SCHEMA_V1_1);
		// XMLConstants.W3C_XML_SCHEMA_NS_URI

		try {
			schemaFactory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
			schemaFactory.setFeature(HONOUR_ALL_SCHEMA_LOCATIONS_ID, honourAllSchemaLocations);
			schemaFactory.setFeature(VALIDATE_ANNOTATIONS_ID, validateAnnotations);
			schemaFactory.setFeature(GENERATE_SYNTHETIC_ANNOTATIONS_ID,
					generateSyntheticAnnotations);
		} catch (Exception e) {
			throw new CreateObjectError(e);
		}
		return schemaFactory;
	}

	/**
	 * Настраивает название класса "правильной" SchemaFactory - с поддержкой
	 * XML_SCHEMA_V1_1.
	 */
	public static void setupSchemaFactory() {
		// System.setProperty("jaxp.debug", "jaxp.debug");
		System.setProperty("javax.xml.validation.SchemaFactory" + ":" + XML_SCHEMA_V1_1,
				"org.apache.xerces.jaxp.validation.XMLSchema11Factory");
	}

	/**
	 * Настраивает "правильный" трансформер XSL - SAXON.
	 */
	public static void setupTransformer() {
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_SAXON,
				"net.sf.saxon.xpath.XPathFactoryImpl");
	}

	/**
	 * Выполняет XSD-проверку пользовательского документа.
	 * 
	 * @param doc
	 *            org.w3c.dom.Document для проверки
	 * @param xsdFileName
	 *            Имя файла XSD-схемы
	 * 
	 */
	public static void
			xsdValidateUserData(final org.w3c.dom.Document doc, final String xsdFileName) {
		XMLValidator validator = new XMLValidator(new UserDataXSDSource());
		validator.validate(new XMLSource(doc, xsdFileName));
	}

	/**
	 * Выполняет XSD-проверку пользовательского документа.
	 * 
	 * @param parser
	 *            SAXParser
	 * @param is
	 *            InputStream для проверки
	 * @param xsdFileName
	 *            Имя файла XSD-схемы
	 * 
	 */
	public static void xsdValidateUserData(final SAXParser parser, final InputStream is,
			final String xsdFileName) {
		XMLValidator validator = new XMLValidator(new UserDataXSDSource());
		validator.validate(new XMLSource(is, parser, xsdFileName));
	}

	/**
	 * Функция проверки XML документа, позволяющая повторное чтение из
	 * переданного InputStream. Повторное чтение обеспечивается с помощью
	 * клонирования InputStream.
	 * 
	 * @param stream
	 *            - входной поток с XML документом.
	 * @param schemaFile
	 *            - наименование файла схемы.
	 * @return - входной поток, из которого можно читать.
	 */
	public static InputStream validateXMLStream(final InputStream stream, final String schemaFile) {
		StreamConvertor duplicator;
		try {
			duplicator = new StreamConvertor(stream);
		} catch (IOException e) {
			throw new CreateObjectError(e);
		}
		InputStream stream1 = duplicator.getCopy();
		InputStream stream2 = duplicator.getCopy();

		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(stream1, schemaFile));
		return stream2;
	}

	/**
	 * Выполняет XSD-проверку пользовательского документа.
	 * 
	 * @param is
	 *            InputStream для проверки
	 * @param xsdFileName
	 *            Имя файла XSD-схемы
	 * 
	 */
	public static void xsdValidateUserData(final InputStream is, final String xsdFileName) {
		XMLValidator validator = new XMLValidator(new UserDataXSDSource());
		validator.validate(new XMLSource(is, xsdFileName));
	}

	/**
	 * Стандартный обработчик ошибки в SAX парсере, корректно обрабатывающий
	 * специальный тип SAXError.
	 * 
	 * @param e
	 *            - исключение.
	 * 
	 * @param errorMes
	 *            - сообщение об ошибке.
	 */
	public static void stdSAXErrorHandler(final Throwable e, final String errorMes) {
		Throwable realExc = e;
		if (e.getCause() != null) {
			if (e.getCause().getClass() == SAXError.class) {
				realExc = e.getCause().getCause();
			}
		}
		throw new XMLFormatException(errorMes, realExc);
	}

	/**
	 * Создает пустой документ с заданным именем корневого элемента.
	 * 
	 * @param rootTag
	 *            - имя корневого элемента.
	 * @return - документ.
	 */
	public static Document createEmptyDoc(final String rootTag) {
		return createBuilder().getDOMImplementation().createDocument("", rootTag, null);
	}

	/**
	 * Строит строку содержащую стартовый тэг с атрибутами для SAX парсера.
	 * 
	 * @param qname
	 *            - имя тэга.
	 * @param attrs
	 *            - атрибуты.
	 * @return - строка.
	 */
	public static String saxTagWithAttrsToString(final String qname, final Attributes attrs) {
		String attrsString = " ";
		for (int i = 0; i < attrs.getLength(); i++) {
			String name = attrs.getQName(i);
			String value = attrs.getValue(i);
			attrsString = attrsString + String.format("%s=\"%s\" ", name, value);
		}
		return "<" + qname + attrsString + ">";
	}

	/**
	 * Функция проверки XML документа, переданного посредством DataFile,
	 * позволяющая повторное чтение из переданного InputStream. Повторное чтение
	 * обеспечивается с помощью клонирования InputStream.
	 * 
	 * @param file
	 *            - файл с XML документом.
	 * @param schema
	 *            - наименование файла схемы.
	 */
	public static void validateXMLStream(final DataFile<InputStream> file, final String schema) {
		StreamConvertor duplicator;
		try {
			duplicator = new StreamConvertor(file.getData());
		} catch (IOException e) {
			throw new CreateObjectError(e);
		}
		InputStream stream1 = duplicator.getCopy();
		file.setData(duplicator.getCopy());

		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(stream1, file.getName(), schema));
	}
}
