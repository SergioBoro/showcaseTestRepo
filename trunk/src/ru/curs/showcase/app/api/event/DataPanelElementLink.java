package ru.curs.showcase.app.api.event;

import ru.curs.showcase.app.api.*;

/**
 * Ссылка на элемент информационной панели.
 * 
 * @author den
 * 
 */
public class DataPanelElementLink implements SerializableElement, GWTClonable {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8381576475440574251L;

	/**
	 * Идентификатор элемента.
	 */
	private String id;

	/**
	 * Переопределенный контекст элемента.
	 */
	private CompositeContext context;

	/**
	 * Признак того, что при перерисовке элемента у него нужно только сменить
	 * контекст, не выполняя запрос на сервер.
	 */
	private Boolean refreshContextOnly = false;

	/**
	 * Признак того, что при выполнении refreshContextOnly данный элемент нужно
	 * пропустить.
	 */
	private Boolean skipRefreshContextOnly = false;

	/**
	 * Признак того, что нужно сохранять пользовательские настройки данного
	 * элемента после выполнения действия. Данная настройка перекрывает
	 * аналогичную настройку действия (Action).
	 */
	private Boolean keepUserSettings;

	public DataPanelElementLink() {
		super();
	}

	public DataPanelElementLink(final String aId, final CompositeContext aContext) {
		super();
		id = aId;
		context = aContext;
	}

	public final String getId() {
		return id;
	}

	public final void setId(final String aId) {
		id = aId;
	}

	public final CompositeContext getContext() {
		return context;
	}

	public final void setContext(final CompositeContext aContext) {
		this.context = aContext;
	}

	/**
	 * Определяет, нужно ли скрывать элемент.
	 * 
	 * @return результат проверки.
	 */
	public boolean doHiding() {
		return context.doHiding();
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public DataPanelElementLink gwtClone() {
		DataPanelElementLink res = new DataPanelElementLink();
		res.id = id;
		res.context = context.gwtClone();
		res.refreshContextOnly = refreshContextOnly;
		res.skipRefreshContextOnly = skipRefreshContextOnly;
		res.keepUserSettings = keepUserSettings;
		return res;
	}

	public Boolean getRefreshContextOnly() {
		return refreshContextOnly;
	}

	public void setRefreshContextOnly(final Boolean aRefreshContextOnly) {
		refreshContextOnly = aRefreshContextOnly;
	}

	public Boolean getSkipRefreshContextOnly() {
		return skipRefreshContextOnly;
	}

	public void setSkipRefreshContextOnly(final Boolean aSkipRefreshContextOnly) {
		skipRefreshContextOnly = aSkipRefreshContextOnly;
	}

	public Boolean getKeepUserSettings() {
		return keepUserSettings;
	}

	public void setKeepUserSettings(final Boolean aKeepUserSettings) {
		keepUserSettings = aKeepUserSettings;
	}
}
