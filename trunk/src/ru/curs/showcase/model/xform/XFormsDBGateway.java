package ru.curs.showcase.model.xform;

import java.io.*;
import java.sql.*;

import ru.curs.showcase.app.api.CommandResult;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

/**
 * Шлюз к БД для получения XForms.
 * 
 * @author den
 * 
 */
public final class XFormsDBGateway extends HTMLBasedSPCallHelper implements XFormsGateway {
	static final String DOWNLOAD_ERROR =
		"SQL запрос на скачивание файла вернул ошибку с кодом %d - '%s'";
	protected static final String SETTINGS_XSD = "xformssettings.xsd";
	public static final String OUTPUT_COLUMNNAME = "xformssettings";
	static final String OUTPUTDATA_PARAM = "outputdata";
	static final String INPUTDATA_PARAM = "inputdata";
	static final String XFORMSDATA_PARAM = "xformsdata";
	static final String ERROR_MES_COL = "error_mes";
	static final String UPLOAD_ERROR =
		"Запрос для загрузки файла на SQL сервер вернул ошибку с кодом %d - %s";

	@Override
	public HTMLBasedElementRawData getInitialData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		setElementInfo(elementInfo);
		setContext(context);

		return stdGetData();
	}

	@Override
	public String getOutSettingsParam() {
		return OUTPUT_COLUMNNAME;
	}

	@Override
	protected String getSqlTemplate() {
		return "{call [dbo].[%s](?, ?, ?, ?, ?, ?, ?)}";
	}

	/**
	 * Возвращает шаблон запроса для сохранения данных.
	 * 
	 * @return - шаблон.
	 */
	protected String getSaveSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?, ?, ?, ?, ?)}";
	}

	/**
	 * Возвращает шаблон для Submission.
	 * 
	 * @return - шаблон.
	 */
	protected String getSubmissionSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?)}";
	}

	private String getFileSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.XFORMS;
	}

	@Override
	public CommandResult saveData(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final String data) {
		check(elementInfo);
		if (!elementInfo.enabledSimpleSave()) {
			throw new IncorrectElementException(elementInfo.toString());
		}
		setElementInfo(elementInfo);
		setContext(context);

		try {
			setDb(ConnectionFactory.getConnection());
			CommandResult result;
			try {
				String sql =
					String.format(getSaveSqlTemplate(), elementInfo.getSaveProc().getName());
				setCs(getDb().prepareCall(sql));
				getCs().registerOutParameter(1, java.sql.Types.INTEGER);
				setupGeneralParameters();
				getCs().setString(XFORMSDATA_PARAM, data);
				getCs().registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
				getCs().execute();
				int errorCode = getCs().getInt(1);
				if (errorCode == 0) {
					result = CommandResult.newSuccessResult();
				} else {
					result =
						CommandResult.newErrorResult(errorCode, getCs().getString(ERROR_MES_COL));
				}
			} finally {
				releaseResources();
			}
			return result;
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	public RequestResult handleSubmission(final String aProcName, final String aInputData) {
		try {
			Connection db = ConnectionFactory.getConnection();
			RequestResult result;
			try {
				String sql = String.format(getSubmissionSqlTemplate(), aProcName);
				CallableStatement cs = db.prepareCall(sql);
				cs.setString(INPUTDATA_PARAM, aInputData);
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
				cs.registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
				cs.registerOutParameter(OUTPUTDATA_PARAM, java.sql.Types.SQLXML);
				cs.execute();
				int errorCode = cs.getInt(1);
				if (errorCode == 0) {
					result =
						RequestResult.newSuccessResult(cs.getSQLXML(OUTPUTDATA_PARAM).getString());
				} else {
					result = RequestResult.newErrorResult(errorCode, cs.getString(ERROR_MES_COL));
				}
			} finally {
				db.close();
			}
			return result;
		} catch (SQLException e) {
			if (SolutionDBException.isSolutionDBException(e)) {
				throw new SolutionDBException(e);
			} else {
				throw new DBQueryException(e, aProcName);
			}
		}
	}

	@Override
	public String getDataParam() {
		return XFORMSDATA_PARAM;
	}

	@Override
	protected String getSettingsSchema() {
		return SETTINGS_XSD;
	}

	@Override
	public DataFile<ByteArrayOutputStream> downloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final String linkId, final String data) {
		check(elementInfo);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(elementInfo.toString());
		}
		setElementInfo(elementInfo);
		setContext(context);

		try {
			setDb(ConnectionFactory.getConnection());
			DataFile<ByteArrayOutputStream> result;
			try {
				String sql = String.format(getFileSqlTemplate(), proc.getName());
				setCs(getDb().prepareCall(sql));
				getCs().registerOutParameter(1, java.sql.Types.INTEGER);
				setupGeneralParameters();
				getCs().setString(XFORMSDATA_PARAM, data);
				getCs().registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
				getCs().registerOutParameter(FILENAME_TAG, java.sql.Types.VARCHAR);
				getCs().registerOutParameter(FILE_TAG, java.sql.Types.BLOB);
				getCs().execute();
				int errorCode = getCs().getInt(1);
				if (errorCode == 0) {
					String fileName = getCs().getString(FILENAME_TAG);
					Blob blob = getCs().getBlob(FILE_TAG);
					InputStream blobIs = blob.getBinaryStream();
					StreamConvertor dup = new StreamConvertor(blobIs);
					ByteArrayOutputStream os = dup.getOutputStream();
					result = new DataFile<ByteArrayOutputStream>(os, fileName);
				} else {
					throw new DBQueryException(proc.getName(), String.format(DOWNLOAD_ERROR,
							errorCode, getCs().getString(ERROR_MES_COL)));
				}
			} finally {
				releaseResources();
			}
			return result;
		} catch (SQLException e) {
			dbExceptionHandler(e);
		} catch (IOException e2) {
			throw new CreateObjectError(e2);
		}
		return null;
	}

	@Override
	public void uploadFile(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String linkId, final String data, final DataFile<InputStream> file) {
		check(elementInfo);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(elementInfo.toString());
		}
		setElementInfo(elementInfo);
		setContext(context);

		try {
			setDb(ConnectionFactory.getConnection());
			try {
				String sql = String.format(getFileSqlTemplate(), proc.getName());
				setCs(getDb().prepareCall(sql));
				getCs().registerOutParameter(1, java.sql.Types.INTEGER);
				setupGeneralParameters();
				getCs().setString(XFORMSDATA_PARAM, data);
				getCs().setString(FILENAME_TAG, file.getName());
				getCs().setBinaryStream(FILE_TAG, file.getData());
				getCs().registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
				getCs().execute();
				int errorCode = getCs().getInt(1);
				if (errorCode != 0) {
					throw new DBQueryException(proc.getName(), String.format(UPLOAD_ERROR,
							errorCode, getCs().getString(ERROR_MES_COL)));
				}
			} finally {
				releaseResources();
			}
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
	}

}
