package org.arc4eclipse.MasterDetailPanel;

import java.util.Arrays;
import java.util.List;

public interface IMasterDetailPanel<T> extends ITableData<T> {

	void setDetailHtmlGetter(IHtmlDetailPopulator<T> populator);

	public static class Utils {
		public static <T> void populate(IMasterDetailPanel<T> panel, T... data) {
			populate(panel, Arrays.asList(data));
		}

		public static <T> void populate(IMasterDetailPanel<T> panel, List<T> data) {
			panel.clear();
			for (T t : data)
				panel.append(t);
		}

	}

}
