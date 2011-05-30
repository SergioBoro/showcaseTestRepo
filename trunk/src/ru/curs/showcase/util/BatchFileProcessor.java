package ru.curs.showcase.util;

import java.io.*;

/**
 * Класс для пакетных операций с файлами. Используется совместно с классами,
 * реализующими интерфейс {@link ru.curs.showcase.util.FileAction FileAction}:
 * 
 * {@link ru.curs.showcase.util.CopyFileAction CopyFileAction}. а также
 * интерфейс {@link java.io.FilenameFilter FilenameFilter}:
 * {@link ru.curs.showcase.util.RegexFilenameFilter RegexFilenameFilter}. Пример
 * использования: <code>
 * BatchFileProcessor fprocessor = new BatchFileProcessor(sourceDir, new
 * RegexFilenameFilter( "^[.].*", false)); try { fprocessor.process(new
 * CopyFileAction(destDir, true)); } catch (IOException e) {
 * LOGGER.error("Ошибка копирования файла:" + e.getMessage()); }
 * </code>
 * 
 * @author den
 * 
 */
public class BatchFileProcessor {
	/**
	 * Исходный каталог с файлами.
	 */
	private final String sourceDir;
	/**
	 * Фильтр для выборки файлов из каталога.
	 */
	private final FilenameFilter filter;

	public BatchFileProcessor(final String aSourceDir, final FilenameFilter aFilter) {
		this.sourceDir = aSourceDir;
		this.filter = aFilter;
	}

	public BatchFileProcessor(final String aSourceDir) {
		this.sourceDir = aSourceDir;
		this.filter = null;
	}

	/**
	 * Выполняет действие над группой файлов.
	 * 
	 * @param action
	 *            - действие.
	 * @throws IOException
	 */
	public void process(final FileAction action) throws IOException {
		File fdir = new File(sourceDir);
		File[] flist = fdir.listFiles(filter);
		for (File f : flist) {
			if (f.isFile()) {
				action.perform(f);
			} else if (f.isDirectory()) {
				BatchFileProcessor bfp =
					new BatchFileProcessor(sourceDir + File.separator + f.getName(), filter);
				bfp.process(action.cloneForHandleChildDir(f.getName()));
			}
		}
	}
}
