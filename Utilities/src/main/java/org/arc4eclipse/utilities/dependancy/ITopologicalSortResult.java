package org.arc4eclipse.utilities.dependancy;

import org.arc4eclipse.utilities.collections.ISimpleList;
import org.arc4eclipse.utilities.exceptions.WrappedException;

public interface ITopologicalSortResult<T> extends ISimpleList<ISimpleList<T>> {

	public static class Utils {
		public static <T> void walk(ITopologicalSortResult<T> result, ITopologicalSortResultVisitor<T> visitor) {
			try {
				for (int i = 0; i < result.size(); i++) {
					ISimpleList<T> item = result.get(i);
					for (int j = 0; j < item.size(); j++)
						visitor.visit(i, item.get(j));
				}
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public static <T> void inverseWalk(ITopologicalSortResult<T> result, ITopologicalSortResultVisitor<T> visitor) {
			try {
				for (int i = result.size() - 1; i >= 0; i--) {
					ISimpleList<T> item = result.get(i);
					for (int j = 0; j < item.size(); j++)
						visitor.visit(i, item.get(j));
				}
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}

}
