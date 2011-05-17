package ru.curs.showcase.util;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.*;

/**
 * Класс, содержащий общие функции для работы с текстом.
 * 
 * @author den
 * 
 */
public final class TextUtils {

	/**
	 * Внутренняя кодировка Java. Все Java String имеют данную кодировку по
	 * умолчанию.
	 */
	private static final String JAVA_ENCODING = "UTF-16";
	/**
	 * Кодировка по умолчанию в приложении. Все выходные и входные документы по
	 * умолчанию должны имеют данную кодировку (если явно не указано другое).
	 */
	public static final String DEF_ENCODING = "UTF-8";

	private TextUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Преобразует InputStream, полученный при обращении к SQL серверу, в
	 * строку. Может использоваться при работе с Gateway классами и\или при
	 * отладке.
	 * 
	 * @param is
	 *            - InputStream
	 * @return - строка с содержимым InputStream.
	 * @throws IOException
	 */
	public static String convertStreamToString(final InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			final int bufMaxLen = 4096;
			char[] buffer = new char[bufMaxLen];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, JAVA_ENCODING));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	/**
	 * Преобразует строку в InputStream. Может использоваться при работе с
	 * Gateway классами и\или при отладке.
	 * 
	 * @param str
	 *            - строка.
	 * @return - InputStream.
	 * @throws UnsupportedEncodingException
	 */
	public static InputStream convertStringToStream(final String str)
			throws UnsupportedEncodingException {
		return new ByteArrayInputStream(str.getBytes(DEF_ENCODING));

	}

	/**
	 * Возвращает числовое значение размера, извлеченное из переданной строки.
	 * 
	 * @param value
	 *            - строка с размером.
	 * @return - числовое значение.
	 */
	public static Integer getIntSizeValue(final String value) {
		Integer intValue = null;
		String strValue;
		if (value.indexOf("px") > -1) {
			strValue = value.substring(0, value.indexOf("px"));
			intValue = Integer.valueOf(strValue); // TODO проверить
		}
		return intValue;
	}

	/**
	 * Функция, возвращающая исходное слово, начинающееся с заглавной буквы. Все
	 * остальные буквы результата будут строчные.
	 * 
	 * @param source
	 *            - исходная строка.
	 * @return - преобразованная строка.
	 */
	public static String capitalizeWord(final String source) {
		return String.format("%s%s", source.substring(0, 1).toUpperCase(), source.substring(1));
	}

	/**
	 * Функция нечувствительной к реестру замены.
	 * 
	 * @param template
	 *            - шаблон для замены.
	 * @param source
	 *            - исходная строка.
	 * @param newValue
	 *            - значение для замены
	 * @return - результат после замены.
	 */
	public static String replaceCI(final String source, final String template,
			final String newValue) {
		Pattern pattern =
			Pattern.compile(template, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(source);
		String result = matcher.replaceAll(newValue);
		return result;
	}

	/**
	 * Перекодирует строку из неправильно определенной кодировки в правильную.
	 * 
	 * @param source
	 *            - исходный текст.
	 * @param sourceCharset
	 *            - кодировка исходного текста.
	 * @param destCharset
	 *            - правильная кодировка.
	 * @return - строка в верной кодировке.
	 * @throws UnsupportedEncodingException
	 */
	public static String recode(final String source, final String sourceCharset,
			final String destCharset) throws UnsupportedEncodingException {
		return new String(source.getBytes(sourceCharset), destCharset);
	}

	/**
	 * Функция, возвращающая строку с текущей датой.
	 * 
	 * @return строка с датой.
	 */
	public static String getCurrentLocalDate() {
		DateFormat df = DateFormat.getDateTimeInstance();
		return df.format(new Date());
	}
}