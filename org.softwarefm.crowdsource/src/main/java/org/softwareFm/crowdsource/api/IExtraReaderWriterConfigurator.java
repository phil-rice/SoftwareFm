package org.softwareFm.crowdsource.api;

public interface IExtraReaderWriterConfigurator<C extends ApiConfig> {

	void builder(IApiBuilder builder, C apiConfig);

	public static class Utils {
		public static <C extends ApiConfig> IExtraReaderWriterConfigurator<C> noExtras() {
			return new IExtraReaderWriterConfigurator<C>() {
				@Override
				public void builder(IApiBuilder builder,C apiConfig) {
				}
			};
		}
	}
}
