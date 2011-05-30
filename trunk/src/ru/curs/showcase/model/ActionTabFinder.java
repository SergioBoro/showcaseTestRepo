package ru.curs.showcase.model;

import ru.curs.showcase.app.api.event.DataPanelLink;

/**
 * Интерфейс для поиска и установки вкладки инф. панели у действия при неполных
 * указанных данных.
 * 
 * @author den
 * 
 */
public abstract class ActionTabFinder {
	/**
	 * Признак того, что нужно искать первую вкладку при первом открытии панели
	 * и оставаться на текущей при последующих.
	 */
	private static final String FIRST_OR_CURRENT_VALUE = "firstOrCurrent";

	/**
	 * Функция поиска вкладки для действия.
	 * 
	 * @param tabValue
	 *            - указатель на вкладку.
	 * @return - реальный идентификатор вкладки.
	 * @param link
	 *            - ссылка на панель.
	 */
	public String findTabForAction(final DataPanelLink link, final String tabValue) {
		if (tabValue != null) {
			if (!tabValue.equalsIgnoreCase(FIRST_OR_CURRENT_VALUE)) {
				return tabValue;
			} else {
				link.setFirstOrCurrentTab(true);
				return getFirstFromStorage(link);
			}
		} else {
			return getFirstFromStorage(link);
		}
	}

	/**
	 * Получает первую вкладку панели.
	 * 
	 * @param link
	 *            - идентификатор панели.
	 * @return - идентификатор первой вкладки.
	 */
	public abstract String getFirstFromStorage(final DataPanelLink link);
}
