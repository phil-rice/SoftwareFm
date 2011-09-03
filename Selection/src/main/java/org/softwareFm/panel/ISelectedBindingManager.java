package org.softwareFm.panel;


public interface ISelectedBindingManager {

	void addSelectedArtifactSelectionListener(ISelectedBindingListener listener);

	void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener);

	public static class Utils {
		public static ISelectedBindingManager noSelectedBindingManager() {
			return new ISelectedBindingManager() {
				@Override
				public void addSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
				}

				@Override
				public void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
				}
			};
		}
	}
}
