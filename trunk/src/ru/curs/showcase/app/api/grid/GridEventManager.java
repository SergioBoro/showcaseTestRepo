package ru.curs.showcase.app.api.grid;

import java.util.Iterator;

import ru.curs.gwt.datagrid.model.Record;
import ru.curs.gwt.datagrid.selection.DataSelection;
import ru.curs.showcase.app.api.element.EventManager;
import ru.curs.showcase.app.api.event.*;

/**
 * Менеджер событий для грида.
 * 
 * @author den
 * 
 */
public class GridEventManager extends EventManager {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4188170257316586043L;

	/**
	 * Функция возвращает нужный обработчик события по переданным ей координатам
	 * ячейки и типу взаимодействия.
	 * 
	 * @param rowId
	 *            - идентификатор строки.
	 * @param colId
	 *            - идентификатор строки.
	 * @param interactionType
	 *            - тип взаимодействия.
	 * @return - событие или NULL.
	 */
	public GridEvent getEventForCell(final String rowId, final String colId,
			final InteractionType interactionType) {
		return (GridEvent) getEventByIds(rowId, colId, interactionType);
	}

	/**
	 * Возвращает действие для отрисовки зависимых элементов, содержащее
	 * фильтрацию по всем выделенным в гриде записям.
	 * 
	 * @param selection
	 *            - информация о выделенных записях.
	 * @return - действие.
	 */
	public Action getSelectionActionForDependentElements(final DataSelection selection) {
		Action result = prepareFilterAction();
		fillFilterActionBySelection(selection, result);
		finishFilterAction(result);
		return result;
	}

	private void finishFilterAction(final Action result) {
		Iterator<DataPanelElementLink> literator =
			result.getDataPanelLink().getElementLinks().iterator();
		while (literator.hasNext()) {
			DataPanelElementLink newLink = literator.next();
			newLink.getContext().finishFilter();
			if (result.getDataPanelLink().getContext().getFilter() == null) {
				result.getDataPanelLink().getContext().setFilter(newLink.getContext().getFilter());
			}
		}
	}

	private void fillFilterActionBySelection(final DataSelection selection, final Action result) {
		Iterator<Record> iterator = selection.getSelectedRecords().iterator();
		while (iterator.hasNext()) {
			Record record = iterator.next();
			GridEvent event = getEventForCell(record.getId(), null, InteractionType.SELECTION);
			if (event != null) {
				Iterator<DataPanelElementLink> literator =
					event.getAction().getDataPanelLink().getElementLinks().iterator();
				while (literator.hasNext()) {
					DataPanelElementLink curLink = literator.next();
					DataPanelElementLink resLink =
						result.getDataPanelLink().getElementLinkById(curLink.getId());
					resLink.getContext().addFilterLine(curLink.getContext());
				}
			}
		}
	}

	private Action prepareFilterAction() {
		Action result = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		Iterator<Event> iterator = getEvents().iterator();
		while (iterator.hasNext()) {
			Event event = iterator.next();
			if (event.getInteractionType() == InteractionType.SELECTION) {
				Iterator<DataPanelElementLink> literator =
					event.getAction().getDataPanelLink().getElementLinks().iterator();
				while (literator.hasNext()) {
					DataPanelElementLink curLink = literator.next();
					DataPanelElementLink newLink = curLink.gwtClone();
					newLink.getContext().setAdditional(null);
					newLink.getContext().setFilter("");
					result.getDataPanelLink().getElementLinks().add(newLink);
				}
			}
		}
		return result;
	}
}