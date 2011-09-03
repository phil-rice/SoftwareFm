package org.softwareFm.utilities.dependancy;

public interface ITopologicalSortResultVisitor<T> {
	void visit(int generation, T item) throws Exception;
}
