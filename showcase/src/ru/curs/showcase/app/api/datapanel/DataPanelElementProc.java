package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.VisualElement;

/**
 * Хранимая SQL процедура элемента информационной панели.
 * 
 * @author den
 * 
 */
public final class DataPanelElementProc extends VisualElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8875550160233655449L;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "DataPanelElementProc [type=" + type + ", getId()=" + getId() + ", getName()="
				+ getName() + "]";
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataPanelElementProc)) {
			return false;
		}
		DataPanelElementProc other = (DataPanelElementProc) obj;
		if (type != other.type) {
			return false;
		}
		return true;
	}

	/**
	 * Тип процедуры.
	 */
	private DataPanelElementProcType type;

	public DataPanelElementProcType getType() {
		return type;
	}

	public void setType(final DataPanelElementProcType aType) {
		type = aType;
	}
}
