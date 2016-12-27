package it.albertus.earthquake.html;

import java.util.LinkedList;
import java.util.List;

public class TableData {

	private final LinkedList<String> items = new LinkedList<>();

	public List<String> getItems() {
		return items;
	}

	public void addItem(final String item) {
		items.add(item);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TableData)) {
			return false;
		}
		TableData other = (TableData) obj;
		if (items == null) {
			if (other.items != null) {
				return false;
			}
		}
		else if (!items.equals(other.items)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TableData [items=" + items + "]";
	}

}
