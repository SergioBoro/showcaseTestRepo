package ru.curs.showcase.app.client.api;

import ru.curs.showcase.app.api.event.Event;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.app.client.*;

/**
 * 
 */

/**
 * @author anlug Класс реализующий функции обратного вызова из html текста
 *         (WebText).
 * 
 */
public final class WebTextPanelCallbacksEvents {

	private WebTextPanelCallbacksEvents() {

	}

	/**
	 * 
	 * Событие одинарного клика на графике (на Chart).
	 * 
	 * @param webTextId
	 *            - Id элемента WebText.
	 * @param linkId
	 *            - идентификатор ссылки на которой был совершен клик
	 */
	public static void webTextPanelClick(final String webTextId, final String linkId) {

		WebText wt = ((WebTextPanel) ActionExecuter.getElementPanelById(webTextId)).getWebText();

		Event chev = wt.getEventManager().getEventForLink(linkId);
		if (chev != null) {
			AppCurrContext.getInstance().setCurrentAction(chev.getAction());
			ActionExecuter.execAction();

		}

	}
}
