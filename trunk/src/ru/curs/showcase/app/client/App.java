package ru.curs.showcase.app.client;

import ru.curs.showcase.app.client.utils.AccessToDomModel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Класс точки входа в ПЦЕ часть приложения. Используется функция
 * <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {

	/**
	 * Метод точки входа в приложение Showcase.
	 */
	@Override
	public void onModuleLoad() {
		addUserDataCSS();
		FeedbackJSNI.initFeedbackJSNIFunctions();
		// AppCurrContext.appCurrContext = AppCurrContext.getInstance();
		AppCurrContext.getInstance();

		// генерация и размещение заглавной части (шапки) приложения Showcase
		Header head = new Header();
		RootPanel.get("showcaseHeaderContainer").add(head.generateHeader());

		// генерация и размещение нижней части (колонтитул) приложения Showcase
		Bottom bottom = new Bottom();
		RootPanel.get("showcaseBottomContainer").add(bottom.generateBottom());

		// генерация и размещение главной части (главной) приложения Showcase
		MainPanel mainPanel = new MainPanel();
		AppCurrContext.getInstance().setMainPanel(mainPanel);
		RootPanel.get("showcaseAppContainer").add(mainPanel.startMainPanelCreation());

	}

	private void addUserDataCSS() {
		AccessToDomModel.addCSSLink("css/solution.css");
		AccessToDomModel.addCSSLink("css/solutionGrid.css");
	}

}
