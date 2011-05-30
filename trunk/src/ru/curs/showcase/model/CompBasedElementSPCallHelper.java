package ru.curs.showcase.model;

import java.sql.SQLException;

import ru.curs.showcase.exception.DBQueryException;

/**
 * Вспомогательный класс для получения данных элементов инф. панели, основанных
 * на компонентах.
 * 
 * @author den
 * 
 */
public abstract class CompBasedElementSPCallHelper extends SPCallHelper {
	/**
	 * Стандартная функция выполнения запроса с проверкой на возврат результата.
	 * 
	 * @throws SQLException
	 * @throws DBQueryException
	 */
	protected void stdGetResults() throws SQLException {
		boolean hasResult = getCs().execute();
		if (!hasResult) {
			throw new DBQueryException(getElementInfo(), NO_RESULTSET_ERROR);
		}
	}
}
