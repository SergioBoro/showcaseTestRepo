package ru.curs.showcase.model.xform;

import java.io.*;

import ru.curs.showcase.app.api.CommandResult;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;

/**
 * Шлюз для получения данных, необходимых для построения элемента панели типа
 * XForms.
 * 
 * @author den
 * 
 */
public interface XFormsGateway {

	/**
	 * Основная функция получения данных. Возвращает начальные данные для XForms
	 * и настройки элемента, в частности события.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - xml данные.
	 */
	HTMLBasedElementRawData getInitialData(CompositeContext context, DataPanelElementInfo element);

	/**
	 * Сохраняет данные, введенные в карточке на основе XForms.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @param data
	 *            - данные для сохранения.
	 * @return - результат процедуры сохранения.
	 * 
	 */
	CommandResult saveData(CompositeContext context, DataPanelElementInfo element, String data);

	/**
	 * Возвращает файл для XForms.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param linkId
	 *            - идентификатор ссылки на файл
	 * @param data
	 *            - данные, введенные пользователем.
	 * @return - файл.
	 */
	DataFile<ByteArrayOutputStream> downloadFile(CompositeContext context,
			DataPanelElementInfo elementInfo, String linkId, String data);

	/**
	 * Загружает (или модифицирует) дополнительные данные через Submission.
	 * 
	 * @param procName
	 *            - идентификатор процедуры.
	 * @param inputData
	 *            - дополнительные данные, требуемые для загрузки (как правило в
	 *            формате XML).
	 * @return - требуемые данные (как правило в формате XML).
	 * 
	 */
	RequestResult handleSubmission(String procName, String inputData);

	/**
	 * Загружает файл в хранилище.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param linkId
	 *            - ссылка на файл.
	 * @param data
	 *            - данные, введенные пользователем.
	 * @param aFile
	 *            - файл.
	 * **/
	void uploadFile(CompositeContext context, DataPanelElementInfo elementInfo, String linkId,
			String data, DataFile<InputStream> aFile);
}
