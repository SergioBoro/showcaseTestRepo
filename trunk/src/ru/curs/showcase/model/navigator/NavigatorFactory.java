package ru.curs.showcase.model.navigator;

import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.navigator.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

/**
 * Фабрика для создания навигатора.
 * 
 * @author den
 * 
 */
public final class NavigatorFactory extends GeneralXMLHelper {
	static final String SELECT_ON_LOAD_TAG = "selectOnLoad";
	protected static final String XML_ERROR_MES = "описание навигатора";
	protected static final String GRP_ICONS_DIR_PARAM_NAME = "navigator.icons.dir.name";
	protected static final String GRP_DEF_ICON_PARAM_NAME = "navigator.def.icon.name";
	/**
	 * Путь к каталогу для иконок.
	 */
	private final String groupIconsDir;
	/**
	 * Имя иконки по умолчанию.
	 */
	private final String groupDefIcon;
	/**
	 * SAX обработчик.
	 */
	private final DefaultHandler myHandler;

	/**
	 * Конструируемый навигатор.
	 */
	private Navigator result;
	/**
	 * Текущая группа навигатора.
	 */
	private NavigatorGroup currentGroup;
	/**
	 * Фабрика событий.
	 */
	private final ActionFactory actionFactory = new ActionFactory();
	/**
	 * Стек текущих элементов навигатора.
	 */
	private final LinkedList<NavigatorElement> currentElStack = new LinkedList<NavigatorElement>();

	public NavigatorFactory() {
		groupIconsDir = AppProps.getRequiredValueByName(GRP_ICONS_DIR_PARAM_NAME);
		groupDefIcon = AppProps.getRequiredValueByName(GRP_DEF_ICON_PARAM_NAME);

		myHandler = new DefaultHandler() {
			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (qname.equalsIgnoreCase(NAVIGATOR_TAG) && (result == null)) {
					result = new Navigator();
					if (attrs.getIndex(HIDE_ON_LOAD_TAG) > -1) {
						result.setHideOnLoad(Boolean.parseBoolean(attrs.getValue(HIDE_ON_LOAD_TAG)));
					}
					if (attrs.getIndex(WIDTH_TAG) > -1) {
						result.setWidth(attrs.getValue(WIDTH_TAG));
					}
					return;
				}
				if (qname.equalsIgnoreCase(GROUP_TAG)) {
					currentGroup = new NavigatorGroup();
					setupBaseProps(currentGroup, attrs);
					if (attrs.getIndex(IMAGE_ATTR_NAME) > -1) {
						setupImageId(attrs.getValue(IMAGE_ATTR_NAME));
					} else {
						setupImageId(groupDefIcon);
					}
					result.getGroups().add(currentGroup);
					return;
				}
				if (qname.startsWith(EL_ON_LEVEL_NODE_NAME)) {
					NavigatorElement el = new NavigatorElement();
					setupBaseProps(el, attrs);
					if (attrs.getIndex(SELECT_ON_LOAD_TAG) > -1) {
						if (attrs.getValue(SELECT_ON_LOAD_TAG).equalsIgnoreCase("true")) {
							result.setAutoSelectElement(el);
						}
					}
					if (currentElStack.size() == 0) {
						currentGroup.getElements().add(el);
					} else {
						currentElStack.getLast().getElements().add(el);
					}
					currentElStack.add(el);
					return;
				}

				if (actionFactory.canHandle(qname, SaxEventType.STARTELEMENT)) {
					Action action =
						actionFactory.handleStartTag(namespaceURI, lname, qname, attrs);
					NavigatorElement cur = currentElStack.getLast();
					cur.setAction(action);
					return;
				}
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (qname.startsWith(EL_ON_LEVEL_NODE_NAME)) {
					currentElStack.removeLast();
				}
			}

			private void setupImageId(final String imageFile) {
				currentGroup.setImageId(String.format("%s/%s", groupIconsDir, imageFile));
			}
		};
	}

	/**
	 * Функция построения навигатора из потока, содержащего XML данные.
	 * 
	 * @param stream
	 *            - поток.
	 * @return - навигатор.
	 */
	public Navigator fromStream(final InputStream stream) {
		InputStream streamForParse = XMLUtils.validateXMLStream(stream, "navigator.xsd");

		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(streamForParse, myHandler);
		} catch (Throwable e) {
			XMLUtils.stdSAXErrorHandler(e, XML_ERROR_MES);
		}
		return result;
	}
}
