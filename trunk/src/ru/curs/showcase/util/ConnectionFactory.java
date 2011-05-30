package ru.curs.showcase.util;

import java.sql.*;

import ru.curs.showcase.exception.DBConnectException;

/**
 * Фабрика для создания соединений с БД.
 * 
 * @author den
 * 
 */
public final class ConnectionFactory {
	/**
	 * Параметр файла настроек приложения, содержащий адрес для соединения с SQL
	 * сервером через JDBC.
	 */
	public static final String CONNECTION_URL_PARAM = "rdbms.connection.url";
	static final String CONNECTION_USERNAME_PARAM = "rdbms.connection.username";
	static final String CONNECTION_PASSWORD_PARAM = "rdbms.connection.password";

	private ConnectionFactory() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Функция получения нового соединения.
	 * 
	 * @return - соединение.
	 */
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(
					AppProps.getRequiredValueByName(CONNECTION_URL_PARAM),
					AppProps.getRequiredValueByName(CONNECTION_USERNAME_PARAM),
					AppProps.getRequiredValueByName(CONNECTION_PASSWORD_PARAM));
		} catch (SQLException e) {
			throw new DBConnectException(e);
		}
	}
}
