package ru.curs.showcase.util;

import java.io.File;

/**
 * Фильтр regexp для поиска файлов.
 * 
 * @author den
 * 
 */
public class RegexFilenameFilter implements java.io.FilenameFilter {
	/**
	 * Шаблон для фильтра.
	 */
	private java.util.regex.Pattern pattern;

	/**
	 * Режим работы фильтра - на включение или на исключение.
	 */
	private final boolean include;

	public RegexFilenameFilter(final String aPattern, final boolean aInclude) {
		setPattern(aPattern);
		include = aInclude;
	}

	public void setPattern(final String aPattern) {
		pattern = java.util.regex.Pattern.compile(aPattern);
	}

	public String getPattern() {
		return pattern.pattern();
	}

	@Override
	public boolean accept(final File dir, final String fileName) {
		boolean res = pattern.matcher(fileName).matches();
		if (include) {
			return res;
		} else {
			return !res;
		}
	}
}