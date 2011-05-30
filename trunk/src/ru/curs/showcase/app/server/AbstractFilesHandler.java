package ru.curs.showcase.app.server;

import java.io.*;
import java.net.URLDecoder;

import javax.servlet.http.*;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.util.TextUtils;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;

/**
 * Базовый обработчик для работы с файлами.
 * 
 * @author den
 * 
 */
public abstract class AbstractFilesHandler {
	/**
	 * Контекст.
	 */
	private CompositeContext context;
	/**
	 * Описание элемента.
	 */
	private DataPanelElementInfo elementInfo;

	/**
	 * Обрабатываемые HTTP запрос.
	 */
	private HttpServletRequest request;
	/**
	 * Формируемый HTTP ответ.
	 */
	private HttpServletResponse response;

	/**
	 * Основной метод обработчика.
	 * 
	 * @param aRequest
	 *            - запрос.
	 * @param aResponse
	 *            - ответ.
	 * @throws SerializationException
	 * @throws IOException
	 * @throws GeneralServerException
	 * @throws FileUploadException
	 */
	public void handle(final HttpServletRequest aRequest, final HttpServletResponse aResponse)
			throws SerializationException, IOException, GeneralServerException,
			FileUploadException {
		request = aRequest;
		response = aResponse;
		handleTemplateMethod();
	}

	private void handleTemplateMethod() throws SerializationException, IOException,
			GeneralServerException, FileUploadException {
		getParams();
		processFiles();
		fillResponse();
	}

	/**
	 * Функция для заполнения response.
	 * 
	 * @throws IOException
	 */
	protected abstract void fillResponse() throws IOException;

	public AbstractFilesHandler() {
		super();
	}

	/**
	 * Функция считывания параметров запроса на скачивание.
	 * 
	 * @throws SerializationException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	protected abstract void getParams() throws SerializationException, FileUploadException,
			IOException;

	/**
	 * Функция получения файла.
	 * 
	 * @throws GeneralServerException
	 */
	protected abstract void processFiles() throws GeneralServerException;

	/**
	 * Функция десериализации объекта, переданного в теле запроса.
	 * 
	 * @param data
	 *            - строка с urlencoded объектом.
	 * @return - объект.
	 * @throws SerializationException
	 */
	protected Object deserializeObject(final String data) throws SerializationException {
		ServerSerializationStreamReader streamReader =
			new ServerSerializationStreamReader(Thread.currentThread().getContextClassLoader(),
					null);
		streamReader.prepareToRead(data);
		return streamReader.readObject();
	}

	/**
	 * Стандартная функция для раскодировки текстовых параметров запроса.
	 * 
	 * @param value
	 *            - исходное значение параметра.
	 * @return - раскодированное значение.
	 * @throws UnsupportedEncodingException
	 */
	protected String decodeParamValue(final String value) throws UnsupportedEncodingException {
		return URLDecoder.decode(value, TextUtils.DEF_ENCODING);
	}

	public CompositeContext getContext() {
		return context;
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(final HttpServletRequest aRequest) {
		request = aRequest;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(final HttpServletResponse aResponse) {
		response = aResponse;
	}

	public void setContext(final CompositeContext aContext) {
		this.context = aContext;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		this.elementInfo = aElementInfo;
	}

}