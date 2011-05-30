/**
 * 
 */
package ru.curs.showcase.model.webtext;

import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Фабрика для создания объектов WebText.
 * 
 * @author den
 * 
 */
public final class WebTextDBFactory extends HTMLBasedElementFactory {
	/**
	 * Результат работы фабрики.
	 */
	private WebText result;

	public WebTextDBFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	@Override
	public WebText build() throws Exception {
		return (WebText) super.build();
	}

	@Override
	protected void transformData() {
		String out =
			XMLUtils.xsltTransform(getSource().getData(), getSource().getElementInfo()
					.getTransformName());
		result.setData(out);
	}

	@Override
	public DataPanelElement getResult() {
		return result;
	}

	@Override
	protected void initResult() {
		result = new WebText();
	}
}
