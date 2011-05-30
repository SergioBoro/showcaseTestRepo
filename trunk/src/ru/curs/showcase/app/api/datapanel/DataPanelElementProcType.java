package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Тип хранимой процедуры для элемента информационной панели.
 * 
 * @author den
 * 
 */
public enum DataPanelElementProcType implements SerializableElement {
	/**
	 * Процедура для сохранения.
	 */
	SAVE,
	/**
	 * Процедура для загрузки данных посредством Submission.
	 */
	SUBMISSION,
	/**
	 * Процедура для загрузки файла с сервера клиенту.
	 */
	DOWNLOAD,
	/**
	 * Процедура для загрузки файла на сервер.
	 */
	UPLOAD
}
