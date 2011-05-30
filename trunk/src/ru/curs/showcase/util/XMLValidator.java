package ru.curs.showcase.util;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.validation.*;

import org.xml.sax.SAXException;

import ru.curs.showcase.exception.*;

/**
 * Валидатор схем, позволяющий настроить месторасположение схем и тип исходных
 * данных.
 * 
 * @author den
 * 
 */
public class XMLValidator {
	/**
	 * Источник для схем.
	 */
	private XSDSource xsdSource;

	/**
	 * Функция проверки XML (Template Method).
	 * 
	 * @param source
	 *            - источник XML данных.
	 */
	public void validate(final XMLSource source) {
		try {
			Validator validator = createValidator(source);
			validator.validate(createSource(source));
		} catch (SettingsFileOpenException e) {
			throw e;
		} catch (Exception e) {
			if (source.getSubjectName() != null) {
				throw new XSDValidateException(e, source.getSubjectName(), source.getSchemaName());
			} else {
				throw new XSDValidateException(e, source.getSchemaName());
			}
		}
	}

	private Source createSource(final XMLSource aSource) throws SAXException {
		return aSource.getExtractor().extract(aSource); // TODO некрасиво
	}

	private Validator createValidator(final XMLSource aSource) throws SAXException {
		SchemaFactory schemaFactory = XMLUtils.createSchemaFactory();
		File file = getFileForSchema(aSource.getSchemaName());
		// передавать InputStream и URL нельзя, т.к. в этом случае парсер не
		// находит вложенных схем!
		Schema schemaXSD = schemaFactory.newSchema(file);
		Validator validator = schemaXSD.newValidator();
		return validator;
	}

	private File getFileForSchema(final String aSchema) {
		return xsdSource.getSchema(aSchema);
	}

	public XMLValidator(final XSDSource aXsdSource) {
		super();
		xsdSource = aXsdSource;
	}

	public XSDSource getXsdSource() {
		return xsdSource;
	}

	public void setXsdSource(final XSDSource aXsdSource) {
		xsdSource = aXsdSource;
	}
}
