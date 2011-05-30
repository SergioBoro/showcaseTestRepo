package ru.curs.showcase.app.client;

import java.util.List;

import ru.beta2.extra.gwt.ui.selector.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.XForms;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с XForm.
 */
public class XFormPanel extends BasicElementPanelBasis {

	/**
	 * String mainInstance.
	 */
	private String mainInstance;

	/**
	 * HTML xf.
	 */
	private HTML xf = null;

	/**
	 * VerticalPanel.
	 */
	private final VerticalPanel p = new VerticalPanel();

	/**
	 * Окно для загрузки файлов на сервер.
	 */
	private UploadWindow uw = null;

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService = null;

	/**
	 * SelectorDataServiceAsync.
	 */
	private final SelectorDataServiceAsync selSrv = GWT.create(SelectorDataService.class);
	{
		((ServiceDefTarget) selSrv).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "SelectorDataService");
	}

	public SelectorDataServiceAsync getSelSrv() {
		return selSrv;
	}

	/**
	 * DataPanelElementInfo.
	 */
	private DataPanelElementInfo elementInfo;

	/**
	 * XForms xform.
	 */
	private XForms xform = null;

	/**
	 * Ф-ция, возвращающая панель с XForm.
	 * 
	 * @return - Панель с XForm.
	 */
	@Override
	public VerticalPanel getPanel() {
		return p;
	}

	@Override
	public DataPanelElement getElement() {
		return xform;
	}

	@Override
	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aelement) {
		this.elementInfo = aelement;
	}

	@Override
	public void hidePanel() {
		p.setVisible(false);
	}

	@Override
	public void showPanel() {
		p.setVisible(true);
	}

	/**
	 * Конструктор класса XFormPanel без начального показа XForm.
	 */
	public XFormPanel(final DataPanelElementInfo element) {

		setElementInfo(element);
		setContext(null);
		setIsFirstLoading(true);

		// --------------

		mainInstance = null;

	}

	/**
	 * Конструктор класса XFormPanel.
	 */
	public XFormPanel(final CompositeContext context, final DataPanelElementInfo element) {

		setContext(context);
		setElementInfo(element);
		setIsFirstLoading(true);

		// --------------

		mainInstance = null;

		p.clear();
		p.add(new HTML(Constants.PLEASE_WAIT_XFORM_1));

		setXForm(false);

	}

	@Override
	public void reDrawPanel(final CompositeContext context, final Boolean refreshContextOnly) {

		setContext(context);
		// --------------

		if ((!getIsFirstLoading()) && refreshContextOnly) {
			xform.updateAddContext(context);
		} else {

			p.clear();
			p.add(new HTML(Constants.PLEASE_WAIT_XFORM_2));
			setXForm(refreshContextOnly);

		}

	}

	private void setXForm(final Boolean refreshContextOnly) {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getXForms(getContext(), elementInfo, mainInstance,
				new GWTServiceCallback<XForms>("Ошибка при получении данных XForm с сервера") {

					@Override
					public void onSuccess(final XForms xform1) {

						xform = xform1;

						destroy();

						p.setSize("100%", "100%");

						xf = new HTML();

						xf.setWidth("100%");

						p.clear();
						p.add(xf);

						instrumentForm(xf, xform.getXFormParts());

						Action ac = xform.getActionForDependentElements();
						if (ac != null) {
							AppCurrContext.getInstance().setCurrentAction(ac);

							ActionExecuter.execAction();
						}

						if (getIsFirstLoading() && refreshContextOnly) {
							xform.updateAddContext(getContext());
						}
						setIsFirstLoading(false);
					}
				});

	}

	/**
	 * Возвращает содержимое mainInstance.
	 * 
	 * @return содержимое mainInstance
	 * 
	 */
	public String getMainInstance() {
		fillMainInstance();
		return mainInstance;
	}

	private native void fillMainInstance() /*-{
		if ($wnd.xforms.defaultModel != null) {
		this.@ru.curs.showcase.app.client.XFormPanel::mainInstance = $wnd.Writer
		.toString($wnd.xforms.defaultModel
		.getInstanceDocument('mainInstance'));
		}
	}-*/;

	/**
	 * Установка процедур обратного вызова.
	 */
	// CHECKSTYLE:OFF
	private static native void setCallbackJSNIFunction() /*-{
		$wnd.gwtXFormSave = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::xFormPanelClickSave(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
		$wnd.gwtXFormFilter = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::xFormPanelClickFilter(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
		$wnd.showSelector =	@ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::showSelector(Lcom/google/gwt/core/client/JavaScriptObject;);
		$wnd.gwtXFormDownload = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::downloadFile(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
		$wnd.gwtXFormUpload = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::uploadFile(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;

	// CHECKSTYLE:ON

	/**
	 * Инициализирует X-форму, запуская инструментующий javascript.
	 */
	private static native void initForm() /*-{
		$wnd.init();
	}-*/;

	/**
	 * Инструментует панель с формой.
	 */
	private static void instrumentForm(final HTML html, final List<String> stringList) {

		// прописываем HTML формы
		if (stringList.size() > 0) {
			html.setHTML(stringList.get(0));
		}

		// прописываем динамический CSS
		if (stringList.size() > 1) {
			AccessToDomModel.addCSS(stringList.get(1));
		}

		// выставляем скрипты
		for (int i = 2; i < stringList.size(); i++) {
			AccessToDomModel.addScript(stringList.get(i));
		}

		setCallbackJSNIFunction();

		initForm();

	}

	/**
	 * Закрывает X-форму, деиницилизирует инструментовку и подчищает все
	 * динамически созданные скрипты и стили.
	 */
	private static native void destroy() /*-{
		//Деинициализируем механизм XSLTForms
		$wnd.xforms.close();

		//Подчищаем динамические стили
		var hdr = $doc.getElementsByTagName('head')[0];
		var ss1 = $doc.getElementById('dynastyle');
		if (ss1 != null)
		hdr.removeChild(ss1);

		//Подчищаем
		ss1 = $doc.getElementById('xf-model-config');
		if (ss1 != null)
		hdr.removeChild(ss1);

		//Подчищаем динамические скрипты
		var div = $doc.getElementById('target');
		while (div.childNodes.length > 0)
		div.removeChild(div.firstChild);
	}-*/;

	/**
	 * Закрывает форму, снимая всю ранее выставленную Javascript-инструментовку.
	 */
	public static synchronized void destroyXForms() {
		destroy();

		List<UIDataPanelTab> uiDataPanel = AppCurrContext.getInstance().getUiDataPanel();
		for (int i = 0; i < uiDataPanel.size(); i++) {
			List<UIDataPanelElement> uiElements = uiDataPanel.get(i).getUiElements();
			for (int j = 0; j < uiElements.size(); j++) {
				if (uiElements.get(j).getElementPanel().getElementInfo().getType() == DataPanelElementType.XFORMS) {
					uiElements.get(j).getElementPanel().getPanel().clear();
					((XFormPanel) uiElements.get(j).getElementPanel()).xf = null;
				}
			}
		}

	}

	@Override
	public void saveSettings(final Boolean reDrawWithSettingsSave) {
		if (!reDrawWithSettingsSave) {
			mainInstance = null;
		} else {
			if (xf != null) {
				fillMainInstance();
			}
		}
	}

	/**
	 * Должна вызываться перед показом модального окна, содержащего XFormPanel.
	 * 
	 * @param bep
	 *            - BasicElementPanel, который будет отображаться в модальном
	 *            окне
	 */
	public static void beforeModalWindow(final BasicElementPanel bep) {
		if ((bep instanceof XFormPanel) && (bep != null)) {
			List<UIDataPanelTab> uiDataPanel = AppCurrContext.getInstance().getUiDataPanel();
			for (int i = 0; i < uiDataPanel.size(); i++) {
				List<UIDataPanelElement> uiElements = uiDataPanel.get(i).getUiElements();
				for (int j = 0; j < uiElements.size(); j++) {
					if ((uiElements.get(j).getElementPanel().getElementInfo().getTab() == bep
							.getElementInfo().getTab())
							&& (uiElements.get(j).getElementPanel().getElementInfo().getType() == DataPanelElementType.XFORMS)
							&& (uiElements.get(j).getElementPanel() != bep)) {
						((XFormPanel) uiElements.get(j).getElementPanel())
								.reDrawBeforeModalWindow();
					}
				}
			}

			destroy();

		}
	}

	private void reDrawBeforeModalWindow() {
		fillMainInstance();

		p.clear();
		p.add(new HTML(Constants.PLEASE_WAIT_XFORM_3));

		xf = null;
	}

	public XForms getXform() {
		return xform;
	}

	public DataServiceAsync getDataService() {
		return dataService;
	}

	public UploadWindow getUw() {
		return uw;
	}

	public void setUw(final UploadWindow auw) {
		this.uw = auw;
	}

}
