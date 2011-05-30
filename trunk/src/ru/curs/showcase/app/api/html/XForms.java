package ru.curs.showcase.app.api.html;

import java.util.List;

import ru.curs.showcase.app.api.element.*;

/**
 * Класс, содержащий данные для отрисовки элемента WebText и необходимые
 * обработчики событий.
 * 
 * @author den
 * 
 */
public class XForms extends DataPanelElement {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2992185048516571628L;

	public XForms() {
		super();
	}

	/**
	 * List<String> xFormParts.
	 */
	private List<String> xFormParts;

	public void setXFormParts(final List<String> xFormParts1) {
		this.xFormParts = xFormParts1;
	}

	public final List<String> getXFormParts() {
		return xFormParts;
	}

	@Override
	protected EventManager initEventManager() {
		return new HTMLEventManager();
	}

	@Override
	public HTMLEventManager getEventManager() {
		return (HTMLEventManager) super.getEventManager();
	}

}
