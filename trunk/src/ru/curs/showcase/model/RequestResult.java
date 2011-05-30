package ru.curs.showcase.model;

import ru.curs.showcase.app.api.CommandResult;


/**
 * Результат работы хранимой процедуры, связанной с запросом данных из БД.
 * Используется в частности, для XForms Submissions.
 * 
 * @author den
 * 
 */
public final class RequestResult extends CommandResult {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -8940144274453299489L;

	/**
	 * Данные, которые вернула хранимая процедура (как правило, в формате XML).
	 */
	private String data;

	public String getData() {
		return data;
	}

	public void setData(final String aData) {
		data = aData;
	}

	/**
	 * Создает объект, сигнализирующий об успешном выполнении команды.
	 * 
	 * @param aData
	 *            - выходные данные.
	 * @return - результат.
	 */
	public static RequestResult newSuccessResult(final String aData) {
		RequestResult res = new RequestResult();
		res.setSuccess(true);
		res.data = aData;
		return res;
	}

	/**
	 * Создает объект, сигнализирующий об неуспешном выполнении команды.
	 * 
	 * @param aErrorCode
	 *            - код ошибки.
	 * @param aErrorMes
	 *            - сообщение об ошибке.
	 * @return - объект результата.
	 */
	public static RequestResult newErrorResult(final int aErrorCode, final String aErrorMes) {
		RequestResult res = new RequestResult();
		return (RequestResult) res.setupErrorResult(aErrorCode, aErrorMes);
	}
}
