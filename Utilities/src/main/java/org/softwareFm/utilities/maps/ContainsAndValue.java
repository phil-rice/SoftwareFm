package org.softwareFm.utilities.maps;

public class ContainsAndValue<V> {
	public final boolean contained;
	public final V value;

	public ContainsAndValue(boolean contained, V value) {
		this.contained = contained;
		this.value = value;
	}

}
