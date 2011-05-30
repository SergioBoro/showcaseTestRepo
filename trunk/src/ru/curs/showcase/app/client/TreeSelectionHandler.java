/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.DataPanel;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.navigator.NavigatorElement;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Обработчик выделения элемента дерева в аккордеоне.
 * 
 */
public class TreeSelectionHandler implements SelectionHandler<TreeItem> {

	@Override
	public void onSelection(final SelectionEvent<TreeItem> arg0) {

		if (((NavigatorElement) arg0.getSelectedItem().getUserObject()).getAction() != null) {

			Action ac = ((NavigatorElement) arg0.getSelectedItem().getUserObject()).getAction();
			AppCurrContext.getInstance().setNavigatorAction(ac);
			generateDatePanel(AppCurrContext.getInstance().getNavigatorAction(),
					arg0.getSelectedItem());
		} else {
			Accordeon.selectLastSelectedItem(arg0.getSelectedItem());
		}
		((ScrollPanel) arg0.getSelectedItem().getTree().getParent())
				.setHorizontalScrollPosition(0);

	}

	private void generateDatePanel(final Action action, final TreeItem selectedTreeItem) {

		final DataServiceAsync dataService = GWT.create(DataService.class);
		dataService.getDataPanel(action, new GWTServiceCallback<DataPanel>(Constants.ERROR) {

			// @Override
			// public void onFailure(final Throwable caught) {

			// MessageBox.showMessageWithDetails(Constants.ERROR,
			// caught.getMessage(),
			// GeneralServerException
			// .checkExeptionTypeAndCreateDetailedTextOfException(caught));
			// }

			@Override
			public void onSuccess(final DataPanel dp) {
				GeneralDataPanel.redrowGeneralDataPanelAtnavigatorClick(dp);
				Accordeon.unselectAllTreesItemsExcludingLastSelecter(selectedTreeItem);

			}

		});

		return;

	}

}
