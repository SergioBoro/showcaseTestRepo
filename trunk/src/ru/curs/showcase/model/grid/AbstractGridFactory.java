package ru.curs.showcase.model.grid;

import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;

/**
 * Базовый класс построителя грида. Содержит функции считывания динамических и
 * статических настроек грида, а также устанавливает настройки по умолчанию.
 * 
 * @author den
 * 
 */
public abstract class AbstractGridFactory extends CompBasedElementFactory {
	public static final String IMAGES_IN_GRID_DIR = "images.in.grid.dir";

	public static final String GRID_DEFAULT_PROFILE = "default.properties";

	/**
	 * Размер страницы с данными грида по умолчанию.
	 */
	public static final int DEF_PAGE_SIZE_VAL = 20;

	/**
	 * Номер страницы в гриде по умолчанию (нумерация с 1).
	 */
	private static final int DEF_PAGE_NUMBER = 1;

	protected static final String GRID_PROPERTIES_XSD = "gridProperties.xsd";

	protected static final String DEF_COL_HOR_ALIGN = "def.column.hor.align";
	protected static final String DEF_COL_VALUE_DISPLAY_MODE = "def.column.value.display.mode";
	protected static final String DEF_COL_WIDTH = "def.column.width";
	protected static final String DEF_VAL_FONT_COLOR = "def.value.font.color";
	protected static final String DEF_VAL_BG_COLOR = "def.value.bg.color";
	protected static final String DEF_VAL_FONT_SIZE = "def.value.font.size";
	protected static final String DEF_VAL_FONT_BOLD = "def.value.font.bold";
	protected static final String DEF_VAL_FONT_IT = "def.value.font.italic";
	protected static final String DEF_VAL_FONT_UL = "def.value.font.underline";
	protected static final String DEF_VAL_FONT_ST = "def.value.font.strikethrough";
	protected static final String DEF_STR_COL_HOR_ALIGN = "def.str.column.hor.align";
	protected static final String DEF_NUM_COL_HOR_ALIGN = "def.num.column.hor.align";
	protected static final String DEF_DATE_COL_HOR_ALIGN = "def.date.column.hor.align";
	protected static final String DEF_IMAGE_COL_HOR_ALIGN = "def.image.column.hor.align";
	protected static final String DEF_LINK_COL_HOR_ALIGN = "def.link.column.hor.align";
	protected static final String DEF_DATE_VALUES_FORMAT = "def.date.values.format";

	protected static final String RELATIVE_NUMBER_TOO_BIG_ERROR =
		"относительный autoSelectRecordId = %d выходит за пределы страницы (%d записей)";
	protected static final String XML_ERROR_MES = "настройки грида";
	protected static final String COL_SETTINGS_TAG = "col";

	protected static final String AUTO_SELECT_REC_TAG = "autoSelectRecordId";
	protected static final String AUTO_SELECT_COL_TAG = "autoSelectColumnId";
	protected static final String AUTO_SELECT_RELATIVE = "autoSelectRelativeRecord";
	protected static final String PRECISION_TAG = "precision";
	protected static final String PROFILE_TAG = "profile";

	/**
	 * Профайл грида.
	 */
	private GridProps gridProps = null;

	/**
	 * Идентификатор записи, которая должна быть выбрана автоматически после
	 * загрузки данных. По идентификатору можно определить autoSelectRecord.
	 */
	private Integer autoSelectRecordId = null;

	/**
	 * Идентификатор столбца, который должен быть выбран автоматически после
	 * загрузки данных. По идентификатору можно определить autoSelectColumn.
	 */
	private String autoSelectColumnId = null;

	/**
	 * Указание на то, как именно учитывать autoSelectRecordId - как
	 * относительный или абсолютный номер записи.
	 */
	private Boolean autoSelectRelativeRecord = true;

	/**
	 * Результат работы фабрики.
	 */
	private Grid result;

	/**
	 * Имя используемого профайла настроек грида.
	 */
	private String profile = GRID_DEFAULT_PROFILE;

	@Override
	public Grid getResult() {
		return result;
	}

	/**
	 * Запрашиваемые настройки грида.
	 */
	private GridRequestedSettings requestSettings;

	protected final void setRequestSettings(final GridRequestedSettings aRequestSettings) {
		requestSettings = aRequestSettings;
	}

	protected final GridRequestedSettings getRequestSettings() {
		return requestSettings;
	}

	@Override
	protected void correctSettingsAndData() {
		setupAutoSelecting();
		correctPageCount();
	}

	private void correctPageCount() {
		result.getUISettings().setPagesButtonCount(
				Math.min(result.getUISettings().getPagesButtonCount(), result.getDataSet()
						.getRecordSet().getPagesTotal()));

	}

	private void setupDefaultUISettings() {
		GridUIStyle style = new DefaultGridUIStyle();
		style.apply(gridProps, getResult().getUISettings());
	}

	@Override
	public Grid build() throws Exception {
		return (Grid) super.build();
	}

	public AbstractGridFactory(final ElementRawData aSource, final GridRequestedSettings aSettings) {
		super(aSource);
		requestSettings = aSettings;
	}

	/**
	 * Функция заполнения данных о столбцах.
	 * 
	 */
	protected abstract void fillColumns();

	/**
	 * Функция заполнения данных о записях.
	 * 
	 */
	protected abstract void fillRecordsAndEvents();

	protected boolean isRefresh() {
		return (requestSettings != null);
	}

	/**
	 * Настройка страничного отображения в гриде.
	 * 
	 */
	protected void initPages() {
		if (isRefresh()) {
			getResult().getDataSet().getRecordSet().setPageSize(requestSettings.getPageSize());
			getResult().getDataSet().getRecordSet().setPageNumber(requestSettings.getPageNumber());
		} else {
			getResult().getDataSet().getRecordSet().setPageSize(DEF_PAGE_SIZE_VAL);
			getResult().getDataSet().getRecordSet().setPageNumber(DEF_PAGE_NUMBER);
		}
	}

	@Override
	protected void initResult() {
		result = new Grid();
		initColumns();
		initRecords();
		initPages();
	}

	@Override
	protected void fillResultByData() {
		fillColumns();
		fillRecordsAndEvents();
	}

	private RecordSet initRecords() {
		RecordSet rs = new RecordSet();
		rs.setRecords(new ArrayList<Record>());
		result.getDataSet().setRecordSet(rs);
		return rs;
	}

	private ColumnSet initColumns() {
		ColumnSet cs = new ColumnSet();
		cs.setColumns(new ArrayList<Column>());
		result.getDataSet().setColumnSet(cs);
		return cs;
	}

	/**
	 * Считывает из файла настроек и устанавливает стандартные свойства записи
	 * грида.
	 * 
	 * @param curRecord
	 *            - запись.
	 */
	protected void setupStdRecordProps(final Record curRecord) {
		String value;
		Boolean boolValue;
		value = gridProps.getValueByNameForGrid(DEF_VAL_FONT_COLOR);
		if (value != null) {
			curRecord.setTextColor(value);
		}
		value = gridProps.getValueByNameForGrid(DEF_VAL_BG_COLOR);
		if (value != null) {
			curRecord.setBackgroundColor(value);
		}
		Integer intValue = gridProps.stdReadIntGridValue(DEF_VAL_FONT_SIZE);
		if (intValue != null) {
			curRecord.setFontSize(intValue.byteValue());
		}
		boolValue = gridProps.stdReadBoolGridValue(DEF_VAL_FONT_BOLD);
		if ((boolValue != null) && (boolValue)) {
			curRecord.addFontModifier(FontModifier.BOLD);
		}
		boolValue = gridProps.stdReadBoolGridValue(DEF_VAL_FONT_IT);
		if ((boolValue != null) && (boolValue)) {
			curRecord.addFontModifier(FontModifier.ITALIC);
		}
		boolValue = gridProps.stdReadBoolGridValue(DEF_VAL_FONT_UL);
		if ((boolValue != null) && (boolValue)) {
			curRecord.addFontModifier(FontModifier.UNDERLINE);
		}
		boolValue = gridProps.stdReadBoolGridValue(DEF_VAL_FONT_ST);
		if ((boolValue != null) && (boolValue)) {
			curRecord.addFontModifier(FontModifier.STRIKETHROUGH);
		}
	}

	/**
	 * Функция считывания и установки стандартных свойств столбца.
	 * 
	 * @param column
	 *            - столбец.
	 */
	protected void setupStdColumnProps(final Column column) {
		String val;
		column.setVisible(true);
		val = gridProps.getValueByNameForGrid(DEF_COL_VALUE_DISPLAY_MODE);
		if (val != null) {
			column.setDisplayMode(ColumnValueDisplayMode.valueOf(val));
		}

		if (column.getValueType().isString()) {
			val = gridProps.getValueByNameForGrid(DEF_STR_COL_HOR_ALIGN);
		} else if (column.getValueType().isNumber()) {
			val = gridProps.getValueByNameForGrid(DEF_NUM_COL_HOR_ALIGN);
		} else if (column.getValueType().isDate()) {
			val = gridProps.getValueByNameForGrid(DEF_DATE_COL_HOR_ALIGN);
		} else if (column.getValueType() == GridValueType.IMAGE) {
			val = gridProps.getValueByNameForGrid(DEF_IMAGE_COL_HOR_ALIGN);
		} else if (column.getValueType() == GridValueType.LINK) {
			val = gridProps.getValueByNameForGrid(DEF_LINK_COL_HOR_ALIGN);
		} else {
			val = gridProps.getValueByNameForGrid(DEF_COL_HOR_ALIGN);
		}
		if (val != null) {
			column.setHorizontalAlignment(HorizontalAlignment.valueOf(val));
		}
		val = gridProps.getValueByNameForGrid(DEF_COL_WIDTH);
		if ((val != null) && (column.getWidth() == null)) {
			column.setWidth(val);
		}
	}

	/**
	 * Класс считывателя настроек грида.
	 * 
	 * @author den
	 * 
	 */
	private class GridDynamicSettingsReader extends DefaultHandler {

		@Override
		public void startElement(final String namespaceURI, final String lname,
				final String qname, final Attributes attrs) {
			if (qname.equalsIgnoreCase(COL_SETTINGS_TAG)) {
				String colId = attrs.getValue(ID_TAG);
				Column col = getResult().getColumnById(colId);
				if (col == null) {
					col = createColumn(colId);
					getResult().getDataSet().getColumnSet().getColumns().add(col);
				}
				if (!isRefresh()) {
					if (attrs.getIndex(WIDTH_TAG) > -1) {
						String width = attrs.getValue(WIDTH_TAG);
						col.setWidth(width);
					}
				}
				if (attrs.getIndex(PRECISION_TAG) > -1) {
					String value = attrs.getValue(PRECISION_TAG);
					col.setFormat(value);
				}
				if (attrs.getIndex(TYPE_TAG) > -1) {
					String value = attrs.getValue(TYPE_TAG);
					col.setValueType(GridValueType.valueOf(value));
				}
				return;
			}
			if (qname.equalsIgnoreCase(PROPS_TAG)) {
				String value;
				if (attrs.getIndex(AUTO_SELECT_RELATIVE) > -1) {
					value = attrs.getValue(AUTO_SELECT_RELATIVE);
					autoSelectRelativeRecord = Boolean.valueOf(value);
				}
				if (attrs.getIndex(AUTO_SELECT_REC_TAG) > -1) {
					value = attrs.getValue(AUTO_SELECT_REC_TAG);
					autoSelectRecordId = Integer.parseInt(value) - 1; // "естественная нумерация с 1"
				}
				if (attrs.getIndex(AUTO_SELECT_COL_TAG) > -1) {
					autoSelectColumnId = attrs.getValue(AUTO_SELECT_COL_TAG);
				}
				if (attrs.getIndex(PROFILE_TAG) > -1) {
					profile = attrs.getValue(PROFILE_TAG);
				}
				if ((!isRefresh()) && (attrs.getIndex(PAGESIZE_TAG) > -1)) {
					value = attrs.getValue(PAGESIZE_TAG);
					Integer intValue = Integer.valueOf(value);
					getResult().getDataSet().getRecordSet().setPageSize(intValue);
				}
				return;
			}
		}

		@Override
		public void endElement(final String namespaceURI, final String lname, final String qname) {
		}

		@Override
		public void characters(final char[] arg0, final int arg1, final int arg2) {
		}

	}

	@Override
	protected void setupDynamicSettings() {
		super.setupDynamicSettings();
		gridProps = new GridProps(profile);
		setupDefaultUISettings();
		correctAutoSelectRecordId();
	}

	private void correctAutoSelectRecordId() {
		RecordSet rs = getResult().getDataSet().getRecordSet();

		if ((autoSelectRecordId != null) && autoSelectRelativeRecord) {
			if (autoSelectRecordId >= rs.getPageSize()) {
				throw new InconsistentSettingsFromDBException(String.format(
						RELATIVE_NUMBER_TOO_BIG_ERROR, autoSelectRecordId, rs.getPageSize()));
			}
			autoSelectRecordId = autoSelectRecordId + rs.getPageSize() * (rs.getPageNumber() - 1);
		} else {
			if (!isRefresh() && (autoSelectRecordId != null)) {
				float autoSelectRecordNumber = autoSelectRecordId;
				if (autoSelectRecordNumber >= rs.getPageSize()) {
					rs.setPageNumber((int) Math.ceil((autoSelectRecordNumber + 1)
							/ rs.getPageSize()));
				}
			}
		}
	}

	/**
	 * Настройка автоматического выделения у грида на основе считанных из БД
	 * данных.
	 */
	protected void setupAutoSelecting() {
		if (autoSelectRecordId == null) {
			return;
		}
		Iterator<Record> riterator =
			getResult().getDataSet().getRecordSet().getRecords().iterator();
		while (riterator.hasNext()) {
			Record current = riterator.next();
			if (current.getId().equals(autoSelectRecordId.toString())) {
				getResult().setAutoSelectRecord(current);
			}
		}

		if (autoSelectColumnId == null) {
			return;
		}
		Iterator<Column> citerator =
			getResult().getDataSet().getColumnSet().getColumns().iterator();
		while (citerator.hasNext()) {
			Column current = citerator.next();
			if (current.getId().equals(autoSelectColumnId)) {
				getResult().setAutoSelectColumn(current);
			}
		}
	}

	@Override
	protected DefaultHandler getConcreteHandler() {
		return new GridDynamicSettingsReader();
	}

	/**
	 * Стандартная функция создания столбца.
	 * 
	 * @param colId
	 *            - идентификатор столбца.
	 * @return - столбец.
	 */
	protected Column createColumn(final String colId) {
		Column res = new Column();
		res.setId(colId);
		res.setCaption(colId);
		res.setMinWidthPx(0);
		return res;
	}

	@Override
	protected String getSettingsErrorMes() {
		return XML_ERROR_MES;
	}

	protected GridProps getGridProps() {
		return gridProps;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(final String aProfile) {
		profile = aProfile;
	}

}