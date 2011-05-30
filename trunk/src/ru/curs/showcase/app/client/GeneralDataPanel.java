/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.*;

import ru.beta2.extra.gwt.ui.panels.CursScrolledTabLayoutPanel;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.client.api.*;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Генерация (создание) панели данных приложения Showcase (панель
 *         закладок TabPanel).
 * 
 */

public class GeneralDataPanel {
	/**
	 * HandlerRegistration.
	 */
	private static HandlerRegistration testRegTabPanelSelectionHandler;
	/**
	 * Панель вкладок (закладок) - GeneralDataPanel.
	 */
	private static CursScrolledTabLayoutPanel tabPanel = new CursScrolledTabLayoutPanel(2.0,
			Unit.EM);

	/**
	 * Процедура создания GeneralDataPanel, которая включает в себя вкладки с
	 * данными.
	 * 
	 * @return возвращает виджет GeneralDataPanel типа SimplePanel.
	 */
	public Widget generateDataPanel() {

		final SimplePanel basicDataVerticalPanel = new SimplePanel();

		basicDataVerticalPanel.setSize("100%", "100%");
		getTabPanel().setSize("100%", "100%");
		basicDataVerticalPanel.add(getTabPanel());

		// tabPanel.setSize("400px", "400px");

		showWelcomPage();
		// generateTestPanels();
		getTabPanel().selectTab(0);

		return basicDataVerticalPanel;
	}

	private void showWelcomPage() {
		final SimplePanel tabVerticalPanel = new SimplePanel();
		tabVerticalPanel.setSize("100%", "100%");
		HTML ht = new HTML();
		// ht = new HTML();
		ht.setHTML("<iframe width='100%' height='100%' style='border:0px;' src='html/welcome.jsp'/>");
		ht.setSize("100%", "100%");
		tabVerticalPanel.add(ht);
		getTabPanel().add(tabVerticalPanel, Constants.WELCOM_TAB_CAPTION);

	}

	/**
	 * 
	 * Процедура перерисовки вкладок в DataPanel при выборе item-са в аккардеоне
	 * (дереве, навигаторе).
	 * 
	 * @param dp
	 *            - панель вкладок DataPanel
	 * 
	 */
	public static void redrowGeneralDataPanelAtnavigatorClick(final DataPanel dp) {
		if (testRegTabPanelSelectionHandler != null) {
			testRegTabPanelSelectionHandler.removeHandler();
			testRegTabPanelSelectionHandler = null;
		}
		if (AppCurrContext.getInstance().getRegTabPanelSelectionHandler() != null) {
			AppCurrContext.getInstance().getRegTabPanelSelectionHandler().removeHandler();
			AppCurrContext.getInstance().setRegTabPanelSelectionHandler(null);
		}

		if (getTabPanel().getWidgetCount() > 0) {
			getTabPanel().clear();
		}

		AppCurrContext.getInstance().setDataPanelMetaData(dp);

		Collection<DataPanelTab> dptCollection = dp.getTabs();
		Iterator<DataPanelTab> itr = dptCollection.iterator();

		AppCurrContext.getInstance().getUiDataPanel().clear();
		while (itr.hasNext()) {
			DataPanelTab dpt = itr.next();
			Widget w = generateTab(dpt);
			AppCurrContext.getInstance().getUiDataPanel().add(new UIDataPanelTab(dpt, w));

		}

		Widget tempWidget = new HTML();
		getTabPanel().add(tempWidget);
		getTabPanel().selectTab(tempWidget);

		AppCurrContext.getInstance().setRegTabPanelSelectionHandler(
				getTabPanel().addSelectionHandler(new TabPanelSelectionHandler()));
		selectTab(dp.getActiveTabForAction(AppCurrContext.getInstance().getNavigatorAction()));
		getTabPanel().remove(tempWidget);
	}

	/**
	 * 
	 * Выделяет (открывает) закладку в UI.
	 * 
	 * @param actDpt
	 *            - закладка DataTabPanel для открытия по умолчанию.
	 */
	public static void selectTab(final DataPanelTab actDpt) {
		for (int i = 0; i < getTabPanel().getWidgetCount(); i++) {
			if (actDpt.getId().equals(
					(AppCurrContext.getInstance().getUiDataPanel().get(i)
							.getDataPanelTabMetaData().getId()))) {
				getTabPanel().selectTab(i, true);
				break;
			}
		}
	}

	/**
	 * 
	 * Генерирует с нуля.
	 * 
	 * @param dpt
	 *            - DataTabPanel - класс вкладки
	 * 
	 * @return - возвращает виджет вкладки в GeneralDataPanel
	 */
	public static Widget generateTab(final DataPanelTab dpt) {

		SimplePanel vp = new SimplePanel();
		getTabPanel().add(vp, dpt.getName());
		return vp;
	}

	/**
	 * Заполняет вкладку соответствующим контентом из виджетов.
	 * 
	 * @param tabIndex
	 *            - индекс (порядковый номер с нуля) вкладки.
	 */
	public static void fillTabContent(final int tabIndex) {

		XFormPanel.destroyXForms(); // Важно !!!!

		// очистка текущей вкладки полностью
		SimplePanel vp = ((SimplePanel) getTabPanel().getWidget(tabIndex));
		vp.clear();

		ScrollPanel sp = new ScrollPanel();

		vp.add(sp);
		sp.setSize("100%", "100%");

		VerticalPanel vp1 = new VerticalPanel();
		vp1.setSpacing(2);
		vp1.setSize("100%", "100%");
		sp.add(vp1);

		AppCurrContext.getInstance().getUiDataPanel().get(tabIndex).getUiElements().clear();

		DataPanelTab dpt =
			AppCurrContext.getInstance().getUiDataPanel().get(tabIndex).getDataPanelTabMetaData();
		Collection<DataPanelElementInfo> tabscoll = dpt.getElements();
		Iterator<DataPanelElementInfo> itr = tabscoll.iterator();
		while (itr.hasNext()) {
			Widget el = null;
			DataPanelElementInfo dpe = itr.next();
			switch (dpe.getType()) {
			case WEBTEXT:
				el = generateWebTextElement(dpe);
				break;
			case XFORMS:
				el = generateXFormsElement(dpe);
				break;
			case GRID:
				el = generateGridElement(dpe);
				break;
			case CHART:
				el = generateChartElement(dpe);
				break;
			case GEOMAP:
				el = generateMapElement(dpe);
				break;
			default:
				break;
			}
			if (el != null) {
				el.addStyleName("dataPanelElement-BorderCorners");

				if (dpe.getStyleClass() != null) {
					el.addStyleName(dpe.getStyleClass());
				}

				el.setWidth("100%");
				DOM.setElementAttribute(el.getElement(), "id", dpe.getFullId());

				if (!(dpe.getNeverShowInPanel())) {
					vp1.add(el);
				}
			}
		}
	}

	private static Widget generateChartElement(final DataPanelElementInfo dpe) {

		ChartPanel chp = null;
		Widget w = null;
		if ((!(dpe.getHideOnLoad())) && (!(dpe.getNeverShowInPanel()))) {

			chp = new ChartPanel(getElementContextForNavigatorAction(dpe), dpe);

			w = chp.getPanel();
		} else {
			// в случае когда у данного элемента есть главный элемент
			chp = new ChartPanel(dpe);
			w = chp.getPanel();
			chp.hidePanel();

		}

		getUiElements(dpe).add(new UIDataPanelElement(chp));
		return w;

	}

	/**
	 * 
	 * создает и возвращает виджет Verticalpanel для элементов типа Grid (на
	 * виджете VerticalPanel лежит компонента Grid).
	 * 
	 * @param dpe
	 *            - DataPanelElement
	 * 
	 * @return - Widget
	 */
	public static Widget generateGridElement(final DataPanelElementInfo dpe) {

		DataGridPanel dgp = null;
		Widget w = null;

		if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {
			dgp =
				new DataGridPanel(getElementContextForNavigatorAction(dpe), dpe,
						new DataGridPanelCallbacksEvents());
			w = dgp.getPanel();
			w.setSize("100%", "100%");
		} else {
			dgp = new DataGridPanel(dpe, new DataGridPanelCallbacksEvents());
			w = dgp.getPanel();
			dgp.hidePanel();
		}

		getUiElements(dpe).add(new UIDataPanelElement(dgp));

		return w;
	}

	/**
	 * 
	 * создает и возвращает виджет HTML для элементов типа WebText.
	 * 
	 * @param dpe
	 *            - DataPanelElement
	 * 
	 * @return - Widget
	 */
	public static Widget generateWebTextElement(final DataPanelElementInfo dpe) {

		WebTextPanel wtp = null;
		Widget w = null;
		if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {

			wtp = new WebTextPanel(getElementContextForNavigatorAction(dpe), dpe);
			w = wtp.getPanel();
			w.setSize("100%", "100%");
		} else {
			wtp = new WebTextPanel(dpe);
			w = wtp.getPanel();
			wtp.hidePanel();
		}

		getUiElements(dpe).add(new UIDataPanelElement(wtp));

		return w;
	}

	/**
	 * 
	 * создает и возвращает виджет для элементов типа XForm.
	 * 
	 * @param dpe
	 *            - DataPanelElement
	 * 
	 * @return - Widget
	 */
	public static Widget generateXFormsElement(final DataPanelElementInfo dpe) {

		XFormPanel wtp = null;
		Widget w = null;
		if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {

			wtp = new XFormPanel(getElementContextForNavigatorAction(dpe), dpe);
			w = wtp.getPanel();
			w.setSize("100%", "100%");
		} else {
			wtp = new XFormPanel(dpe);
			w = wtp.getPanel();
			wtp.hidePanel();
		}

		getUiElements(dpe).add(new UIDataPanelElement(wtp));

		return w;
	}

	/**
	 * 
	 * создает и возвращает виджет для элемента типа Map.
	 * 
	 * @param dpe
	 *            - DataPanelElement
	 * 
	 * @return - Widget
	 */
	public static Widget generateMapElement(final DataPanelElementInfo dpe) {

		MapPanel mp = null;
		Widget w = null;
		if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {

			mp = new MapPanel(getElementContextForNavigatorAction(dpe), dpe);

			w = mp.getPanel();
		} else {
			// в случае когда у данного элемента есть главный элемент
			mp = new MapPanel(dpe);
			w = mp.getPanel();
			mp.hidePanel();

		}

		getUiElements(dpe).add(new UIDataPanelElement(mp));
		return w;

	}

	private static List<UIDataPanelElement> getUiElements(final DataPanelElementInfo dpe) {
		return AppCurrContext.getInstance().getUiDataPanel().get(dpe.getTab().getPosition())
				.getUiElements();
	}

	private static CompositeContext getElementContextForNavigatorAction(
			final DataPanelElementInfo dpe) {
		return dpe.getContext(AppCurrContext.getInstance().getNavigatorAction());
	}

	/**
	 * @param atabPanel
	 *            the tabPanel to set
	 */
	public static void setTabPanel(final CursScrolledTabLayoutPanel atabPanel) {
		GeneralDataPanel.tabPanel = atabPanel;
	}

	/**
	 * @return the tabPanel
	 */
	public static CursScrolledTabLayoutPanel getTabPanel() {
		return tabPanel;
	}

}
