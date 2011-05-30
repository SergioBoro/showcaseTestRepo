/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.beta2.extra.gwt.ui.panels.DialogBoxWithCaptionButton;
import ru.curs.showcase.app.client.api.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * 
 * Класс окна для отображения элементов информационной панели различных типов.
 * 
 * @author anlug
 * 
 */
public class WindowWithDataPanelElement extends DialogBoxWithCaptionButton {

	/**
	 * ScrollPanel, которая содержит содержимое модального окна.
	 */
	private final ScrollPanel sp = new ScrollPanel();
	/**
	 * BasicElementPanel bep.
	 */
	private BasicElementPanel bep = null;

	/**
	 * Переменная определяющая показывать ли внизу модального окна кнопку
	 * Закрыть.
	 */
	private Boolean showCloseBottomButton;

	/**
	 * @param ashowCloseBottomButton
	 *            the showCloseBottomButton to set
	 */
	public void setShowCloseBottomButton(final Boolean ashowCloseBottomButton) {
		this.showCloseBottomButton = ashowCloseBottomButton;
	}

	/**
	 * @return the showCloseBottomButton
	 */
	public Boolean getShowCloseBottomButton() {
		return showCloseBottomButton;
	}

	public WindowWithDataPanelElement() {
		super();
		setShowCloseBottomButton(false);
		final int n100 = 100;
		sp.setSize(String.valueOf(Window.getClientWidth() - n100) + "px",
				String.valueOf(Window.getClientHeight() - n100) + "px");
	}

	public WindowWithDataPanelElement(final String caption, final Integer width,
			final Integer heigth, final Boolean ashowCloseBottomButton) {

		super(caption);

		sp.setSize(String.valueOf(width) + "px", String.valueOf(heigth) + "px");

		setShowCloseBottomButton(ashowCloseBottomButton);
	}

	// public WindowWithDataPanelElement(final boolean autoHide, final boolean
	// modal) {
	// super(autoHide, modal, "");
	//
	// }

	// public WindowWithDataPanelElement(final boolean autoHide) {
	// super(autoHide);
	// }

	/**
	 * Показывает модальное окно с переданной ему BasicElementPanel и заголовком
	 * окна.
	 * 
	 * @param bep1
	 *            - BasicElementPanel, который будет отображаться в окне.
	 */
	public void showModalWindow(final BasicElementPanel bep1) {

		bep = bep1;

		if (bep instanceof XFormPanel) {
			XFormPanel.beforeModalWindow(bep);
		}

		VerticalPanel dialogContents = new VerticalPanel();

		sp.add(dialogContents);
		final int n = 10;
		dialogContents.setSpacing(n);
		dialogContents.setSize("100%", "100%");
		setWidget(sp);
		setAnimationEnabled(true);
		setGlassEnabled(true);

		dialogContents.add(bep.getPanel());

		if (getShowCloseBottomButton()) {
			Button ok = new Button("Закрыть");
			ok.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					closeWindow();
				}
			});

			dialogContents.add(ok);
			dialogContents.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);

			ok.setFocus(true);

		}

		AppCurrContext.getInstance().setCurrentOpenWindowWithDataPanelElement(this);
		center();
		show();

		// альтернатива - xforms в iframe

		// NamedFrame iframe = new NamedFrame("modalXForm1");
		// DOM.setElementAttribute(iframe.getElement(), "id", "modalXForm1");
		// dialogContents.add(iframe);
		//
		// DOM.setElementAttribute(dialogContents.getElement(), "id",
		// "xformParent");
		// DOM.setElementAttribute(bep.getPanel().getElement(), "id", "xform1");
		// show(); // must do it!

		// IFrameElement frame =
		// IFrameElement.as(RootPanel.get("modalXForm1").getElement());
		// Document doc = frame.getContentDocument();
		// BodyElement be = doc.getBody();
		//
		// DivElement div = doc.createDivElement();
		// div.setId("target");
		// be.appendChild(div);
		//
		// be.appendChild(bep.getPanel().getElement());
		//
		// addScriptLink("xsltforms/xsltforms.js");
		// addCSSLink("xsltforms/xsltforms.css");
		// show();
		// плюс к вышеперечисленному во всех JSNI функциях XFormPanel нужно
		// заменить $wnd на $wnd.frames['modalXForm1'] и $doc на
		// $wnd.frames['modalXForm1'].document
		// и добавить 2 функции:
		//
		// public static native void addScriptLink(final String link) /*-{
		// var newscript =
		// $wnd.frames['modalXForm1'].document.createElement('script');
		// newscript.type = "text/javascript";
		// newscript.src = link;
		// var body =
		// $wnd.frames['modalXForm1'].document.getElementsByTagName('body')[0];
		// body.insertBefore(newscript, body.firstChild);
		// }-*/;
		//
		// public static native void addCSSLink(final String link) /*-{
		// var objCSS =
		// $wnd.frames['modalXForm1'].document.createElement('link');
		// objCSS.rel = 'stylesheet';
		// objCSS.href = link;
		// objCSS.type = 'text/css';
		// var hh1 =
		// $wnd.frames['modalXForm1'].document.getElementsByTagName('head')[0];
		// hh1.appendChild(objCSS);
		// }-*/;
	}

	/**
	 * Скрывает модальное окно.
	 */
	@Override
	public void closeWindow() {

		AppCurrContext.getInstance().setCurrentOpenWindowWithDataPanelElement(null);
		hide();
		if (bep != null) {
			if (bep instanceof XFormPanel) {
				XFormPanel.destroyXForms();
			}
		}
		bep = null;

		// ------------------ если на вкладке есть xForm то
		// прорисовываем ее

		ActionExecuter.drawXFormPanelsAfterModalWindowShown(AppCurrContext.getInstance()
				.getCurrentAction());

		// ------------
	}

}
