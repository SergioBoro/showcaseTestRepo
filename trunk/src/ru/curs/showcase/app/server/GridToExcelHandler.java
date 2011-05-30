package ru.curs.showcase.app.server;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.GeneralServerException;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Обработчик запроса на получение Excel файла по данным в гриде. Вызывается из
 * сервлета. Отдельный класс (и один объект на один запрос) нужны для того,
 * чтобы избежать проблем многопоточности.
 * 
 * @author den
 * 
 */
public class GridToExcelHandler extends AbstractDownloadHandler {
	/**
	 * Тип экспорта в Excel.
	 */
	private GridToExcelExportType exportType;
	/**
	 * Требуемые настройки грида.
	 */
	private GridRequestedSettings settings;
	/**
	 * Описание настроенного пользователем набора столбцов.
	 */
	private ColumnSet columnSet;

	public GridToExcelHandler() {
	}

	/**
	 * Создает новый экземпляр обработчика.
	 * 
	 * @return - новый экземпляр.
	 */
	public static GridToExcelHandler newInstance() {
		return new GridToExcelHandler();
	}

	@Override
	protected void processFiles() throws GeneralServerException {
		ServiceLayerDataServiceImpl serviceLayer =
			new ServiceLayerDataServiceImpl(getRequest().getSession().getId());
		setOutputFile(serviceLayer.generateExcelFromGrid(exportType, getContext(),
				getElementInfo(), settings, columnSet));
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		super.getParams();
		exportType = GridToExcelExportType.valueOf(getParam(GridToExcelExportType.class));
		settings =
			(GridRequestedSettings) deserializeObject(getParam(GridRequestedSettings.class));
		columnSet = (ColumnSet) deserializeObject(getParam(ColumnSet.class));
	}
}