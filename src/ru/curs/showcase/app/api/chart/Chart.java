package ru.curs.showcase.app.api.chart;

import ru.curs.showcase.app.api.element.*;

/**
 * Данные для построения графика.
 * 
 * @author den
 * 
 */
public class Chart extends DataPanelElementWithLegend {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8945028162073890091L;

	/**
	 * Динамические данные для графика, на основе которых строится JSON объект
	 * для передачи в JS функцию построения графика.
	 */
	private ChartData javaDynamicData;

	@Override
	public final ChartData getJavaDynamicData() {
		return javaDynamicData;
	}

	public final void setJavaDynamicData(final ChartData aData) {
		javaDynamicData = aData;
	}

	@Override
	public final ChartEventManager getEventManager() {
		return (ChartEventManager) super.getEventManager();
	}

	@Override
	protected EventManager initEventManager() {
		return new ChartEventManager();
	}

	@Override
	public void resetJavaDynamicData() {
		javaDynamicData = null;
	}
}
