package ru.curs.showcase.app.api.navigator;

import java.util.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Класс навигатора. Навигатор служит для перехода между различными вкладками и
 * информационных панелей. Типичная реализация навигатора - компонент StackPanel
 * или Accordion.
 * 
 * @author den
 * 
 */
public class Navigator implements SerializableElement {
	/**
	 * Ширина навигатора по умолчанию.
	 */
	private static final String DEF_NAV_WIDTH = "300px";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 582764293154975973L;
	/**
	 * Список групп в навигаторе.
	 */
	private List<NavigatorGroup> groups = new ArrayList<NavigatorGroup>();

	/**
	 * Признак того, что навигатор должен быть скрыт при загрузке главной
	 * страницы. При этом навигатор должен хранится в памяти на клиентской
	 * стороне для того, чтобы работали action, связанные с ним, а также на
	 * случай, если в будущем появится действие "Показать навигатор".
	 */
	private Boolean hideOnLoad = false;

	/**
	 * Ширина навигатора. Настраиваем здесь потому что установка через CSS не
	 * корректно работает - навигатор лежит на SplitPanel, которая сама создает
	 * панели и устанавливает их ширины. Отказ от SplitPanel - тоже не хорошо.
	 * Значение по умолчанию - DEF_NAV_WIDTH. Может задаваться как в процентах,
	 * так и в пикселях. В первом случае значение должно содержать префикс «px»,
	 * во втором - префикс »%».
	 */
	private String width = DEF_NAV_WIDTH;

	/**
	 * Автоматически выделяемый при загрузке в навигаторе элемент.
	 */
	private NavigatorElement autoSelectElement = null;

	public final List<NavigatorGroup> getGroups() {
		return groups;
	}

	public final void setGroups(final List<NavigatorGroup> aGroups) {
		this.groups = aGroups;
	}

	public NavigatorElement getAutoSelectElement() {
		return autoSelectElement;
	}

	public void setAutoSelectElement(final NavigatorElement aAutoSelectElement) {
		autoSelectElement = aAutoSelectElement;
	}

	/**
	 * Возвращает группу по id.
	 * 
	 * @param id
	 *            - id.
	 * @return - группа.
	 */
	public NavigatorGroup getGroupById(final String id) {
		if (id == null) {
			return null;
		}
		Iterator<NavigatorGroup> iterator = groups.iterator();
		while (iterator.hasNext()) {
			NavigatorGroup current = iterator.next();
			if (id.equals(current.getId())) {
				return current;
			}
		}
		return null;
	}

	public Boolean getHideOnLoad() {
		return hideOnLoad;
	}

	public void setHideOnLoad(final Boolean aHideOnLoad) {
		hideOnLoad = aHideOnLoad;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(final String aWidth) {
		width = aWidth;
	}
}
