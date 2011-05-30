package ru.curs.showcase.app.client.utils;

import ru.beta2.extra.gwt.ui.panels.DialogBoxWithCaptionButton;
import ru.curs.showcase.app.client.api.UploadEndHandler;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * Окно для загрузки файлов.
 * 
 * @author den
 * 
 */
public class UploadWindow extends DialogBoxWithCaptionButton {

	static final String SC_UPLOADER_WINDOW_CSS = "sc-uploader-holder";

	/**
	 * Форма для загрузки файлов на сервер.
	 */
	private UploadHelper uploadHelper = null;

	/**
	 * Обработчик на закрытие окна.
	 */
	private UploadEndHandler closeHandler = null;

	/**
	 * Основная панель окна.
	 */
	private final VerticalPanel holder = new VerticalPanel();

	/**
	 * Имя выбранного файла.
	 */
	private String fileName;

	public UploadWindow(final String caption) {
		super(false, true, caption);
		center();

		holder.setStylePrimaryName(SC_UPLOADER_WINDOW_CSS);
		add(holder);

		uploadHelper = new UploadHelper();
		uploadHelper.setSize("100%", "100%");
		holder.add(uploadHelper);
	}

	@Override
	public void closeWindow() {
		uploadHelper.hide();
		super.closeWindow();
		if (closeHandler != null) {
			closeHandler.onEnd(fileName != null, fileName);
		}
		fileName = null;
	};

	/**
	 * Функция отображения окна для загрузки файла.
	 * 
	 * @param linkId
	 *            - идентификатор ссылки на файл.
	 * @param aCloseHandler
	 *            - обработчик закрытия окна.
	 */
	public void runUpload(final String linkId, final UploadEndHandler aCloseHandler) {
		closeHandler = aCloseHandler;
		uploadHelper.runUpload(linkId, new ChangeHandler() {

			@Override
			public void onChange(final ChangeEvent aEvent) {
				fileName = ((FileUpload) aEvent.getSource()).getFilename();
				if ((fileName != null) && (!fileName.isEmpty())) {
					closeWindow();
				}
			}
		});

		if (uploadHelper.getParent() != holder) {
			holder.add(uploadHelper);
		}
		show();
	}

	public UploadHelper getUploadHelper() {
		return uploadHelper;
	}

}
