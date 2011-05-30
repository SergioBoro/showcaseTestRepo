package ru.curs.showcase.app.server;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.app.api.services.GeneralServerException;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Класс-обработчик на получение файла из БД с помощью хранимой процедуры.
 * Используется для скачивания файлов из XForms.
 * 
 * @author den
 * 
 */
public final class DownloadHandler extends AbstractDownloadHandler {
	/**
	 * Ссылка на файл.
	 */
	private String linkId;

	/**
	 * Данные, введенные пользователем.
	 */
	private String data;

	@Override
	protected void processFiles() throws GeneralServerException {
		ServiceLayerDataServiceImpl serviceLayer =
			new ServiceLayerDataServiceImpl(getRequest().getSession().getId());
		setOutputFile(serviceLayer.getDownloadFile(getContext(), getElementInfo(), linkId, data));
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		super.getParams();
		linkId = decodeParamValue(getRequest().getParameter("linkId"));
		data = decodeParamValue(getRequest().getParameter("data"));
	}

	public String getLinkId() {
		return linkId;
	}

	/**
	 * Возвращает новый экземпляр обработчика.
	 * 
	 * @return - новый экземпляр.
	 */
	public static AbstractFilesHandler newInstance() {
		return new DownloadHandler();
	}

	public String getData() {
		return data;
	}

}
