package ru.curs.showcase.model;

import javax.xml.parsers.SAXParser;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.element.DataPanelCompBasedElement;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.exception.SAXError;
import ru.curs.showcase.util.XMLUtils;

/**
 * Абстрактная фабрика для элементов инф. панели, основанных на компонентах -
 * GRID, CHART, GEOMAP.
 * 
 * @author den
 * 
 */
public abstract class CompBasedElementFactory extends TemplateMethodFactory {

	@Override
	public abstract DataPanelCompBasedElement getResult();

	public CompBasedElementFactory(final ElementRawData aSource) {
		super(aSource);
	}

	/**
	 * Конкретный обработчик для считывания динамических настроек в процедуре
	 * setupDynamicSettings.
	 * 
	 * @return - обработчик.
	 */
	protected abstract DefaultHandler getConcreteHandler();

	@Override
	protected void setupDynamicSettings() {
		final DefaultHandler addHandler = getConcreteHandler();
		DefaultHandler myHandler = new DefaultHandler() {

			private boolean readingHeader = false;
			private boolean readingFooter = false;
			private final ActionFactory actionFactory = new ActionFactory();

			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (qname.equalsIgnoreCase(HEADER_TAG)) {
					readingHeader = true;
					getResult().setHeader("");
					return;
				}
				if (qname.equalsIgnoreCase(FOOTER_TAG)) {
					readingFooter = true;
					getResult().setFooter("");
					return;
				}
				if (addHandler != null) {
					try {
						addHandler.startElement(namespaceURI, lname, qname, attrs);
					} catch (SAXException e) {
						throw new SAXError(e);
					}
				}
				if (actionFactory.canHandle(qname, SaxEventType.STARTELEMENT)) {
					Action action =
						actionFactory.handleStartTag(namespaceURI, lname, qname, attrs);
					getResult().setDefaultAction(action);
					return;
				}
				if (readingHeader) {
					getResult().setHeader(
							getResult().getHeader()
									+ XMLUtils.saxTagWithAttrsToString(qname, attrs));
					return;
				}
				if (readingFooter) {
					getResult().setFooter(
							getResult().getFooter()
									+ XMLUtils.saxTagWithAttrsToString(qname, attrs));
					return;
				}
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (qname.equalsIgnoreCase(HEADER_TAG)) {
					readingHeader = false;
					getResult().setHeader(getResult().getHeader().trim());
					return;
				}
				if (qname.equalsIgnoreCase(FOOTER_TAG)) {
					readingFooter = false;
					getResult().setFooter(getResult().getFooter().trim());
					return;
				}
				if (addHandler != null) {
					try {
						addHandler.endElement(namespaceURI, lname, qname);
					} catch (SAXException e) {
						throw new SAXError(e);
					}
				}
				if (readingHeader) {
					getResult().setHeader(getResult().getHeader() + "</" + qname + ">");
					return;
				}
				if (readingFooter) {
					getResult().setFooter(getResult().getFooter() + "</" + qname + ">");
					return;
				}
			}

			@Override
			public void characters(final char[] arg0, final int arg1, final int arg2) {
				if (readingHeader) {
					getResult().setHeader(
							getResult().getHeader() + String.copyValueOf(arg0, arg1, arg2));
					return;
				}
				if (readingFooter) {
					getResult().setFooter(
							getResult().getFooter() + String.copyValueOf(arg0, arg1, arg2));
					return;
				}
				if (addHandler != null) {
					try {
						addHandler.characters(arg0, arg1, arg2);
					} catch (SAXException e) {
						throw new SAXError(e);
					}
				}
			}
		};

		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(getSettings(), myHandler);
		} catch (Throwable e) {
			XMLUtils.stdSAXErrorHandler(e, getSettingsErrorMes());
		}
	}

	/**
	 * Возвращает специфичную для типа элемента часть ошибки при разборе
	 * настроек элемента.
	 * 
	 * @return - строка с ошибкой.
	 */
	protected abstract String getSettingsErrorMes();

}
