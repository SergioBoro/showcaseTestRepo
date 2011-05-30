/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.Date;

import ru.beta2.extra.gwt.ui.panels.DialogBoxWithCaptionButton;
import ru.curs.showcase.app.api.ServerCurrentState;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.Constants;
import ru.curs.showcase.app.client.utils.AccessToDomModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Класс генерации пользовательского интерфейса верхней части (шапка)
 *         приложения Showcase.
 * 
 */
public class Header {

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * HTML виджет для отображения текущего имени пользователя в шапке
	 * приложения.
	 */
	private final HTML htmlForUserNameIndication = new HTML();

	/**
	 * Таймаут при выходе из системы.
	 */
	private static final int LOGOUT_TIMEOUT = 5000;

	/**
	 * Генерация заголовка (шапки) приложения Showcase.
	 * 
	 * @return возвращает виджет заголовка (шапки)
	 */
	public Widget generateHeader() {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}
		dataService.getServerCurrentState(new GWTServiceCallback<ServerCurrentState>(
				Constants.ERROR_OF_SERVER_CURRENT_STATE_RETRIEVING_FROM_SERVER) {

			@Override
			public void onSuccess(final ServerCurrentState serverCurrentState) {

				if (serverCurrentState != null) {

					AppCurrContext.getInstance().setServerCurrentState(serverCurrentState);

					fillServerCurrentStateInfoToTheAppropriatePanels();

				}
			}
		});
		final VerticalPanel headerVerticalPanel = new VerticalPanel();
		headerVerticalPanel.setStyleName("showcaseHeaderContainerStyle");
		headerVerticalPanel.setSize("100%", "100%");
		HorizontalPanel headerHorizontalPanel1 = new HorizontalPanel();
		htmlForUserNameIndication.setHTML("<b>Текущий пользователь: </b>");
		headerVerticalPanel.add(headerHorizontalPanel1);
		headerHorizontalPanel1.setSize("100%", "100%");

		Anchor onMainPageLink =
			new Anchor("<b>На главную</b>", true, Window.Location.getQueryString());

		headerHorizontalPanel1.add(onMainPageLink);

		headerHorizontalPanel1.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);

		Anchor exitLink = new Anchor("<b>Выход</b>", true);
		exitLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent arg0) {

				RequestBuilder builder =
					new RequestBuilder(RequestBuilder.GET, "auth/logoutServlet?nocache="
							+ (new Date()).getTime());
				builder.setTimeoutMillis(LOGOUT_TIMEOUT);
				try {
					builder.sendRequest(null, new RequestCallback() {
						@Override
						public void onError(final Request request, final Throwable exception) {

							Window.Location.assign(AccessToDomModel.getAppContextPath()
									+ "/logout");
						}

						@Override
						public void onResponseReceived(final Request request,
								final Response response) {
							Window.Location.assign(AccessToDomModel.getAppContextPath()
									+ "/logout");
						}
					});

				} catch (RequestException e) {
					Window.alert("Failed to send the request: " + e.getMessage());
				}
			}

		});

		Anchor aboutLink = new Anchor("О программе...", false);

		aboutLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent arg0) {

				DialogBoxWithCaptionButton db = new DialogBoxWithCaptionButton("О программе...");

				HTML about = new HTML();
				String fff =
					(AppCurrContext.getInstance().getServerCurrentState().getIsNativeUser()) ? "внутренним"
							: "внешним";
				String textHTML =
					"<p><img src='resources/internal/logo.gif' alt='КУРС' /></p>"
							+ "<img src='resources/internal/favicon32.png' alt='' />&nbsp;Showcase&nbsp;"
							+ AppCurrContext.getInstance().getServerCurrentState().getAppVersion()
							+ "<br /><br />"
							+

							"Copyright ООО 'КУРС-ИТ', 1998-2011 <br />"
							+ "Тел/факс: +7(495)640-2772<br />"
							+ "E-mail: <a href='mailto://info@mail.ru'>info@curs.ru</a>"
							+ " <br/> <a href='http://www.curs.ru' target='_blank'>http://www.curs.ru</a><br />"

							+ "<br />Версия SQL сервера: "
							+ AppCurrContext.getInstance().getServerCurrentState().getSqlVersion()
							+ "<br />"
							+ "Версия JAVA на сервере: "
							+ AppCurrContext.getInstance().getServerCurrentState()
									.getJavaVersion()
							+ "<br />"
							+ "Версия сервлет контейнера: "
							+ AppCurrContext.getInstance().getServerCurrentState()
									.getServletContainerVersion() + "<br />"
							+ "Текущий пользователь '"
							+ AppCurrContext.getInstance().getServerCurrentState().getUserName()
							+ "'" + "	является " + fff;
				about.setHTML(textHTML);
				final int n500 = 500;
				final int n310 = 310;
				about.setPixelSize(n500, n310);
				db.add(about);
				db.center();
				db.show();

			}

		});

		HorizontalPanel hp = new HorizontalPanel();
		hp.add(aboutLink);
		aboutLink.setStyleName("aboutLink");
		htmlForUserNameIndication.setStyleName("currentUserWidget");
		hp.add(htmlForUserNameIndication);
		hp.add(exitLink);
		headerHorizontalPanel1.add(hp);
		final int n = 27;
		headerVerticalPanel.setWidth(Window.getClientWidth() - n + "px");
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				int width = event.getWidth() - n;
				headerVerticalPanel.setWidth(width + "px");
			}
		});
		return headerVerticalPanel;

	}

	private void fillServerCurrentStateInfoToTheAppropriatePanels() {
		if (AppCurrContext.getInstance().getServerCurrentState() != null) {
			htmlForUserNameIndication.setHTML("Текущий пользователь: <b>"
					+ AppCurrContext.getInstance().getServerCurrentState().getUserName() + "</b>");
		}
	}

	@SuppressWarnings("unused")
	private Widget createHeaderImage() {
		Image im = new Image();
		im.setUrl("resources/header.jpg");
		return im;
	}

}
