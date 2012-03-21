package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.server.ICallProcessor;

public interface IExtraCallProcessorFactory {

	ICallProcessor[] makeExtraCalls(ICrowdSourceReadWriteApi api, ServerConfig ServerConfig);

	public static class Utils {
		public static IExtraCallProcessorFactory noExtraCalls() {
			return new IExtraCallProcessorFactory() {
				@Override
				public ICallProcessor[] makeExtraCalls(ICrowdSourceReadWriteApi api, ServerConfig ServerConfig) {
					return new ICallProcessor[0];
				}
			};
		}
	}
}
