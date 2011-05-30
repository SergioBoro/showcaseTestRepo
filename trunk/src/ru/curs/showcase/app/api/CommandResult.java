package ru.curs.showcase.app.api;

/**
 * Результат выполнения команды.
 * 
 * @author den
 * 
 */
public class CommandResult implements SerializableElement {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 4714242403681928221L;
	/**
	 * Результат выполнения.
	 */
	private Boolean success;
	/**
	 * Код возврата команды. Используется в случае ошибки как код ошибки или в
	 * особых случаях.
	 */
	private Integer errorCode;
	/**
	 * Сообщение о результате выполнения команды. Используется в случае ошибки
	 * как сообщение об ошибке или в особых случаях.
	 */
	private String errorMessage;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(final Boolean aSuccess) {
		success = aSuccess;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(final Integer aErrorCode) {
		errorCode = aErrorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(final String aErrorMessage) {
		errorMessage = aErrorMessage;
	}

	/**
	 * Создает объект, сигнализирующий об успешном выполнении команды.
	 * 
	 * @return объект результата.
	 */
	public static CommandResult newSuccessResult() {
		CommandResult res = new CommandResult();
		res.success = true;
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
	public static CommandResult newErrorResult(final int aErrorCode, final String aErrorMes) {
		CommandResult res = new CommandResult();
		return res.setupErrorResult(aErrorCode, aErrorMes);
	}

	/**
	 * Инициализирует объект, сигнализирующий об неуспешном выполнении команды.
	 * 
	 * @param aErrorCode
	 *            - код ошибки.
	 * @param aErrorMes
	 *            - сообщение об ошибке.
	 * @return - объект результата.
	 * 
	 */
	protected CommandResult setupErrorResult(final int aErrorCode, final String aErrorMes) {
		success = false;
		errorCode = aErrorCode;
		errorMessage = aErrorMes;
		return this;
	}

	/**
	 * Возвращает стандартизированное сообщение об ошибке.
	 * 
	 * @return - сообщение.
	 */
	public String generateStandartErrorMessage() {
		String errorMes = "";
		if (errorCode != null) {
			errorMes = "Код ошибки: " + errorCode + ". ";
		}
		errorMes = errorMes + errorMessage;
		return errorMes;
	}
}
