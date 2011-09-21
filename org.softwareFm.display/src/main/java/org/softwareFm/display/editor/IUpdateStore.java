package org.softwareFm.display.editor;

import org.softwareFm.display.data.ActionData;

public interface IUpdateStore {

	void update(ActionData actionData, String key, Object newValue);

	public class Utils {

		public static IUpdateStore errorUpdateStore() {
			return new IUpdateStore() {
				@Override
				public void update(ActionData actionData, String key, Object newValue) {
					throw new UnsupportedOperationException();
				}
			};
		}
		public static IUpdateStore sysoutUpdateStore() {
			return new IUpdateStore() {
				@Override
				public void update(ActionData actionData, String key, Object newValue) {
					System.out.println("Updateing: " + key +" to be equal to " + newValue + " in context " + actionData);
				}
			};
		}
	}
}
