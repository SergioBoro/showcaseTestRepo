package ru.curs.showcase.exception;

import java.io.*;
import java.sql.SQLException;

import javax.xml.parsers.SAXParser;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

/**
 * Исключение для решения. Исключение можно вызвать, если в тексте сообщения об
 * ошибке из БД будут определенные комбинации символов. Описание выдаваемых
 * пользователю сообщений при таких исключений хранятся в файле в папке
 * userdata. Формат этих файлов следующий: <messages> <messages id="id1"
 * type="ERROR"> Это текст сообщения об ошибке в хранимой процедуре.
 * 
 * @author den
 * 
 */
public class SolutionDBException extends AbstractShowcaseException {

	/**
	 * Префикс для исключения решения.
	 */
	public static final String SOL_MES_PREFIX = "__sol_mes_";
	/**
	 * Суффикс для исключения решения.
	 */
	public static final String SOL_MES_SUFFIX = "_src__";

	/**
	 * Имя файла с описанием сообщений.
	 */
	public static final String SOL_MESSAGES_FILE = "solution.messages.xml";
	/**
	 * Сообщение, выдаваемое пользователю.
	 */
	private SolutionMessage solutionMessage;

	/**
	 * Временная переменная для хранения идентификатора сообщения.
	 */
	private String mesId;
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 870894006633410366L;
	static final String MESSAGE_TAG = "message";

	/**
	 * Признак того, что нужное сообщение найдено в файле.
	 */
	private boolean mesFound = false;

	public SolutionDBException(final Throwable cause) {
		super(cause);
		parse(cause);
		loadMessage();
	}

	private void loadMessage() {
		InputStream stream;
		try {
			stream = AppProps.loadUserDataToStream(SOL_MESSAGES_FILE);
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, SOL_MESSAGES_FILE,
					SettingsFileType.SOLUTION_MESSAGES);
		}

		DefaultHandler saxHandler = new DefaultHandler() {

			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (qname.equalsIgnoreCase(MESSAGE_TAG)) {
					if (attrs.getValue(GeneralXMLHelper.ID_TAG).equals(mesId)) {
						mesFound = true;
						solutionMessage = new SolutionMessage();
						solutionMessage.setId(mesId);
						solutionMessage.setText("");
						if (attrs.getIndex(GeneralXMLHelper.TYPE_TAG) > -1) {
							solutionMessage.setType(MessageType.valueOf(attrs
									.getValue(GeneralXMLHelper.TYPE_TAG)));
						}
					}
				}
			}

			@Override
			public void characters(final char[] aCh, final int aStart, final int aLength)
					throws SAXException {
				if (mesFound) {
					solutionMessage.setText(solutionMessage.getText()
							+ String.copyValueOf(aCh, aStart, aLength));
				}
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (qname.equalsIgnoreCase(MESSAGE_TAG)) {
					if (mesFound) {
						mesFound = false;
						solutionMessage.setText(solutionMessage.getText().trim());
					}
				}
			}
		};

		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(stream, saxHandler);
		} catch (Throwable e) {
			XMLUtils.stdSAXErrorHandler(e, SOL_MESSAGES_FILE);
		}

		if (solutionMessage == null) {
			throw new SettingsFileRequiredPropException(SOL_MESSAGES_FILE, mesId,
					SettingsFileType.SOLUTION_MESSAGES);
		}

	}

	private void parse(final Throwable cause) {
		String mes = cause.getMessage();
		mesId =
			(String) mes.subSequence(mes.indexOf(SOL_MES_PREFIX) + SOL_MES_PREFIX.length(),
					mes.indexOf(SOL_MES_SUFFIX));
	}

	/**
	 * Функция проверки на то, что полученное из БД исключение является
	 * "предопределенным" создателями решения.
	 * 
	 * @param exc
	 *            - исключение.
	 * @return - результат проверки.
	 */
	public static boolean isSolutionDBException(final SQLException exc) {
		return exc.getMessage().contains(SOL_MES_PREFIX)
				&& exc.getMessage().contains(SOL_MES_SUFFIX);
	}

	public SolutionMessage getSolutionMessage() {
		return solutionMessage;
	}

	public void setSolutionMessage(final SolutionMessage aSolutionMessage) {
		solutionMessage = aSolutionMessage;
	}

}
