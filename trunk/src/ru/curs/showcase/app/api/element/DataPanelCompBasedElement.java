package ru.curs.showcase.app.api.element;


/**
 * Абстрактный класс элемента информационной панели, в основе которого лежит
 * некий компонент (сделанный по технологии GWT или JS) и имеющий Header и
 * Footer.
 * 
 * @author den
 * 
 */
public abstract class DataPanelCompBasedElement extends DataPanelElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8093132785939139397L;

	/**
	 * Заголовок перед элементом.
	 */
	private String header = "";

	/**
	 * Подпись под элементом.
	 */
	private String footer = "";

	public final String getFooter() {
		return footer;
	}

	public final void setFooter(final String aFooter) {
		footer = aFooter;
	}

	public final String getHeader() {
		return header;
	}

	public final void setHeader(final String aHeader) {
		header = aHeader;
	}
}
