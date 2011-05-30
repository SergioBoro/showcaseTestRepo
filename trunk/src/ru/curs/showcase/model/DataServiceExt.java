package ru.curs.showcase.model;

import java.io.ByteArrayOutputStream;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.GeneralServerException;

/**
 * Расширенный интерфейс DataService, который содержит функции, не вошедшие в
 * GWT RPC интерфейс по причине невозможности их работы через GWT RPC.
 * 
 * @author den
 * 
 */
public interface DataServiceExt {
	/**
	 * Функция создания файла Excel по данным грида.
	 * 
	 * @param exportType
	 *            - тип экспорта.
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @param settings
	 *            - настройки грида.
	 * @param cs
	 *            - набор столбцов.
	 * @return - файл.
	 * @throws GeneralServerException
	 */
	ExcelFile generateExcelFromGrid(GridToExcelExportType exportType, CompositeContext context,
			DataPanelElementInfo element, GridRequestedSettings settings, ColumnSet cs)
			throws GeneralServerException;

	/**
	 * Выполняет xforms SQL submission - т.е. вызывает хранимую процедуру
	 * передавая ей данные из xforms и возвращает результат.
	 * 
	 * @param procName
	 *            - имя процедуры.
	 * @param content
	 *            - некие данные из формы.
	 * @return - результат выполнения submission.
	 * @throws GeneralServerException
	 */
	RequestResult handleSQLSubmission(String procName, String content)
			throws GeneralServerException;

	/**
	 * Выполняет xforms XSLT submission - т.е. вызывает XSL преобразование
	 * передавая ему данные из xforms и возвращает результат.
	 * 
	 * @param xsltFile
	 *            - имя файла с XSL преобразованием.
	 * @param content
	 *            - некие данные из формы.
	 * @return - результат выполнения submission.
	 * @throws GeneralServerException
	 */
	String handleXSLTSubmission(String xsltFile, String content) throws GeneralServerException;

	/**
	 * Возвращает файл для скачивания.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param linkId
	 *            - ссылка на файл.
	 * @param data
	 *            - данные, введенные пользователем.
	 * @return - файл.
	 * @throws GeneralServerException
	 */
	DataFile<ByteArrayOutputStream> getDownloadFile(CompositeContext context,
			DataPanelElementInfo elementInfo, String linkId, String data)
			throws GeneralServerException;

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
	 * @param file
	 *            - файл.
	 * @throws GeneralServerException
	 */
	void uploadFile(CompositeContext context, DataPanelElementInfo elementInfo, String linkId,
			String data, DataFile<ByteArrayOutputStream> file) throws GeneralServerException;
}
