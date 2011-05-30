package ru.curs.showcase.model.xform;

import org.w3c.dom.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.GeneralXMLHelper;
import ru.curs.showcase.util.XMLUtils;

/**
 * Класс, модифицирующий шаблон XForms, добавляя в него служебную информацию,
 * необходимую для работы Showcase.
 * 
 * @author den
 * 
 */
public final class XFormsTemplateModificator extends GeneralXMLHelper {
	static final String ROOT_SRV_DATA_TAG = "srvdata";

	private XFormsTemplateModificator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Добавляет в шаблон информацию о текущем контексте и элементе.
	 * 
	 * @param doc
	 *            - документ шаблона.
	 * @return - документ шаблона.
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента инф. панели.
	 */
	public static org.w3c.dom.Document addSrvInfo(final org.w3c.dom.Document doc,
			final CompositeContext context, final DataPanelElementInfo element) {
		Node node = XFormProducer.getMainInstance(doc);
		Node parent = node.getParentNode();

		Element el = doc.createElement(XFormProducer.XF_INSTANCE);
		el.setAttribute(ID_TAG, ROOT_SRV_DATA_TAG);
		Node srv = parent.appendChild(el);
		el = doc.createElement(SCHEMA_TAG);
		srv = srv.appendChild(el);

		addServerElement(doc, srv, context);

		addServerElement(doc, srv, element);

		return doc;
	}

	private static void addServerElement(final org.w3c.dom.Document doc, final Node srv,
			final Object xsrElement) {
		Element inserted = XMLUtils.objectToXML(xsrElement).getDocumentElement();
		Node nn = doc.importNode(inserted, true);
		srv.appendChild(nn);
	}

}
