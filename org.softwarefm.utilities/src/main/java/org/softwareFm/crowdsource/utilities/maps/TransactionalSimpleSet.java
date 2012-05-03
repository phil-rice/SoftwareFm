package org.softwareFm.crowdsource.utilities.maps;

import java.util.Set;

import org.softwareFm.crowdsource.utilities.collections.ITransactionalMutableSimpleSet;
import org.softwareFm.crowdsource.utilities.collections.Sets;

public class TransactionalSimpleSet<V> implements ITransactionalMutableSimpleSet<V> {

	private final Set<V> baseSet;
	private final Set<V> escrowSet = Sets.newSet();

	public TransactionalSimpleSet() {
		this(Sets.<V> newSet());
	}

	public TransactionalSimpleSet(Set<V> raw) {
		this.baseSet = raw;
	}

	@Override
	public void commit() {
		baseSet.addAll(escrowSet);
		escrowSet.clear();
	}

	@Override
	public void rollback() {
		escrowSet.clear();
	}

	@Override
	public boolean contains(V value) {
		return baseSet.contains(value) || escrowSet.contains(value);
	}

	@Override
	public void add(V value) {
		escrowSet.add(value);
	}

}
