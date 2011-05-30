package ru.curs.showcase.app.api.geomap;

import ru.curs.showcase.app.api.*;

/**
 * 
 * Абстрактный класс объекта на карте. Является общим как для географических
 * объектов, так и для числовых показателей.
 * 
 * @author den
 * 
 */
public abstract class GeoMapObject extends VisualElement implements SerializableElement {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7054114684387944282L;

	/**
	 * Текстовая подпись для объекта.
	 */
	private String tooltip;

	/**
	 * Стиль объекта на карте. Для полигона и показателя - цвет (задается в
	 * формате #FFFFFF). Для точки - вид ее значка (список возможных стилей
	 * определяется шаблоном карты). В будущем возможно введение объекта Style.
	 */
	private String style;

	public final String getStyle() {
		return style;
	}

	public final void setStyle(final String aStyle) {
		style = aStyle;
	}

	public final String getTooltip() {
		return tooltip;
	}

	public final void setTooltip(final String aTooltip) {
		tooltip = aTooltip;
	}

	public GeoMapObject() {
		super();
	}

	public GeoMapObject(final String aId, final String aName) {
		super(aId, aName);
	}
}
