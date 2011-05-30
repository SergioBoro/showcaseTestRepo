package ru.curs.showcase.app.client.api;

/**
 * Интерфейс обработчика окончания загрузки файлов на сервер.
 * 
 * @author den
 * 
 */
public interface UploadSubmitEndHandler {
	/**
	 * Обработчик окончания загрузки файлов на сервер.
	 * 
	 * @param aRes
	 *            - был ли выбран файл.
	 */
	void onEnd(boolean aRes);
}
