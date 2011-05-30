/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.beta2.extra.gwt.ui.panels.*;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.client.api.Constants;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Класс генерации пользовательского интерфейса средней (главной,
 *         рабочей панели приложения - MainPanel) части приложения Showcase.
 * 
 */
public class MainPanel {

	/**
	 * Базовая вертикальная панель, содержащая все виджеты MainPanel.
	 */
	private final VerticalPanel basicVerticalPanel = new VerticalPanel();

	/**
	 * CursSplitLayoutPanel.
	 */
	private final CursSplitLayoutPanel p = new CursSplitLayoutPanel();

	/**
	 * Виджет, который содержит GeneralDataPanel.
	 */
	private Widget gp;

	/**
	 * Виджет который содержит навигатор.
	 */
	private Widget accordeon;

	/**
	 * Переменная, которая определяет на какую ширину от ширины экрана(от ширины
	 * рабочей части окна браузера) нужно уменьшить MainPanel.
	 */
	private final int n35 = 35;

	/**
	 * Процедура создания MainPanel, которая включает в себя Accordeon и
	 * GeneralDataPanel.
	 * 
	 * @return возвращает заполненный виджет MainPanel типа VerticalPanel.
	 */
	public Widget startMainPanelCreation() {

		basicVerticalPanel.add(new DownloadHelper());

		final int n85 = 85;
		p.setPixelSize(Window.getClientWidth() - n35, Window.getClientHeight() - n85);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				int height = event.getHeight() - n85;
				int width = event.getWidth() - n35;
				p.setHeight(height + "px");
				p.setWidth(width + "px");
			}
		});

		DecoratorPanel decoratorPanel = new DecoratorPanel();
		decoratorPanel.setWidget(p);
		basicVerticalPanel.add(decoratorPanel);

		basicVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		basicVerticalPanel.setSpacing(Constants.SPACINGN);

		accordeon = (new Accordeon()).generateAccordeon();

		return basicVerticalPanel;
	}

	/**
	 * 
	 * Процедура которая продолжает создание главной панели MainPanel (добавляет
	 * на нее Accordeon и GeneralDataPanel) на основе настроек пришедших из
	 * хранимой процедуры через асинхронный gwt-servlet запрос.
	 * 
	 * @param showNavigator
	 *            - показывать ли навигатор в приложении или скрывать его.
	 * @param navigatorWidth
	 *            - переменная которая содержит значение шириныв навигатора в
	 *            пикселях или процентах (напр. "500px" или "30%").
	 */
	public void generateMainPanel(final boolean showNavigator, final String navigatorWidth) {

		if (showNavigator) {

			int widthNumber = 0;
			try {
				widthNumber = NavigatorWidthParser.getWidth(navigatorWidth);
			} catch (Exception e) {

				MessageBox.showMessageWithDetails(Constants.TRANSFORMATION_NAVIGATOR_WIDTH_ERROR,
						e.getClass().getName() + ": " + e.getMessage(),
						GeneralServerException.getStackText(e));
			}

			switch (NavigatorWidthParser.getWidthType(navigatorWidth)) {

			case PIXELS:

				p.addWest(accordeon, widthNumber);
				break;

			case PERCENTS:
				final int percentsTotal = 100;
				final int absoluteWidth =
					widthNumber * (Window.getClientWidth() - n35) / percentsTotal;
				p.addWest(accordeon, absoluteWidth);
				break;

			default:

				p.addWest(accordeon, Constants.SPLITLAYOUTPANELSIZEN);
				break;

			}

		}

		gp = (new GeneralDataPanel()).generateDataPanel();

		p.setSplitterDragHandler(new SplitterDragHandler() {

			@Override
			public void splitterDragEvent() {
				// GeneralDataPanel.getTabPanel().saveTabBarCurrentScrollingPosition();
				GeneralDataPanel.getTabPanel().checkIfScrollButtonsNecessary();

			}

		});

		p.add(gp);

		p.setWidgetMinSize(gp, 1);

	}
}
