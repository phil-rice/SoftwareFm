package org.softwareFm.crowdsource.api;

public interface IExtraReaderWriterConfigurator<API extends ApiConfig> {

	void builder(IContainerBuilder builder,  API apiConfig);

	public static class Utils {
		public static <API extends ApiConfig> IExtraReaderWriterConfigurator<API> noExtras() {
			return new IExtraReaderWriterConfigurator<API>() {
				@Override
				public  void builder(IContainerBuilder builder, API apiConfig) {
				}
			};
		}
	}
}
