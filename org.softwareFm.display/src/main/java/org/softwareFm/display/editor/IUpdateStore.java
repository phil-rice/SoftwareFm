package org.softwareFm.display.editor;

import java.util.Map;

import org.softwareFm.display.data.ActionData;

public interface IUpdateStore {

	void update(ActionData actionData, String key, Object newValue);

	void update(final String entity, final String url, final Map<String, Object> data);

	public class Utils {

		public static IUpdateStore errorUpdateStore() {
			return new IUpdateStore() {
				@Override
				public void update(ActionData actionData, String key, Object newValue) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void update(String entity, String url, Map<String, Object> data) {
					throw new UnsupportedOperationException();
				}
			};
		}

		public static IUpdateStore sysoutUpdateStore() {
			return new IUpdateStore() {
				@Override
				public void update(ActionData actionData, String key, Object newValue) {
					System.out.println("Updateing: " + key + " to be equal to " + newValue + " in context " + actionData);
				}

				@Override
				public void update(String entity, String url, Map<String, Object> data) {
					System.out.println("Updateing: " + entity + " url " + url + " with data: " + data);

				}
			};
		}

		public static RememberUpdateStore rememberUpdateStore() {
			return new RememberUpdateStore();
		}
	}
}
