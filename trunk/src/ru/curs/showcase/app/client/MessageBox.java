/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.client.api.Constants;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Стандартное сообщение приложения Showcase
 * 
 */
public final class MessageBox {

	private MessageBox() {
		super();
	}

	/**
	 * 
	 * Процедура вывода стандартного сообщения с одной кнопкой ОК в стиле gwt.
	 * 
	 * @param caption
	 *            - заглавие окна сообщения
	 * @param message
	 *            - текст сообщения
	 * @return возвращает DialogBox
	 * 
	 */
	public static DialogBox showSimpleMessage(final String caption, final String message) {
		final DialogBox dlg = new DialogBox();
		VerticalPanel dialogContents = new VerticalPanel();
		final int n = 10;
		dialogContents.setSpacing(n);
		dlg.setWidget(dialogContents);
		dlg.setAnimationEnabled(true);
		dlg.setGlassEnabled(true);
		dlg.setText(caption);
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				dlg.hide();
			}
		});
		Label l = new Label(message);
		dialogContents.add(l);
		dialogContents.add(ok);
		dialogContents.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);
		dlg.center();
		ok.setFocus(true);
		return dlg;
	}

	/**
	 * 
	 * Процедура вывода сообщения с одной кнопкой ОК в стиле gwt c текстом для
	 * скрытия.
	 * 
	 * @param caption
	 *            - заглавие окна сообщения
	 * @param message
	 *            - текст сообщения
	 * @param hideMessage
	 *            - текст сообщения, который скрывается
	 * @return возвращает DialogBox
	 * 
	 */
	public static DialogBox showMessageWithDetails(final String caption, final String message,
			final String hideMessage) {
		final DialogBox dlg = new DialogBox();
		VerticalPanel dialogContents = new VerticalPanel();
		final int n = 10;
		dialogContents.setSpacing(n);
		dlg.setWidget(dialogContents);
		dlg.setAnimationEnabled(true);
		dlg.setGlassEnabled(true);
		dlg.setText(caption);
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				dlg.hide();
			}
		});
		Label l = new Label(message);
		dialogContents.add(l);

		final DisclosurePanel dp = new DisclosurePanel();

		HorizontalPanel hp = new HorizontalPanel();

		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Image im = new Image();
		im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_CLOSE_IMAGE);

		hp.add(im);
		hp.add(new HTML("&nbsp;" + "Показать подробную информацию" + "&nbsp;" + "&nbsp;"));
		dp.setHeader(hp);

		dialogContents.add(dp);
		final TextArea textArea = new TextArea();
		dp.addOpenHandler(new OpenHandler<DisclosurePanel>() {

			@Override
			public void onOpen(final OpenEvent<DisclosurePanel> arg0) {

				HorizontalPanel hp = new HorizontalPanel();

				hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				Image im = new Image();
				im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_OPEN_IMAGE);
				textArea.setSize("600px", "250px");
				hp.add(im);
				hp.add(new HTML("&nbsp;" + "Скрыть подробную информацию" + "&nbsp;" + "&nbsp;"));
				dp.setHeader(hp);

			}

		});

		dp.addCloseHandler(new CloseHandler<DisclosurePanel>() {

			@Override
			public void onClose(final CloseEvent<DisclosurePanel> arg0) {
				HorizontalPanel hp = new HorizontalPanel();

				hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				Image im = new Image();
				im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_CLOSE_IMAGE);

				hp.add(im);
				hp.add(new HTML("&nbsp;" + "Показать подробную информацию"));
				dp.setHeader(hp);

			}

		});

		dp.setSize("100%", "100%");
		textArea.setSize("90%", "100%");
		final int n1 = 5;
		textArea.setVisibleLines(n1);
		textArea.setText(hideMessage);

		textArea.setReadOnly(true);
		dp.setContent(textArea);

		dialogContents.add(ok);
		dialogContents.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);
		dlg.center();
		ok.setFocus(true);

		return dlg;

	}
}
