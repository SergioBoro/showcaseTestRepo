package ru.curs.showcase.model.xform;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.html.XForms;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

/**
 * Фабрика по созданию объектов XForms.
 * 
 * @author den
 * 
 */
public final class XFormsDBFactory extends HTMLBasedElementFactory {
	/**
	 * Результат работы фабрики.
	 */
	private XForms result;

	public XFormsDBFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	@Override
	public XForms build() throws Exception {
		return (XForms) super.build();
	}

	@Override
	protected void transformData() {
		DocumentBuilder db = XMLUtils.createBuilder();
		Document template = null;
		InputStream stream;
		String file =
			String.format("%s/%s", AppProps.XFORMS_DIR, getSource().getElementInfo()
					.getTemplateName());
		try {
			stream = AppProps.loadUserDataToStream(file);

		} catch (IOException e) {
			throw new SettingsFileOpenException(getSource().getElementInfo().getTemplateName(),
					SettingsFileType.XFORM);
		}

		try {
			template = db.parse(stream);
		} catch (Exception e1) {
			throw new XMLFormatException(file, e1);
		}

		String html;
		try {
			template =
				XFormsTemplateModificator.addSrvInfo(template, getSource().getCallContext(),
						getSource().getElementInfo());
			html =
				XFormProducer.getHTML(template, (getSource()).getData(), getSource()
						.getElementInfo().getId());
			result.setXFormParts(XFormCutter.xFormParts(html));
		} catch (Exception e) {
			throw new XSLTTransformException(e);
		}
	}

	@Override
	public XForms getResult() {
		return result;
	}

	@Override
	protected void initResult() {
		result = new XForms();
	}
}
