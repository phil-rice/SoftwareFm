package org.arc4eclipse.utilities.dependancy;

public interface ITopologicalSortResultVisitor<T> {
	void visit(int generation, T item) throws Exception;
}
