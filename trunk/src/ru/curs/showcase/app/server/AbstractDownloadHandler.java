package ru.curs.showcase.app.server;

import java.io.*;
import java.net.*;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.HTTPRequestRequiredParamAbsentException;
import ru.curs.showcase.model.DataFile;
import ru.curs.showcase.util.TextUtils;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Базовый обработчик для сервлетов, предназначенных для передачи файлов на
 * клиента.
 * 
 * @author den
 * 
 */
public abstract class AbstractDownloadHandler extends AbstractFilesHandler {
	/**
	 * Создаваемый файл.
	 */
	private DataFile<ByteArrayOutputStream> outputFile;

	@Override
	protected void fillResponse() throws IOException {
		String encName = URLEncoder.encode(outputFile.getName(), TextUtils.DEF_ENCODING);
		// Перекодирование нужно для корректной передачи русских символов в
		// имени файла.
		// Работает в IE, Chrome и Opera, не работает в Firefox и Safari.
		// На английские символы перекодировка не влияет.
		if (!ServletUtils.isOldIE(getRequest())) {
			getResponse().setContentType("application/octet-stream");
		} else {
			getResponse().setContentType("application/force-download");
		}
		// По агентурным данным для старых версий IE "application/octet-stream"
		// обрабатывается некорректно.
		getResponse().setHeader("Content-Disposition",
				String.format("attachment; filename=\"%s\"", encName));
		OutputStream out = getResponse().getOutputStream();
		out.write(outputFile.getData().toByteArray());
		out.close();
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		setContext(((CompositeContext) deserializeObject(getParam(CompositeContext.class))));
		setElementInfo(((DataPanelElementInfo) deserializeObject(getParam(DataPanelElementInfo.class))));
	}

	/**
	 * Получить значение параметра по классу объекта. Имя параметра должно
	 * совпадать с именем класса.
	 * 
	 * @param paramClass
	 *            - класс.
	 * @return - значение параметра.
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	protected String getParam(final Class paramClass) throws UnsupportedEncodingException {
		String paramName = paramClass.getName();
		String result = getRequest().getParameter(paramName);
		if (result == null) {
			throw new HTTPRequestRequiredParamAbsentException(paramName);
		}
		return URLDecoder.decode(result, TextUtils.DEF_ENCODING);
	}

	public DataFile<ByteArrayOutputStream> getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(final DataFile<ByteArrayOutputStream> aOutputFile) {
		outputFile = aOutputFile;
	}
}
