package org.softwareFm.displayCore.api;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IHasControl;

public interface ILineEditor<T> {

	ICodec<T> getCodec();

	IHasControl makeLineControl(ILineEditable<T> lineEditable, Composite parent, int index, T t);

	void add(ILineEditable<T> lineEditable);

	void edit(ILineEditable<T> lineEditable, int i);

	public static class Utils {
		public static <T> ILineEditor<T> noLineEditor() {
			return new ILineEditor<T>() {
				@Override
				public String toString() {
					return "noLineEditor";
				}

				@Override
				public void add(ILineEditable<T> lineEditable) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void edit(ILineEditable<T> lineEditable, int i) {
					throw new UnsupportedOperationException();
				}

				@Override
				public ICodec<T> getCodec() {
					return ICodec.Utils.errorEncoder();
				}

				@Override
				public IHasControl makeLineControl(ILineEditable<T> lineEditable, Composite parent, int index, T t) {
					throw new UnsupportedOperationException();
				};

			};
		}
	}
}
