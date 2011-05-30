package ru.curs.showcase.app.api;

/**
 * Класс текущего состояния приложения.
 * 
 * @author den
 * 
 */
public final class ServerCurrentState implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6409374109173355019L;

	/**
	 * Версия приложения.
	 */
	private String appVersion;

	/**
	 * Имя текущего пользователя.
	 */
	private String userName;

	/**
	 * Признак того, что это собственный пользователь приложения. Альтернатива -
	 * пользователь из AuthServer.
	 */
	private Boolean isNativeUser;

	/**
	 * Версия контейнера сервлетов.
	 */
	private String servletContainerVersion;

	/**
	 * Версия Java, которая используется на сервере.
	 */
	private String javaVersion;

	/**
	 * Время генерации данного объекта на сервере.
	 */
	private String serverTime;

	/**
	 * Версия SQL сервера.
	 */
	private String sqlVersion;

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(final String aAppVersion) {
		appVersion = aAppVersion;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String aUserName) {
		userName = aUserName;
	}

	public Boolean getIsNativeUser() {
		return isNativeUser;
	}

	public void setIsNativeUser(final Boolean aIsNativeUser) {
		isNativeUser = aIsNativeUser;
	}

	public String getServletContainerVersion() {
		return servletContainerVersion;
	}

	public void setServletContainerVersion(final String aServletContainerVersion) {
		servletContainerVersion = aServletContainerVersion;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(final String aJavaVersion) {
		javaVersion = aJavaVersion;
	}

	public String getServerTime() {
		return serverTime;
	}

	public void setServerTime(final String aServerTime) {
		serverTime = aServerTime;
	}

	@Override
	public String toString() {
		return "ServerCurrentState [appVersion=" + appVersion + ", userName=" + userName
				+ ", isNativeUser=" + isNativeUser + ", servletContainerVersion="
				+ servletContainerVersion + ", javaVersion=" + javaVersion + ", serverTime="
				+ serverTime + ", sqlVersion=" + sqlVersion + "]";
	}

	public String getSqlVersion() {
		return sqlVersion;
	}

	public void setSqlVersion(final String aSqlVersion) {
		sqlVersion = aSqlVersion;
	}
}
