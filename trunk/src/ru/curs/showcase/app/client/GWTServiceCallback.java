package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.client.utils.AccessToDomModel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Базовый класс для асинхронных колбэков при вызове сервисов gwt.
 * 
 * @param <T>
 *            тип результата
 */
public abstract class GWTServiceCallback<T> implements AsyncCallback<T> {

	public GWTServiceCallback(final String amsgErrorCaption) {
		super();
		this.msgErrorCaption = amsgErrorCaption;
	}

	/**
	 * Сообщение, которое будет показано в случае, если в процессе вызова
	 * сервиса GWT произошла ошибка.
	 */
	private final String msgErrorCaption;

	@Override
	public void onFailure(final Throwable caught) {

		if ("SessionNotAuthenticated".equals(caught.getMessage())) {
			Window.Location.assign(AccessToDomModel.getAppContextPath() + "/logout");
			// Window.Location.replace(Window.Location.getPath() + "logout");

		} else {

			MessageBox.showMessageWithDetails(msgErrorCaption, caught.getMessage(),
					GeneralServerException
							.checkExeptionTypeAndCreateDetailedTextOfException(caught));

		}

	}
}
