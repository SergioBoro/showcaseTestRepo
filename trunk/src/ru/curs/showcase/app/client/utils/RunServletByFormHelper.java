package ru.curs.showcase.app.client.utils;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.client.WebUtils;

import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

/**
 * Класс для "запуска" сервлетов, методами GET и POST.
 */
public abstract class RunServletByFormHelper extends FormPanel {

	/**
	 * VerticalPanel panel.
	 */
	private final VerticalPanel panel = new VerticalPanel();

	/**
	 * SerializationStreamFactory ssf.
	 */
	private SerializationStreamFactory ssf = null;

	/**
	 * Заголовок сообщения об ошибке.
	 */
	private String errorCaption = "";

	public RunServletByFormHelper() {
		super();
		// setWidget(panel);
		init();
		add(panel);
	}

	public VerticalPanel getPanel() {
		return panel;
	}

	/**
	 * Функция инициализации формы.
	 */
	protected void init() {
		initFormProps();
		initFormView();
		initFormHandlers();
	}

	/**
	 * Настройка обработчиков формы.
	 */
	protected abstract void initFormHandlers();

	/**
	 * Настройка внешнего вида формы.
	 */
	protected void initFormView() {

	}

	/**
	 * Настойка невизуальных свойств формы.
	 */
	protected void initFormProps() {
		setMethod(FormPanel.METHOD_POST);
	}

	/**
	 * Добавляет параметр для передачи в сервлет.
	 * 
	 * @param name
	 *            Название параметра
	 * @param value
	 *            Значение параметра
	 * 
	 */
	public void addParam(final String name, final String value) {
		Hidden res = new Hidden(name, value);
		panel.add(res);
	}

	/**
	 * Очищает настройки. Необходимо вызывать в начале каждого использования.
	 */
	@Override
	public void clear() {
		reset();

		// form.setMethod(FormPanel.METHOD_POST);
		// form.setEncoding(FormPanel.ENCODING_URLENCODED);
		// form.setAction("");

		panel.clear();

		setErrorCaption("");
	}

	/**
	 * Устанавливает заголовок сообщения, которое будет выводиться в случае
	 * ошибки вызова сервлета.
	 * 
	 * @param errorCaption1
	 *            Заголовок сообщения об ошибке
	 * 
	 */
	public void setErrorCaption(final String errorCaption1) {
		errorCaption = errorCaption1;
	}

	/**
	 * Возвращает "сериализатор" для gwt объектов, передаваемых при вызове
	 * сервлета.
	 * 
	 * @return - SerializationStreamFactory.
	 */
	public SerializationStreamFactory getObjectSerializer() {
		if (ssf == null) {
			ssf = WebUtils.createStdGWTSerializer();
		}
		return ssf;
	}

	/**
	 * Добавляет с тело запроса "стандартные" параметры - контекст и описание
	 * вызвавшего запрос элемента.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @throws SerializationException
	 */
	public void addStdPostParamsToBody(final CompositeContext context,
			final DataPanelElementInfo elementInfo) throws SerializationException {
		addParam(context.getClass().getName(), context.toParamForHttpPost(getObjectSerializer()));
		addParam(elementInfo.getClass().getName(),
				elementInfo.toParamForHttpPost(getObjectSerializer()));
	}

	public String getErrorCaption() {
		return errorCaption;
	}

}
