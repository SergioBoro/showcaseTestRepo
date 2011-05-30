package ru.curs.showcase.app.client.api;

import ru.beta2.extra.gwt.ui.selector.SelectorComponent;
import ru.curs.showcase.app.api.CommandResult;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Класс, реализующий функции обратного вызова из XFormPanel.
 * 
 */
public final class XFormPanelCallbacksEvents {

	private XFormPanelCallbacksEvents() {

	}

	/**
	 * Функция, которая будет выполняться по клику на кнопку Сохранить в XForm.
	 * 
	 * @param xformId
	 *            - Id элемента xForm.
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            - Данные xForm'ы
	 */
	public static void xFormPanelClickSave(final String xformId, final String linkId,
			final String data) {

		final XFormPanel curXFormPanel = getCurrentPanel(xformId);

		if (curXFormPanel != null) {
			// MessageBox.showSimpleMessage("Save=" + xformId, data);

			final Action ac = getActionByLinkId(linkId, curXFormPanel);

			if (curXFormPanel.getElementInfo().enabledSimpleSave()) {
				curXFormPanel.getDataService().saveXForms(curXFormPanel.getContext(),
						curXFormPanel.getElementInfo(), data,
						new GWTServiceCallback<CommandResult>(Constants.XFORM_SAVE_DATA_ERROR) {

							@Override
							public void onSuccess(final CommandResult result) {
								if (result.getSuccess()) {
									if (curXFormPanel.getUw() != null) {
										submitUploadForm(data, curXFormPanel, ac);
									} else {
										runAction(ac);
									}
								} else {
									MessageBox.showSimpleMessage(
											Constants.XFORM_CHECK_DURING_SAVE_ERROR,
											result.generateStandartErrorMessage());
								}

							}

						});
			} else {
				runAction(ac);
			}

		}
	}

	private static void runAction(final Action ac) {
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentAction(ac);
			ActionExecuter.execAction();
		}
	}

	private static Action
			getActionByLinkId(final String linkId, final XFormPanel currentXFormPanel) {
		Action ac = null;

		Event ev = currentXFormPanel.getXform().getEventManager().getEventForLink(linkId);

		if (ev != null) {
			ac = ev.getAction();
		}
		return ac;
	}

	private static void submitUploadForm(final String data, final XFormPanel currentXFormPanel,
			final Action ac) {
		UploadHelper uh = currentXFormPanel.getUw().getUploadHelper();
		try {

			uh.addStdPostParamsToBody(currentXFormPanel.getContext(),
					currentXFormPanel.getElementInfo());
		} catch (SerializationException e) {
			MessageBox.showSimpleMessage(Constants.XFORMS_UPLOAD_ERROR, e.getMessage());
		}
		uh.addParam("data", URL.encode(data));
		currentXFormPanel.getPanel().add(uh);
		uh.submit(new UploadSubmitEndHandler() {

			@Override
			public void onEnd(final boolean aRes) {
				runAction(ac);
			}
		});
		uh.clear();
	}

	/**
	 * Функция, которая будет выполняться по клику на кнопку Отфильтровать в
	 * XForm.
	 * 
	 * @param xformId
	 *            Id элемента xForm.
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            Данные xForm'ы
	 */
	public static void xFormPanelClickFilter(final String xformId, final String linkId,
			final String data) {
		XFormPanel currentXFormPanel = getCurrentPanel(xformId);

		if (currentXFormPanel != null) {
			// MessageBox.showSimpleMessage("Filter=" + xformId, data);

			Action ac = getActionByLinkId(linkId, currentXFormPanel);

			if (ac != null) {
				ac.filterBy(data);
				AppCurrContext.getInstance().setCurrentAction(ac);
				ActionExecuter.execAction();
			}
		}
	}

	/**
	 * Статический метод для открытия окна-селектора. Может быть использован
	 * непосредственно в javscript-е на форме.
	 * 
	 * @param o
	 *            Объект-хендлер окна-селектора
	 * 
	 */
	public static void showSelector(final JavaScriptObject o) {

		/**
		 * SelectorParam
		 */
		final class SelectorParam extends JavaScriptObject {

			protected SelectorParam() {

			};

			/**
			 * Id элемента xForm
			 * 
			 * @return String
			 */
			native String id()/*-{
		return this.id;
	}-*/;

			/**
			 * Название процедуры получения общего числа записей
			 * 
			 * @return String
			 */
			native String procCount()/*-{
		return this.procCount;
	}-*/;

			/**
			 * Название процедуры получения записей как таковых
			 * 
			 * @return String
			 */
			native String procList()/*-{
		return this.procList;
	}-*/;

			/**
			 * общие фильтры. Этот параметр передаётся хранимой процедуре БД
			 * (см. ниже) без изменений
			 * 
			 * @return String
			 */
			native String generalFilters()/*-{
		return this.generalFilters;
	}-*/;

			/**
			 * начальное значение поискового поля
			 * 
			 * @return String
			 */
			native String currentValue()/*-{
		return this.currentValue;
	}-*/;

			/**
			 * заголовок окна для выбора из больших списков.
			 * 
			 * @return String
			 */
			native String windowCaption()/*-{
		return this.windowCaption;
	}-*/;

			/**
			 * onSelectionComplete
			 * 
			 * @param ok
			 *            boolean
			 * 
			 * @param selected
			 *            JavaScriptObject
			 */
			native void onSelectionComplete(final boolean ok, final JavaScriptObject selected)/*-{
		this.onSelectionComplete(ok, selected);
	}-*/;
		}

		final SelectorParam param = (SelectorParam) o;

		XFormPanel currentXFormPanel =
			((XFormPanel) ActionExecuter.getElementPanelById(param.id()));

		if (currentXFormPanel != null) {

			SelectorComponent c =
				new SelectorComponent(currentXFormPanel.getSelSrv(), param.windowCaption());
			c.setSelectorListener(new SelectorComponent.SelectorListener() {
				@Override
				public void onSelectionComplete(final SelectorComponent selector) {
					param.onSelectionComplete(selector.isOK(), selector.getSelectedAsJsObject());
				}
			});
			c.center();

			c.initData(param.generalFilters(), param.procCount()
					+ "FDCF8ABB9B6540A89E350010424C2B80" + param.procList(), param.currentValue());

		}

	}

	/**
	 * Загружает файл с сервера.
	 * 
	 * @param xformId
	 *            Id элемента xForm
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            Данные xForm'ы
	 */
	public static void downloadFile(final String xformId, final String linkId, final String data) {
		XFormPanel currentXFormPanel = getCurrentPanel(xformId);

		if (currentXFormPanel != null) {
			DownloadHelper dh = DownloadHelper.getInstance();
			dh.clear();
			dh.setErrorCaption(Constants.XFORMS_DOWNLOAD_ERROR);
			dh.setAction("secured/download");

			try {
				dh.addParam("linkId", URL.encode(linkId));
				dh.addParam("data", URL.encode(data));
				dh.addStdPostParamsToBody(currentXFormPanel.getContext(),
						currentXFormPanel.getElementInfo());
				dh.submit();
			} catch (Exception e) {
				MessageBox.showSimpleMessage(Constants.XFORMS_DOWNLOAD_ERROR, e.getMessage());
			}
		}
	}

	/**
	 * Загружает файл на сервер.
	 * 
	 * @param o
	 *            JavaScriptObject
	 * 
	 */
	public static void uploadFile(final JavaScriptObject o) {
		/**
		 * SelectorParam
		 */
		final class UploadParam extends JavaScriptObject {

			protected UploadParam() {

			};

			/**
			 * Id элемента xForm
			 * 
			 * @return String
			 */
			native String xformsId()/*-{
		return this.xformsId;
	}-*/;

			/**
			 * Id ссылки на файл.
			 * 
			 * @return String
			 */
			native String linkId()/*-{
		return this.linkId;
	}-*/;

			/**
			 * onSelectionComplete
			 * 
			 * @param ok
			 *            boolean
			 * 
			 * @param fileName
			 *            String
			 */
			native void onSelectionComplete(final boolean ok, final String fileName)/*-{
		this.onSelectionComplete(ok, fileName);
	}-*/;
		}
		final UploadParam param = (UploadParam) o;

		final XFormPanel currentXFormPanel = getCurrentPanel(param.xformsId());

		if (currentXFormPanel != null) {

			if (currentXFormPanel.getUw() == null) {
				currentXFormPanel.setUw(new UploadWindow(Constants.XFORM_UPLOAD_CAPTION));
				currentXFormPanel.getPanel().add(currentXFormPanel.getUw());
				UploadHelper uh = currentXFormPanel.getUw().getUploadHelper();
				uh.setErrorCaption(Constants.XFORMS_UPLOAD_ERROR);
				uh.setAction("secured/upload");
			}
			currentXFormPanel.getUw().runUpload(param.linkId(), new UploadEndHandler() {

				@Override
				public void onEnd(final boolean res, final String filePath) {
					int index = filePath.lastIndexOf('\\');
					String fileName = filePath;
					if (index > -1) {
						fileName = fileName.substring(++index);
					}
					param.onSelectionComplete(res, fileName);
				}

			});
		}
	}

	private static XFormPanel getCurrentPanel(final String xformId) {
		return ((XFormPanel) ActionExecuter.getElementPanelById(xformId));
	}

}
