/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.showcase.app.api.ServerCurrentState;
import ru.curs.showcase.app.api.datapanel.DataPanel;
import ru.curs.showcase.app.api.event.ActionHolder;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 
 * Текущий контекст приложения (клиентской части) - СИНГЛЕТОН.
 * 
 * @author anlug
 * 
 */
public final class AppCurrContext extends ActionHolder {

	/**
	 * Переменная хранящая текущее состояние приложение (версия, текущее имя
	 * пользователя и т.п.).
	 */
	private static ServerCurrentState serverCurrentState;

	/**
	 * Синглетон клиентской части приложения.
	 */
	private static AppCurrContext appCurrContext;

	/**
	 * MainPanel для текущей сессии.
	 */
	private static MainPanel mainPanel;

	/**
	 * Переменная которая хранит в себе ссылку на текущее открытое окно (скорее
	 * всего модальное) с элементами информационной панели. Если ссылка равно
	 * null то активного открытого окна не открыто.
	 */
	private static WindowWithDataPanelElement currentOpenWindowWithDataPanelElement;

	/**
	 * Переменная регистрации обработчика onSelect на TabPanel.
	 */
	private HandlerRegistration regTabPanelSelectionHandler;

	/**
	 * @return the regTabPanelSelectionHandler
	 */
	public HandlerRegistration getRegTabPanelSelectionHandler() {
		return regTabPanelSelectionHandler;
	}

	/**
	 * @param aregTabPanelSelectionHandler
	 *            the regTabPanelSelectionHandler to set
	 */
	public void setRegTabPanelSelectionHandler(
			final HandlerRegistration aregTabPanelSelectionHandler) {
		this.regTabPanelSelectionHandler = aregTabPanelSelectionHandler;
	}

	/**
	 * Метаданные информационной панели.
	 */
	private static DataPanel dataPanelMetaData;

	/**
	 * Коллекция объектов, которая связывает UI элементы DataPanel (вкладки и
	 * элементы на вкладках) c метаданными.
	 */
	private static List<UIDataPanelTab> uiDataPanel = new ArrayList<UIDataPanelTab>();

	/**
	 * @return the uiWidgetsAndData
	 */
	public List<UIDataPanelTab> getUiDataPanel() {
		return uiDataPanel;
	}

	/**
	 * @param auiDataPanel
	 *            the List<UIDataPanelTab> to set
	 */
	public void setUiDataPanel(final List<UIDataPanelTab> auiDataPanel) {
		uiDataPanel = auiDataPanel;
	}

	private AppCurrContext() {
		super();
	}

	/**
	 * @return возвращает синглетон AppCurrContext.
	 */
	public static AppCurrContext getInstance() {
		if (appCurrContext == null) {
			appCurrContext = new AppCurrContext();
			AppCurrContext.currentOpenWindowWithDataPanelElement = null;
			AppCurrContext.serverCurrentState = null;
		}
		return appCurrContext;
	}

	/**
	 * @param acurrentOpenWindowWithDataPanelElement
	 *            the currentOpenWindowWithDataPanelElement to set
	 */
	public void setCurrentOpenWindowWithDataPanelElement(
			final WindowWithDataPanelElement acurrentOpenWindowWithDataPanelElement) {
		AppCurrContext.currentOpenWindowWithDataPanelElement =
			acurrentOpenWindowWithDataPanelElement;
	}

	/**
	 * @return the currentOpenWindowWithDataPanelElement
	 */
	public WindowWithDataPanelElement getCurrentOpenWindowWithDataPanelElement() {
		return currentOpenWindowWithDataPanelElement;
	}

	/**
	 * @param adataPanelMetaData
	 *            the dataPanelMetaData to set
	 */
	public void setDataPanelMetaData(final DataPanel adataPanelMetaData) {
		dataPanelMetaData = adataPanelMetaData;
	}

	/**
	 * @return the dataPanelMetaData
	 */
	public DataPanel getDataPanelMetaData() {
		return dataPanelMetaData;
	}

	/**
	 * @param aserverCurrentState
	 *            the serverCurrentState to set
	 */
	public void setServerCurrentState(final ServerCurrentState aserverCurrentState) {
		serverCurrentState = aserverCurrentState;
	}

	/**
	 * @return the serverCurrentState
	 */
	public ServerCurrentState getServerCurrentState() {
		return serverCurrentState;
	}

	/**
	 * @param amainPanel
	 *            the mainPanel to set
	 */
	public void setMainPanel(final MainPanel amainPanel) {
		AppCurrContext.mainPanel = amainPanel;
	}

	/**
	 * @return the mainPanel
	 */
	public MainPanel getMainPanel() {
		return mainPanel;
	}

}
