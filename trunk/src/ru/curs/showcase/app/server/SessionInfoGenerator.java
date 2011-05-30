package ru.curs.showcase.app.server;

import java.util.*;

import org.slf4j.*;
import org.w3c.dom.*;

import ru.curs.showcase.model.GeneralXMLHelper;
import ru.curs.showcase.util.XMLUtils;

/**
 * Класс, содержащий функции для получения информации о текущей сессией
 * приложения и создании XML документа, ее содержащего.
 * 
 * @author den
 * 
 */
public final class SessionInfoGenerator extends GeneralXMLHelper {
	/**
	 * LOGGER.
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(SessionInfoGenerator.class);

	static final String SESSION_CONTEXT_TAG = "sessioncontext";
	public static final String USERNAME_TAG = "username";
	public static final String URL_PARAMS_TAG = "urlparams";
	public static final String URL_PARAM_TAG = "urlparam";

	private SessionInfoGenerator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Формирует контекст сессии в виде XML объекта.
	 * 
	 * 
	 * @return - строку с XML.
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @param params
	 *            - параметры.
	 */
	static String
			generateSessionContext(final String sessionId, final Map<String, String[]> params) {
		LOGGER.debug("generateSessionContext sessionId = " + sessionId);
		Document info =
			XMLUtils.createBuilder().getDOMImplementation()
					.createDocument("", SESSION_CONTEXT_TAG, null);

		Element node = info.createElement(USERNAME_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(ServletUtils.getUserNameFromSession()));

		fillURLParams(info, sessionId, params);

		String result = XMLUtils.xsltTransform(info, null);
		return result;
	}

	private static void fillURLParams(final Document info, final String sessionId,
			final Map<String, String[]> params) {
		Element node;

		if ((params != null) && (!params.isEmpty())) {
			node = info.createElement(URL_PARAMS_TAG);
			info.getDocumentElement().appendChild(node);
			Iterator<String> iterator = params.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				Element child = info.createElement(URL_PARAM_TAG);
				node.appendChild(child);
				child.setAttribute(NAME_TAG, key);
				String value = "";
				if (params.get(key) != null) {
					value = Arrays.toString(params.get(key));
				}
				child.setAttribute(VALUE_TAG, value);
			}
		}
	}
}
