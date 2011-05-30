package ru.curs.showcase.app.client.api;

/**
 * @author anlug
 * 
 *         Класс реализации интерфейса DataGridPanelCallbacks, реализующего
 *         функции обратного вызова из грида.
 * 
 */
public class DataGridPanelCallbacksEvents implements DataGridPanelCallbacks {

	@Override
	public void dataGridPanelClick() {
		ActionExecuter.execAction();
	}

}
