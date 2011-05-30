package ru.curs.showcase.app.api.grid;

import java.util.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.*;

/**
 * Класс, содержащий настройки грида, которые интерактивно могут изменять
 * пользователи в процессе работы с гридом. В частности, содержит информацию о
 * выделении в гриде. Информация о выделении может быть использована при печати,
 * а также выделение должно восстанавливаться в некоторых случаях - в частности,
 * при возврате из карточки.
 * 
 * @author den
 * 
 */
public class GridRequestedSettings extends TransferableElement implements SerializableElement {

	@Override
	public String toString() {
		return "GridRequestedSettings.sortedColumns=" + sortedColumns
				+ "&GridRequestedSettings.pageNumber=" + pageNumber
				+ "&GridRequestedSettings.pageSize=" + pageSize;
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2005065362465664382L;

	/**
	 * Набор столбцов, на которых установлена сортировка. Если null - сортировка
	 * не задана.
	 */
	private Collection<Column> sortedColumns = new ArrayList<Column>();

	/**
	 * Номер текущей страницы с данными. По-умолчанию - первая страница.
	 */
	private Integer pageNumber = 0;

	/**
	 * Установленный размер страницы. Если null - размер считывается из файла
	 * настроек.
	 */
	private Integer pageSize;

	/**
	 * Признак того, что нужно применять форматирование для дат и чисел при
	 * формировании грида. По умолчанию - нужно. Отключать эту опцию необходимо
	 * при экспорте в Excel.
	 */
	private Boolean applyLocalFormatting = true;

	/**
	 * Идентификатор выделенной по клику в гриде записи.
	 */
	private String currentRecordId = null;

	/**
	 * Идентификатор выделенного по клику в гриде столбца. Имеет смысл только в
	 * режиме выделения ячеек.
	 */
	private String currentColumnId = null;

	/**
	 * Массив идентификаторов выделенных с помощью селектора записей в гриде.
	 */
	private List<String> selectedRecordIds = new ArrayList<String>();

	/**
	 * Сбрасывает настройки таким образом, чтобы сервер вернул все записи на
	 * первой странице.
	 */
	public void resetForReturnAllRecords() {
		setPageNumber(0);
		setPageSize(Integer.MAX_VALUE);
	}

	/**
	 * Получение сортировки из запрашиваемых параметров.
	 * 
	 * @param aCurColumn
	 *            - столбец.
	 * @return - сортировка.
	 */
	public Sorting getSortingForColumn(final Column aCurColumn) {
		if (sortedColumns != null) {
			Iterator<Column> iterator = sortedColumns.iterator();
			while (iterator.hasNext()) {
				Column col = iterator.next();
				if (col.getId().equals(aCurColumn.getId())) {
					return col.getSorting();
				}
			}
		}
		return null;
	}

	public final Integer getPageSize() {
		return pageSize;
	}

	public final void setPageSize(final Integer aPageSize) {
		this.pageSize = aPageSize;
	}

	public final Collection<Column> getSortedColumns() {
		return sortedColumns;
	}

	public final Integer getPageNumber() {
		return pageNumber;
	}

	public final void setSortedColumns(final Collection<Column> aSortedColumns) {
		this.sortedColumns = aSortedColumns;
	}

	public final void setPageNumber(final Integer aPageNumber) {
		this.pageNumber = aPageNumber;
	}

	/**
	 * Функция нормализации настроек - т.е. приведения их в вид, необходимый для
	 * правильной работы шлюза и фабрики.
	 */
	public void normalize() {
		Collection<Column> source = getSortedColumns();
		SortedMap<Integer, Column> orderedByIndex = new TreeMap<Integer, Column>();
		Iterator<Column> iterator = source.iterator();
		while (iterator.hasNext()) {
			Column col = iterator.next();
			orderedByIndex.put(col.getIndex(), col);
		}
		setSortedColumns(orderedByIndex.values());
	}

	public final Boolean getApplyLocalFormatting() {
		return applyLocalFormatting;
	}

	public final void setApplyLocalFormatting(final Boolean aApplyFormatting) {
		applyLocalFormatting = aApplyFormatting;
	}

	public String getCurrentRecordId() {
		return currentRecordId;
	}

	public void setCurrentRecordId(final String aCurrentRecordId) {
		currentRecordId = aCurrentRecordId;
	}

	public String getCurrentColumnId() {
		return currentColumnId;
	}

	public void setCurrentColumnId(final String aCurrentColumnId) {
		currentColumnId = aCurrentColumnId;
	}

	public List<String> getSelectedRecordIds() {
		return selectedRecordIds;
	}

	public void setSelectedRecordIds(final List<String> aSelectedRecordIds) {
		selectedRecordIds = aSelectedRecordIds;
	}
}
