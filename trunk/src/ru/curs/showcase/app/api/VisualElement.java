package ru.curs.showcase.app.api;

/**
 * 
 * Базовый класс визуального элемента. От него наследуются все визуальные
 * элементы в UI.
 * 
 * @author den
 * 
 */
public abstract class VisualElement implements SerializableElement {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VisualElement)) {
			return false;
		}
		VisualElement other = (VisualElement) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public VisualElement() {
		super();
	}

	public VisualElement(final String aId, final String aName) {
		super();
		id = aId;
		name = aName;
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7814677563299260714L;

	/**
	 * Идентификатор визуального элемента. Не отображается в UI, служит для
	 * быстрой идентификации элемента в коде программы. Содержимое строки
	 * идентификатора не определено - это может быть и число, и GUID.
	 */
	private String id;

	/**
	 * Имя (заголовок) визуального элемента. Отображается в UI, служит для
	 * идентификации элемента пользователем программы.
	 */
	private String name;

	public final String getId() {
		return id;
	}

	public final void setId(final String aId) {
		this.id = aId;
	}

	public final String getName() {
		return name;
	}

	public final void setName(final String aName) {
		this.name = aName;
	}
}
