/**
 * 
 */
package ru.curs.showcase.app.client.api;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * @author anlug
 * 
 */
public abstract class BasicElementPanelBasis implements BasicElementPanel {

	/**
	 * CompositeContext элемента, с которым об был отрисован последний раз..
	 */
	private CompositeContext context;

	public void setContext(final CompositeContext acontext) {
		this.context = acontext;
	}

	public CompositeContext getContext() {
		return context;
	}

	/**
	 * Переменная, отвечающая на ответ, первый раз ли грузится полноценно (с
	 * прорисовкой) элемент.
	 */
	private Boolean isFirstLoading = true;

	/**
	 * @param aisFirstLoading
	 *            the isFirstLoading to set
	 */
	public void setIsFirstLoading(final Boolean aisFirstLoading) {
		this.isFirstLoading = aisFirstLoading;
	}

	/**
	 * @return the isFirstLoading
	 */
	public Boolean getIsFirstLoading() {
		return isFirstLoading;
	}

	@Override
	public void saveSettings(final Boolean reDrawWithSettingsSave) {
		// если появятся пользовательские настройки - в этой функции,
		// переопределенной в панели элемента их нужно сохранять
	}

}
