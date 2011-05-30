package ru.curs.showcase.model;

import java.io.*;
import java.util.*;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Внутренняя фабрика для создания событий из XML документа. При описании
 * событий может использоваться характеристика "простой" (simple). Она означает
 * событие, для которого определен только Id1.
 * 
 * @author den
 * 
 * @param <E>
 *            - класс событий.
 */
public class EventFactory<E extends Event> extends GeneralXMLHelper {
	/**
	 * Сообщение об ошибке разбора XML.
	 */
	private static final String PROP_COL_ERROR_MES = "SQL XML с описанием событий";

	/**
	 * Текущее событие.
	 */
	private Event current;
	/**
	 * Результат.
	 */
	private final Collection<E> result = new ArrayList<E>();
	/**
	 * Фабрика событий.
	 */
	private final ActionFactory actionFactory = new ActionFactory();

	/**
	 * Требуемый класс событий.
	 */
	private Class<? extends Event> eventClass;
	/**
	 * Атрибут, содержащий Id2.
	 */
	private String id2Tag;
	/**
	 * Префикс к событию, для которого определен Id2.
	 */
	private String id2Prefix;
	/**
	 * Атрибут, содержащий Id1.
	 */
	private String id1Tag;

	/**
	 * Обработчик для SAX парсера.
	 */
	private DefaultHandler saxHandler;

	/**
	 * Общий идентификатор id1.
	 */
	private String generalId;

	/**
	 * Действие по умолчанию. Может быть задано вместе с набором событий.
	 */
	private Action defaultAction;

	/**
	 * Имя схемы для проверки XML с событиями.
	 */
	private String schemaName;

	/**
	 * Массив дополнительных обработчиков.
	 */
	private final List<SAXTagHandler> handlers = new ArrayList<SAXTagHandler>();

	public EventFactory(final Class<? extends Event> aEventClass) {
		init(aEventClass);
	}

	/**
	 * Инициализировать для получения "простых" событий одной функцией.
	 * 
	 * @param aId1Tag
	 *            - тэг для Id1.
	 * @param aSchemaSource
	 *            - имя схемы.
	 */
	public void intiForGetSimpleEvents(final String aId1Tag, final String aSchemaSource) {
		id1Tag = aId1Tag;
		id2Tag = "faketag";
		id2Prefix = "fakeprefix";
		schemaName = aSchemaSource;
	}

	/**
	 * Инициализировать для получения событий для конкретного Id1.
	 * 
	 * @param aId2Tag
	 *            - атрибут с Id2.
	 * @param aId2Prefix
	 *            - префикс для атрибута, обозначающего, что Id2 задан.
	 * @param aSchemaName
	 *            - имя схемы.
	 */
	public void initForGetSubSetOfEvents(final String aId2Tag, final String aId2Prefix,
			final String aSchemaName) {
		id1Tag = null;
		id2Tag = aId2Tag;
		id2Prefix = aId2Prefix;
		schemaName = aSchemaName;
	}

	private void init(final Class<? extends Event> aEventClass) {
		eventClass = aEventClass;
		saxHandler = new DefaultHandler() {

			@SuppressWarnings("unchecked")
			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (qname.equalsIgnoreCase(EVENT_TAG)) {
					Event event;
					String value;
					try {
						event = eventClass.newInstance();
					} catch (Exception e) {
						throw new CreateObjectError(e);
					}
					if (id1Tag == null) {
						event.setId1(generalId);
					} else {
						value = attrs.getValue(id1Tag);
						event.setId1(value);
					}
					value = attrs.getValue(NAME_TAG);

					Iterator<InteractionType> iterator =
						Arrays.asList(InteractionType.values()).iterator();
					while (iterator.hasNext()) {
						InteractionType type = iterator.next();
						if (value.endsWith(type.toString().toLowerCase())) {
							event.setInteractionType(type);
							break;
						}
					}
					if (value.startsWith(id2Prefix)) {
						String id2 = attrs.getValue(id2Tag);
						event.setId2(id2);
					}
					current = event;
					result.add((E) event);
					return;
				}

				if (actionFactory.canHandle(qname, SaxEventType.STARTELEMENT)) {
					Action action =
						actionFactory.handleStartTag(namespaceURI, lname, qname, attrs);
					if (current != null) {
						current.setAction(action);
					} else {
						defaultAction = action;
					}
					return;
				}
				Iterator<SAXTagHandler> iterator = handlers.iterator();
				while (iterator.hasNext()) {
					SAXTagHandler handler = iterator.next();
					if (handler.canHandle(qname, SaxEventType.STARTELEMENT)) {
						handler.handleStartTag(namespaceURI, lname, qname, attrs);
						return;
					}
				}
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (qname.equalsIgnoreCase(EVENT_TAG)) {
					current = null;
				}
			}
		};
	}

	/**
	 * Основная функция фабрики - получение коллекции событий из потока.
	 * 
	 * @param aGeneralId
	 *            - общий идентификатор 1 для возвращаемых событий.
	 * @param data
	 *            - строка с XML данными.
	 * @return - коллекция событий.
	 */
	public Collection<E> getSubSetOfEvents(final String aGeneralId, final String data) {
		generalId = aGeneralId;

		InputStream xml;
		try {
			xml = new ByteArrayInputStream(data.getBytes(TextUtils.DEF_ENCODING));
		} catch (Exception e) {
			throw new ResultSetHandleException(e);
		}

		xml = XMLUtils.validateXMLStream(xml, schemaName);

		stdParseProc(xml);
		return result;
	}

	private void stdParseProc(final InputStream stream) {
		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(stream, saxHandler);
		} catch (Throwable e) {
			XMLUtils.stdSAXErrorHandler(e, PROP_COL_ERROR_MES);
		}
	}

	/**
	 * Основная функция фабрики - получение коллекции "простых" событий из
	 * потока.
	 * 
	 * @param stream
	 *            - поток с XML данными.
	 * @return - коллекция событий.
	 */
	public Collection<E> getSimpleEvents(final InputStream stream) {
		InputStream data = stream;
		if (schemaName != null) {
			data = XMLUtils.validateXMLStream(data, schemaName);
		}
		stdParseProc(data);
		return result;
	}

	public Action getDefaultAction() {
		return defaultAction;
	}

	/**
	 * Инициализирует фабрику для считывания событий с конкретным Id1 и без Id2.
	 * 
	 * @param aSchemaName
	 *            - имя схемы.
	 */
	public void initForGetSimpleSubSetOfEvents(final String aSchemaName) {
		id1Tag = null;
		id2Tag = "faketag";
		id2Prefix = "fakeprefix";
		schemaName = aSchemaName;
	}

	/**
	 * Добавляет обработчик в фабрику.
	 * 
	 * @param aColorHandler
	 *            - обработчик.
	 */
	public void addHandler(final SAXTagHandler aColorHandler) {
		handlers.add(aColorHandler);
	}
}