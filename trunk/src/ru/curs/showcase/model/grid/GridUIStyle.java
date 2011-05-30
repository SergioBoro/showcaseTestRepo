package ru.curs.showcase.model.grid;

import ru.curs.gwt.datagrid.model.DataGridSettings;

/**
 * Стиль для настройке грида. Представляет собой шаблон "Стратегия".
 * 
 * @author den
 * 
 */
public interface GridUIStyle {
	/**
	 * Применяет стиль к настройкам грида.
	 * 
	 * @param gp
	 *            - профайл грида
	 * @param settings
	 *            - настройки.
	 */
	void apply(GridProps gp, DataGridSettings settings);
}
