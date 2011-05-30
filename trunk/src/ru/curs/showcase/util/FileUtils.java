/**
 * 
 */
package ru.curs.showcase.util;

import java.io.*;

import org.slf4j.*;

/**
 * Класс, содержащий общие функции для работы с файлами.
 * 
 * @author anlug
 * 
 */
public final class FileUtils {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	private FileUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * Процедура копирования файла.
	 * 
	 * @param source
	 *            - полный путь к файлу, который будет скопирован (с указанием
	 *            имени файла).
	 * @param destination
	 *            - полный путь места, куда будет скопирован файл (без указания
	 *            имени файла)
	 * @return - возвращает результат копирования файла: true - в случае успеха,
	 *         false - в случае неудачи.
	 */
	public static boolean copyFile(final String source, final String destination) {
		try {
			File sourceFile = new File(source);
			BatchFileProcessor fprocessor =
				new BatchFileProcessor(sourceFile.getPath(), new RegexFilenameFilter(
						sourceFile.getName(), true));
			fprocessor.process(new CopyFileAction(destination));
			return true;
		} catch (FileNotFoundException ex) {
			LOGGER.error(ex.getMessage() + " in the specified directory.");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

		return false;
	}

	/**
	 * Удаляет каталог с подкаталогами и файлами.
	 * 
	 * @param dir
	 *            - удаляемый каталог.
	 * @return - результат удаления.
	 */
	public static boolean deleteDir(final File dir) {
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDir(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (dir.delete());
	}
}
