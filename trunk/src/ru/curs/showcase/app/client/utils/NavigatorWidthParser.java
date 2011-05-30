/**
 * 
 */
package ru.curs.showcase.app.client.utils;

/**
 * 
 * Набор функций для работы со сторкой в которой заданы ширина навигатора в
 * пикселях или процентах и ее анализом.
 * 
 * @author anlug
 * 
 */
public final class NavigatorWidthParser {

	private NavigatorWidthParser() {

	}

	/**
	 * Процедура, получающая абсолютную (число) ширину навигатора в процентах
	 * или пикселях.
	 * 
	 * @param s
	 *            - строка.
	 * @return - ширина навигатора в Integer.
	 */
	public static Integer getWidth(final String s) {
		String temp;
		switch (getWidthType(s)) {

		case PIXELS:
			int firstInsexOfPx = s.indexOf("px");
			temp = String.copyValueOf(s.toCharArray(), 0, firstInsexOfPx);
			return Integer.valueOf(temp);

		case PERCENTS:
			int firstInsexOfPercents = s.indexOf("%");
			temp = String.copyValueOf(s.toCharArray(), 0, firstInsexOfPercents);
			return Integer.valueOf(temp);

		default:
			return -1;

		}

	}

	/**
	 * Процедура, получающая тип (проценты или пикселы) значения, обозначающего
	 * ширину навигатора.
	 * 
	 * @param s
	 *            - строка.
	 * @return - WidthType
	 */
	public static WidthType getWidthType(final String s) {

		if (s.endsWith("px")) {
			return WidthType.PIXELS;
		} else if (s.endsWith("%")) {
			return WidthType.PERCENTS;
		} else {
			return WidthType.ERROR_OF_TYPE_RETRIEVING;
		}

	}

}
