/**
 * 
 */
package ru.curs.showcase.app.client.utils;

/**
 * @author anlug
 * 
 */
public final class AccessToDomModel {

	private AccessToDomModel() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Инициализирует динамический CSS.
	 * 
	 * @param cssdef
	 *            - текст CSS.
	 */
	public static native void addCSS(final String cssdef) /*-{
		var ss1 = $doc.createElement('style');
		ss1.setAttribute('type', 'text/css');
		ss1.setAttribute('id', 'dynastyle');
		if (ss1.styleSheet) { // IE
		ss1.styleSheet.cssText = cssdef;
		} else { // the world
		var tt1 = $doc.createTextNode(cssdef);
		ss1.appendChild(tt1);
		}
		var hh1 = $doc.getElementsByTagName('head')[0];
		hh1.appendChild(ss1);
	}-*/;

	/**
	 * Динамически вставляет в страницу блок javascript-кода.
	 * 
	 * @param code
	 *            Javacript-код, который необходимо вставить
	 */
	public static native void addScript(final String code) /*-{
		var newscript = $doc.createElement('script');
		newscript.text = code;
		newscript.type = "text/javascript";
		var div = $doc.getElementById('target');
		div.appendChild(newscript);
	}-*/;

	/**
	 * @return - возвращает текущий ContextPath для данной открытой страницы
	 *         (например, "/showcase")
	 */
	public static native String getAppContextPath() /*-{
		return $wnd.appContextPath;
	}-*/;

	/**
	 * Динамически добавляет ссылку на CSS в DOM.
	 * 
	 * @param link
	 *            - адрес ссылки.
	 */
	public static native void addCSSLink(final String link) /*-{
		var objCSS = $doc.createElement('link');
		objCSS.rel = 'stylesheet';
		objCSS.href = link;
		objCSS.type = 'text/css';
		var hh1 = $doc.getElementsByTagName('head')[0];
		hh1.appendChild(objCSS);
	}-*/;

}
