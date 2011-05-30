/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.showcase.app.api.navigator.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Класс генерации пользовательского интерфейса аккордеона (дерева) в
 *         главной части (в MainPanel) приложения Showcase.
 * 
 */
public class Accordeon {

	/**
	 * Коллекция объектов аккордеона, которая связывает UI элементы (группы и
	 * элементы дерева) c метаданными.
	 */
	private static List<Tree> uiListOfAccordeonTrees = new ArrayList<Tree>();

	/**
	 * TreeItem в дереве, который был выделен последний и по щелчку на который
	 * задано действие Action.
	 */
	private static TreeItem lastSelectedItem = null;

	/**
	 * Переменная хранящая ссылку на виджет accordeon, типа DecoratedStackPanel.
	 */
	private StackLayoutPanel accordeon = new StackLayoutPanel(Unit.EM);

	/**
	 * переменная accordeonData содержит точную копию структуры Navigator
	 * соответствующую текущему пользователю.
	 */
	private Navigator accordeonData;

	/**
	 * Создает удаленный proxy сервис для общения с серверной частью сервиса
	 * DataService.
	 */
	private final DataServiceAsync dataService = GWT.create(DataService.class);

	/**
	 * @return the accordeon
	 */
	public Widget getAccordeon() {
		return accordeon;
	}

	/**
	 * @param aaccordeon
	 *            the accordeon to set
	 */
	public void setAccordeon(final StackLayoutPanel aaccordeon) {
		this.accordeon = aaccordeon;
	}

	/**
	 * @return the accordeonData
	 */
	public Navigator getAccordeonData() {
		return accordeonData;
	}

	/**
	 * @param aaccordeonData
	 *            the accordeonData to set
	 */
	public void setAccordeonData(final Navigator aaccordeonData) {
		this.accordeonData = aaccordeonData;
	}

	/**
	 * 
	 * Создает аккордеон (дерево или правый навигатор) приложения.
	 * 
	 * @return возвращает заполненный виджет accordeon типа SimplePanel.
	 */
	public Widget generateAccordeon() {

		final SimplePanel verpan = new SimplePanel();

		verpan.add(new HTML(Constants.PLEASE_WAIT_NAVIGATION_DATA_ARE_LOADING));

		accordeon.setSize("100%", "100%");
		verpan.setSize("100%", "100%");

		final DecoratedPopupPanel db = new DecoratedPopupPanel();
		db.setAnimationEnabled(true);
		db.setGlassEnabled(true);
		Image waiteImage = new Image();
		waiteImage.setUrl("resources/internal/progress.gif");
		db.add(waiteImage);
		db.center();
		db.show();

		dataService.getNavigator(new GWTServiceCallback<Navigator>(
				Constants.ERROR_OF_NAVIGATOR_DATA_RETRIEVING_FROM_SERVER) {
			@Override
			public void onFailure(final Throwable caught) {

				db.hide();
				super.onFailure(caught);

			}

			@Override
			public void onSuccess(final Navigator navigator) {
				db.hide();
				AppCurrContext.getInstance().getMainPanel()
						.generateMainPanel(!navigator.getHideOnLoad(), navigator.getWidth());

				// navigator.getWidth()
				fillAccordeon(navigator);
				verpan.clear();
				verpan.add(accordeon);
				NavigatorElement nav = navigator.getAutoSelectElement();
				if (nav != null) {
					selectNesessaryItemInAccardion(nav.getId());
				}

			}

		});

		return verpan;
	}

	/**
	 * 
	 * Выделяет элемент с соответствующим id в аккордеон.
	 * 
	 * @param id
	 *            - id
	 */
	protected void selectNesessaryItemInAccardion(final String id) {

		int n = getGroupNamberInAccordeonById(id);
		if (n > -1) {
			accordeon.showWidget(n);

			TreeItem ti = getTreeItemInAccordeonById(id);
			if (ti != null) {
				uiListOfAccordeonTrees.get(n).setSelectedItem(ti, true);
			}
		}

	}

	private void fillAccordeon(final Navigator navigator) {
		final int m = 4;

		for (int i = 0; i < navigator.getGroups().size(); i++) {
			accordeon.add(getGroupTreeWidget(navigator.getGroups().get(i)),
					getGroupString(navigator.getGroups().get(i)), true, m);
		}
	}

	private String getGroupString(final NavigatorGroup ng) {

		HorizontalPanel hPanel = new HorizontalPanel();

		final int n = 5;
		hPanel.setSpacing(n);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		Image im = new Image();
		im.setUrl(ng.getImageId()); // by default
									// "resources/group_icon_default.png"
		hPanel.add(im);
		HTML headerText = new HTML(ng.getName());
		headerText.setStyleName("cw-StackPanelHeader");
		hPanel.add(headerText);
		return hPanel.getElement().getString();
	}

	private Widget getGroupTreeWidget(final NavigatorGroup ng) {

		if (!(ng.getElements().size() > 0)) {
			return new HTML(Constants.EMPTY);
		}
		SimplePanel simpPanel = new SimplePanel();
		ScrollPanel sp = new ScrollPanel();
		simpPanel.add(sp);
		Tree groupTree = new Tree();
		uiListOfAccordeonTrees.add(groupTree);
		sp.add(groupTree);
		sp.setSize("100%", "100%");

		for (int i = 0; i < ng.getElements().size(); i++) {

			TreeItem ti = groupTree.addItem(ng.getElements().get(i).getName());
			ti.setUserObject(ng.getElements().get(i));
			generateTreeItem(ng.getElements().get(i), ti);

		}

		groupTree.addSelectionHandler(new TreeSelectionHandler());

		return simpPanel;

	}

	private void generateTreeItem(final NavigatorElement element, final TreeItem treeItem) {
		if (!(element.getElements().size() > 0)) {
			return;
		}

		for (int i = 0; i < element.getElements().size(); i++) {
			TreeItem ti = treeItem.addItem(element.getElements().get(i).getName());
			ti.setUserObject(element.getElements().get(i));
			generateTreeItem(element.getElements().get(i), ti);
		}

	}

	/**
	 * @param auiListOfAccordeonTrees
	 *            the uiWidgetsAndDataAccordeon to set
	 */
	public static void setUiListOfAccordeonTrees(final List<Tree> auiListOfAccordeonTrees) {
		Accordeon.uiListOfAccordeonTrees = auiListOfAccordeonTrees;
	}

	/**
	 * @return the uiListOfAccordeonTrees
	 */
	public static List<Tree> getUiListOfAccordeonTrees() {
		return uiListOfAccordeonTrees;
	}

	/**
	 * 
	 * Функция, которая убирает выделение всех элементов всех деревьев
	 * аккардиона, за исключением элемента lastSelectedItem.
	 * 
	 * @param alastSelectedItem
	 *            - TreeItem
	 */
	public static void
			unselectAllTreesItemsExcludingLastSelecter(final TreeItem alastSelectedItem) {

		for (int i = 0; i < getUiListOfAccordeonTrees().size(); i++) {
			if (alastSelectedItem.getTree() != getUiListOfAccordeonTrees().get(i)) {
				getUiListOfAccordeonTrees().get(i).setSelectedItem(null);
			}
		}
		lastSelectedItem = alastSelectedItem;
	}

	/**
	 * Функция, которая отменяет выделение alastSelectedItem, и выделяет
	 * предыдущий выделенный в аккардионе TreeItem (это необходимо, если
	 * произошел клик на TreeItem, на котором нет дейстия-Action).
	 * 
	 * @param alastSelectedItem
	 *            - TreeItem
	 */
	public static void selectLastSelectedItem(final TreeItem alastSelectedItem) {

		alastSelectedItem.getTree().setSelectedItem(null);
		if (lastSelectedItem != null) {
			if (lastSelectedItem.getTree() == alastSelectedItem.getTree()) {
				lastSelectedItem.getTree().setSelectedItem(lastSelectedItem, false);
			}
		}
	}

	/**
	 * Функция возвращающая элемент TreeItem дерева по его уникальному в рамках
	 * аккардиона Id.
	 * 
	 * @param id
	 *            - уникальный Id для элемента дерева
	 * @return - TreeItem для элемента дерева
	 */
	public TreeItem getTreeItemInAccordeonById(final String id) {
		// TODO Auto-generated method stub
		TreeItem ti = null;
		if (id == null) {
			return ti;
		}
		if (!(uiListOfAccordeonTrees.size() > 0)) {
			return ti;
		}

		for (Tree t : uiListOfAccordeonTrees) {

			for (int i = 0; i < t.getItemCount(); i++) {

				if (id.equals(((NavigatorElement) t.getItem(i).getUserObject()).getId())) {

					return t.getItem(i);

				} else {
					ti = getChildItemsInTreeElement(t.getItem(i), id);
					if (ti != null) {
						return ti;
					}

				}

			}

		}

		return ti;

	}

	private TreeItem getChildItemsInTreeElement(final TreeItem ti, final String id) {

		if (!(ti.getChildCount() > 0)) {
			return null;
		}

		for (int i = 0; i < ti.getChildCount(); i++) {

			if (id.equals(((NavigatorElement) ti.getChild(i).getUserObject()).getId())) {

				return ti.getChild(i);

			} else {
				TreeItem temp = getChildItemsInTreeElement(ti.getChild(i), id);
				if (temp != null) {
					return temp;
				}

			}

		}
		return null;
	}

	/**
	 * Функция возвращающая номер группы в аккардионе куда входит элемент дерева
	 * с заданным Id.
	 * 
	 * @param id
	 *            - уникальный Id для элемента дерева
	 * @return - Integer - номер группы аккардеона
	 */
	public Integer getGroupNamberInAccordeonById(final String id) {

		TreeItem ti = null;
		if (id == null) {
			return -1;
		}
		if (!(uiListOfAccordeonTrees.size() > 0)) {
			return -1;
		}

		for (int t = 0; t < uiListOfAccordeonTrees.size(); t++) {
			for (int i = 0; i < uiListOfAccordeonTrees.get(t).getItemCount(); i++) {

				if (id.equals(((NavigatorElement) uiListOfAccordeonTrees.get(t).getItem(i)
						.getUserObject()).getId())) {

					return t;

				} else {
					ti = getChildItemsInTreeElement(uiListOfAccordeonTrees.get(t).getItem(i), id);
					if (ti != null) {
						return t;
					}

				}

			}

		}

		return -1;

	}

}
