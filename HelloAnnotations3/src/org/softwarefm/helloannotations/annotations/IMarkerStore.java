package org.softwarefm.helloannotations.annotations;

import java.util.Map;

import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.maps.Maps;

public interface IMarkerStore {

	void makerFor(String sfmId, ICallback<String> markerFoundCallback);

	public static class Utils {

		public static IMarkerStore mock(final String... nameAndvalues) {
			return new IMarkerStore() {

				private final Map<String, String> map = Maps.makeMap((Object[]) nameAndvalues);

				@Override
				public void makerFor(String sfmId, ICallback<String> markerFoundCallback) {
					String result = map.get(sfmId);
					if (result != null)
						try {
							markerFoundCallback.process(result);
						} catch (Exception e) {
							throw WrappedException.wrap(e);
						}
				}

			};
		}

	}

}
