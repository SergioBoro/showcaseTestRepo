package ru.curs.showcase.model.xform;

import java.io.StringWriter;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import ru.curs.showcase.model.GeneralXMLHelper;

/**
 * Класс, преобразующий документ в HTML-код XForm.
 */
public final class XFormProducer extends GeneralXMLHelper {
	static final String XF_INSTANCE = "xf:instance";
	static final String XSLTFORMS_XSL = "xsltforms.xsl";
	/**
	 * String XFORMS_URI.
	 */
	private static final String XFORMS_URI = "http://www.w3.org/2002/xforms";
	/**
	 * int DEFAULT_BUFFER_SIZE.
	 */
	private static final int DEFAULT_BUFFER_SIZE = 1024;
	/**
	 * String MAIN_INSTANCE.
	 */
	private static final String MAIN_INSTANCE = "mainInstance";
	/**
	 * String INSTANCE.
	 */
	private static final String INSTANCE = "instance";

	private XFormProducer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Возвращает org.w3c.dom.Document с пустыми данными, полученными на основе
	 * шаблона.
	 * 
	 * @param template
	 *            Шаблон
	 * 
	 * @return org.w3c.dom.Document
	 * 
	 * @throws Exception
	 *             в случае ошибок.
	 */
	public static org.w3c.dom.Document getEmptyData(final org.w3c.dom.Document template)
			throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document doc = db.newDocument();

		Node n = getMainInstance(template).getFirstChild();

		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				break;
			}
			n = n.getNextSibling();
		}

		n = doc.importNode(n, true);
		doc.appendChild(n);

		return doc;
	}

	/**
	 * Возвращает ноду xf:instance c id=mainInstance. Такая нода должна
	 * существовать в шаблоне для того, чтобы он работал с Showcase.
	 * 
	 * @param doc
	 *            - документ шаблона.
	 * @return - элемент mainInstance.
	 */
	public static Node getMainInstance(final org.w3c.dom.Document doc) {
		NodeList l = doc.getElementsByTagName(XF_INSTANCE);
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i).getAttributes().getNamedItem(ID_TAG);
			if ((n != null) && (MAIN_INSTANCE.equals(n.getTextContent()))) {
				return l.item(i);
			}
		}
		return null;
	}

	/**
	 * Возвращает строку, являющуюся HTML-фрагментом для отображения документа в
	 * виде XForm.
	 * 
	 * @param xml
	 *            документ
	 * 
	 * @param tempData
	 *            временные данные документа (если эта переменная не равна null,
	 *            эти данные подставляются в MainInstance). Необходимо для
	 *            просмотра проимпортированного содержимого формы.
	 * 
	 * @param xformId
	 *            Id элемента информационной панели
	 * 
	 * @return HTML-фрагмент, пригодный для отображения в браузере
	 * 
	 * @throws Exception
	 *             в случае ошибки ввода-вывода, ошибки XSLT-преобразования и
	 *             проч.
	 */
	public static String getHTML(final org.w3c.dom.Document xml,
			final org.w3c.dom.Document tempData, final String xformId) throws Exception {
		/* В случае, когда нам подложили временные данные, мы их подменяем */
		if (tempData != null) {
			NodeList nl = xml.getElementsByTagNameNS(XFORMS_URI, INSTANCE);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (MAIN_INSTANCE.equals(n.getAttributes().getNamedItem(ID_TAG).getTextContent())) {
					n.setTextContent("");
					Node nn = xml.importNode(tempData.getDocumentElement(), true);
					n.appendChild(nn);
					break;
				}
			}
		}

		// Выполняем трансформацию xsltforms
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer tr =
			tf.newTransformer(new StreamSource(XFormProducer.class
					.getResourceAsStream(XSLTFORMS_XSL)));
		tr.setParameter("baseuri", "xsltforms/");
		StringWriter sw = new StringWriter(DEFAULT_BUFFER_SIZE);

		tr.transform(new DOMSource(xml), new StreamResult(sw));

		String ret = sw.toString();
		ret = ret.replaceAll("xformId", xformId);

		return ret;
	}
}
