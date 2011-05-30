package ru.curs.showcase.app.server;

/**
 * Класс с данными UserData.
 */
public final class UserData {
	/**
	 * path.
	 */
	private final String path;
	/**
	 * Ширина для разделителя столбцов в гриде.
	 */
	private Integer gridColumnGapWidth = null;

	public UserData(final String aPath) {
		path = aPath;
	}

	public String getPath() {
		return path;
	}

	public Integer getGridColumnGapWidth() {
		return gridColumnGapWidth;
	}

	public void setGridColumnGapWidth(final Integer aGidColumnGapWidth) {
		gridColumnGapWidth = aGidColumnGapWidth;
	}

}