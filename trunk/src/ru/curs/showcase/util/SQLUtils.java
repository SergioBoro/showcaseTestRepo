package ru.curs.showcase.util;

import java.sql.*;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

import ru.curs.showcase.exception.ResultSetHandleException;

import com.sun.rowset.CachedRowSetImpl;

/**
 * Содержит вспомогательные функции для работы с SQL.
 * 
 * @author den
 * 
 */
public final class SQLUtils {

	/**
	 * Создает кэшированный RowSet в памяти. Сейчас используется default и
	 * deprecated реализация CachedRowSetImpl.
	 * 
	 * @param rs
	 *            - открытый ResultSet с данными.
	 * @return - CachedRowSet.
	 * @throws SQLException
	 */
	public static RowSet cacheResultSet(final ResultSet rs) throws SQLException {
		CachedRowSet sql = new CachedRowSetImpl();
		sql.populate(rs);
		return sql;
	}

	private SQLUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Определяет наличие столбца с определенным заголовком в ResultSet.
	 * 
	 * @param md
	 *            - метаданные ResultSet.
	 * @param caption
	 *            - заголовок столбца.
	 * @return - результат проверки.
	 */
	public static boolean existsColumn(final ResultSetMetaData md, final String caption) {
		try {
			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (caption.equals(md.getColumnLabel(i))) {
					return true;
				}
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
		return false;
	}

	/**
	 * Определяет, является ли тип SQL датой .
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isDateType(final int aSqlType) {
		return (aSqlType == Types.DATE);
	}

	/**
	 * Определяет, является ли тип SQL временем.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isTimeType(final int aSqlType) {
		return (aSqlType == Types.TIME);
	}

	/**
	 * Определяет, является ли тип SQL датой и временем.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isDateTimeType(final int aSqlType) {
		return (aSqlType == Types.TIMESTAMP);
	}

	/**
	 * Определяет, является ли тип SQL целым числом.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isIntType(final int aSqlType) {
		return (aSqlType == Types.BIGINT) || (aSqlType == Types.INTEGER)
				|| (aSqlType == Types.SMALLINT) || (aSqlType == Types.TINYINT);
	}

	/**
	 * Определяет, является ли тип SQL дробным числом.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isFloatType(final int aSqlType) {
		return (aSqlType == Types.DECIMAL) || (aSqlType == Types.DOUBLE)
				|| (aSqlType == Types.FLOAT) || (aSqlType == Types.NUMERIC)
				|| (aSqlType == Types.REAL);
	}

	/**
	 * Определяет, является ли тип SQL строкой.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isStringType(final int aSqlType) {
		return (aSqlType == Types.CHAR) || (aSqlType == Types.NCHAR)
				|| (aSqlType == Types.NVARCHAR) || (aSqlType == Types.VARCHAR)
				|| (aSqlType == Types.LONGNVARCHAR) || (aSqlType == Types.LONGVARCHAR);
	}

}
