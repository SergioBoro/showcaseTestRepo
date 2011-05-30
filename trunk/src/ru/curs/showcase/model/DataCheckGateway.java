package ru.curs.showcase.model;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.exception.IncorrectElementException;

/**
 * Абстрактный класс шлюза с проверкой данных для элементов панели управления.
 * 
 * @author den
 * 
 */
public abstract class DataCheckGateway extends GeneralXMLHelper {

	/**
	 * Функция проверки переданных в шлюз данных.
	 * 
	 * @param element
	 *            - информация о загружаемом элементе.
	 */
	public void check(final DataPanelElementInfo element) {
		if (element == null) {
			throw new IncorrectElementException();
		}
		if ((element.getType() != getGatewayType()) || (!element.isCorrect())) {
			throw new IncorrectElementException(element.toString());
		}
	}

	/**
	 * Функция возвращает тип шлюза.
	 * 
	 * @return - тип шлюза.
	 */
	protected abstract DataPanelElementType getGatewayType();
}
