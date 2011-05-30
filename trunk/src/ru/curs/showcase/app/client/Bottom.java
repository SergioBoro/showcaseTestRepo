/**
 * 
 */
package ru.curs.showcase.app.client;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Класс генерации пользовательского интерфейса нижней части приложения
 *         Showcase.
 * 
 */
public class Bottom {

	/**
	 * Генерация заголовка (шапки) приложения Showcase.
	 * 
	 * @return возвращает виджет заголовка (шапки)
	 */
	public Widget generateBottom() {

		final VerticalPanel bottomVerticalPanel = new VerticalPanel();
		bottomVerticalPanel.setSize("100%", "100%");
		bottomVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		HorizontalPanel bottomHorizontalPanel = new HorizontalPanel();
		bottomVerticalPanel.add(bottomHorizontalPanel);
		// final int n = 10;
		bottomHorizontalPanel.setSpacing(0);
		// bottomHorizontalPanel.setSize("100%", "100%");

		// VerticalPanel vp1 = new VerticalPanel();
		// bottomHorizontalPanel.add(vp1);
		// vp1.add(new Label("111020, г. Москва, Боровая ул., д. 7, стр. 1"));
		// vp1.add(new Label("тел.: +7-495-7805090"));
		// vp1.add(new Label("info@curs.ru"));

		bottomHorizontalPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		// VerticalPanel vp2 = new VerticalPanel();
		// bottomHorizontalPanel.add(vp2);
		// vp2.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		bottomHorizontalPanel.add(new Label("© Copyright,"));
		HTML html = new HTML();
		html.setHTML("&nbsp;");
		bottomHorizontalPanel.add(html);
		Anchor exitLink = new Anchor("<b>ООО 'КУРС-ИТ'</b>", true, "http://www.curs.ru", "_blank");
		exitLink.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);

		bottomHorizontalPanel.add(exitLink);
		bottomHorizontalPanel.add(new Label(", 2010-2011"));

		// vp2.add(new Label("www.curs.ru"));
		final int n = 17;
		bottomVerticalPanel.setWidth(Window.getClientWidth() - n + "px");

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				int width = event.getWidth() - n;
				bottomVerticalPanel.setWidth(width + "px");
			}
		});

		return bottomVerticalPanel;

	}
}
