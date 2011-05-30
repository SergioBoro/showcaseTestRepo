package ru.curs.showcase.app.api.event;


/**
 * Класс API, служащий для корректного сохранения текущего контекста на
 * клиентской части. Также в данный класс вынесены все правила, по которым
 * происходит смена контекста.
 * 
 * @author den
 * 
 */
public class ActionHolder {

	public ActionHolder() {
		super();
	}

	/**
	 * Текущий Action. По нему отрабатываются клики внутри вкладок.
	 */
	private Action currentAction;

	/**
	 * Последний Action, пришедший из навигатора. По нему отрабатываются клики в
	 * навигаторе и клики по вкладками панели.
	 */
	private Action navigatorAction;

	public Action getCurrentAction() {
		return currentAction;
	}

	/**
	 * Метод установки нового текущего контекста, обновляющий поля Action так,
	 * чтобы корректно установить current значения.
	 * 
	 * @param newAction
	 *            - новый Action.
	 */
	public void setCurrentAction(final Action newAction) {
		Action clone = newAction.gwtClone();
		clone.actualizeBy(navigatorAction);
		leaveOldFilterForInsideTabAction(clone);
		currentAction = clone;
	}

	public Action getNavigatorAction() {
		return navigatorAction;
	}

	/**
	 * Метод установки нового контекста навигатора взамен старого при клике по
	 * вкладке, обновляющий поля Action так, чтобы корректно установить current
	 * значения.
	 * 
	 * @param newAction
	 *            - новый Action.
	 */
	public void setNavigatorActionFromTab(final Action newAction) {
		navigatorAction.setDataPanelActionType(DataPanelActionType.REFRESH_TAB);
		determineNavigatorKeepUserSettingsWhenTabSwitching(newAction);
		navigatorAction.getDataPanelLink().setTabId(newAction.getDataPanelLink().getTabId());
		navigatorAction.getDataPanelLink().setFirstOrCurrentTab(false);
	}

	/**
	 * Метод установки нового контекста навигатора взамен старого при клике в
	 * навигаторе, обновляющий поля Action так, чтобы корректно установить
	 * current значения.
	 * 
	 * @param newAction
	 *            - новый Action.
	 */
	public void setNavigatorAction(final Action newAction) {
		Action clone = newAction.gwtClone();
		if (navigatorAction != null) {
			clone.actualizeBy(navigatorAction);
		}
		navigatorAction = clone;
		currentAction = null;
	}

	private void determineNavigatorKeepUserSettingsWhenTabSwitching(final Action newAction) {
		if (!navigatorAction.getDataPanelLink().getTabId()
				.equals(newAction.getDataPanelLink().getTabId())) {
			navigatorAction.setKeepUserSettingsForAll(false);
		}
	}

	private void leaveOldFilterForInsideTabAction(final Action clone) {
		if ((clone.getDataPanelActionType() == DataPanelActionType.REFRESH_TAB)
				|| (clone.getDataPanelActionType() == DataPanelActionType.RELOAD_ELEMENTS)) {
			if (!clone.isFiltered()) {
				if (currentAction != null) {
					clone.filterBy(currentAction.getDataPanelLink().getContext().getFilter());
				}
			}
		}
	}
}