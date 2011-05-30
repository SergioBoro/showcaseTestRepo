package ru.curs.showcase.model.chart;

import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.app.api.element.LegendPosition;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.TextUtils;

import com.google.gson.Gson;

/**
 * Шаблонный класс построителя графика. Содержит некоторые шаблонные методы,
 * используемые для создания графика, а также функции установки настроек по
 * умолчанию.
 * 
 * @author den
 * 
 */
public abstract class AbstractChartFactory extends CompBasedElementFactory {
	/**
	 * Константа из шаблона для типа круговой диаграммы.
	 */
	private static final String PIE_CHART = "Pie";

	static final String MAX_X_TAG = "maxX";

	static final String SERIES_NAME_TAG = "seriesName";

	static final String LABEL_X_TAG = "labelx";

	protected static final String CHART_SETTINGS_ERROR_MES = "настройки графика";

	protected static final String SELECTOR_TAG = "selectorColumn";

	protected static final String LABEL_Y_TAG = "labely";

	protected static final String LABEL_Y_TEXT = "text";

	protected static final String CHART_PROPERTIES_XSD = "chartProperties.xsd";

	protected static final String X_TAG = "x";
	/**
	 * Название столбца, в котором содержатся подписи по X или названия серий.
	 */
	private String selectorColumn;

	/**
	 * Признак того, что набор с данными нужно транспонировать при считывании.
	 */
	private boolean flip;

	protected final String getSelectorColumn() {
		return selectorColumn;
	}

	protected final boolean isFlip() {
		return flip;
	}

	/**
	 * Конструируемый график.
	 */
	private Chart result;

	/**
	 * Общий для всех подсказок на графике шаблон форматирования.
	 */
	private String hintFormat;

	public final String getHintFormat() {
		return hintFormat;
	}

	@Override
	public Chart getResult() {
		return result;
	}

	@Override
	public Chart build() throws Exception {
		return (Chart) super.build();
	}

	public AbstractChartFactory(final ElementRawData aSource) {
		super(aSource);
	}

	@Override
	protected void initResult() {
		result = new Chart();
		result.setJavaDynamicData(new ChartData());
	}

	@Override
	protected void fillResultByData() {
		fillLabelsX();
		fillSeries();
	}

	/**
	 * Функция заполнения массива подписей по оси X.
	 * 
	 */
	protected abstract void fillLabelsX();

	/**
	 * Функция заполнения данными серий.
	 * 
	 */
	protected abstract void fillSeries();

	/**
	 * Класс считывателя настроек графика.
	 * 
	 * @author den
	 * 
	 */
	private class ChartDynamicSettingsReader extends DefaultHandler {
		/**
		 * Признак чтения шаблона.
		 */
		private boolean readingTemplate = false;

		@Override
		public void startElement(final String namespaceURI, final String lname,
				final String qname, final Attributes attrs) {
			String value;
			Integer intValue = null;
			if (qname.equalsIgnoreCase(TEMPLATE_TAG)) {
				readingTemplate = true;
				getResult().setTemplate("");
				return;
			}
			if (qname.equalsIgnoreCase(PROPS_TAG)) {
				value = attrs.getValue(LEGEND_TAG);
				value = value.toUpperCase().trim();
				getResult().setLegendPosition(LegendPosition.valueOf(value));
				value = attrs.getValue(WIDTH_TAG);
				intValue = TextUtils.getIntSizeValue(value);
				getResult().getJavaDynamicData().setWidth(intValue);
				value = attrs.getValue(HEIGHT_TAG);
				intValue = TextUtils.getIntSizeValue(value);
				getResult().getJavaDynamicData().setHeight(intValue);
				value = attrs.getValue(SELECTOR_TAG);
				selectorColumn = value;
				value = attrs.getValue(FLIP_TAG);
				flip = Boolean.valueOf(value);
				hintFormat = attrs.getValue(HINT_FORMAT_TAG);
				return;
			}
			if (qname.equalsIgnoreCase(LABEL_Y_TAG)) {
				ChartLabel currentLabel = new ChartLabel();
				getResult().getJavaDynamicData().getLabelsY().add(currentLabel);
				value = attrs.getValue(VALUE_TAG);
				currentLabel.setValue(Double.parseDouble(value));
				value = attrs.getValue(LABEL_Y_TEXT);
				currentLabel.setText(value);
			}
		}

		@Override
		public void endElement(final String namespaceURI, final String lname, final String qname) {
			if (qname.equalsIgnoreCase(TEMPLATE_TAG)) {
				readingTemplate = false;
				return;
			}
		}

		@Override
		public void characters(final char[] arg0, final int arg1, final int arg2) {
			if (readingTemplate) {
				getResult().setTemplate(
						getResult().getTemplate() + String.copyValueOf(arg0, arg1, arg2));
				return;
			}
		}
	}

	@Override
	protected void setupDynamicSettings() {
		super.setupDynamicSettings();
		correctFlipMode();
	}

	private void correctFlipMode() {
		// Для Pie графика удобнее
		// задавать данные в виде нескольких сессий, в каждой из которых -
		// одно
		// значение. Но увы - используемая нами компонента не понимает
		// данные в
		// таком формате, так что применяем обратное транспонирование.
		boolean realFlip = flip;
		Gson gson = new Gson();
		FakeChartTemplate template =
			gson.fromJson(getResult().getTemplate(), FakeChartTemplate.class);
		if ((template.getPlot() != null)
				&& (PIE_CHART.equalsIgnoreCase(template.getPlot().getType()))) {
			realFlip = !realFlip;
		}
		flip = realFlip;
	}

	@Override
	protected DefaultHandler getConcreteHandler() {
		return new ChartDynamicSettingsReader();
	}

	@Override
	protected String getSettingsErrorMes() {
		return CHART_SETTINGS_ERROR_MES;
	}

	@Override
	protected void correctSettingsAndData() {
		setupTooltips();
		setupBarLabels();
	}

	private void setupTooltips() {
		if (hintFormat == null) {
			return;
		}
		Iterator<ChartSeries> siterator = result.getJavaDynamicData().getSeries().iterator();
		while (siterator.hasNext()) {
			ChartSeries series = siterator.next();
			Iterator<ChartSeriesValue> viterator = series.getData().iterator();
			int x = 1;
			while (viterator.hasNext()) {
				ChartSeriesValue value = viterator.next();
				String toolTip = hintFormat;
				ChartLabel label = result.getJavaDynamicData().getLabelsX().get(x);
				if (label != null) {
					toolTip = TextUtils.replaceCI(toolTip, "%" + LABEL_X_TAG, label.getText());
				}
				if (value.getY() != null) {
					label = result.getJavaDynamicData().getLabelsYByValue(value.getY());
					if (label != null) {
						toolTip = TextUtils.replaceCI(toolTip, "%" + LABEL_Y_TAG, label.getText());
					}
					toolTip =
						TextUtils.replaceCI(toolTip, "%" + VALUE_TAG, value.getY().toString());
				}
				toolTip = TextUtils.replaceCI(toolTip, "%" + X_TAG, String.valueOf(x));
				toolTip = TextUtils.replaceCI(toolTip, "%" + SERIES_NAME_TAG, series.getName());
				toolTip =
					TextUtils.replaceCI(toolTip, "%" + MAX_X_TAG,
							String.valueOf(series.getData().size()));
				value.setTooltip(toolTip);
				x++;
			}
		}
	}

	private void setupBarLabels() {
		Iterator<ChartSeries> siterator = result.getJavaDynamicData().getSeries().iterator();
		while (siterator.hasNext()) {
			Iterator<ChartSeriesValue> viterator = siterator.next().getData().iterator();
			int x = 1;
			while (viterator.hasNext()) {
				ChartSeriesValue value = viterator.next();
				ChartLabel label = result.getJavaDynamicData().getLabelsX().get(x);
				if (label != null) {
					value.setLegend(label.getText());
				}
				if (value.getY() != null) {
					label = result.getJavaDynamicData().getLabelsYByValue(value.getY());
					if (label != null) {
						if (value.getLegend() == null) {
							value.setLegend(label.getText());
						}
					}
				}
				x++;
			}
		}
	}
}
