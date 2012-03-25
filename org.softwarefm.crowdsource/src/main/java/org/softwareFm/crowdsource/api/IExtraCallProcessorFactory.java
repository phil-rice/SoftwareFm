package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.server.ICallProcessor;

public interface IExtraCallProcessorFactory {

	ICallProcessor[] makeExtraCalls(IContainer api, ServerConfig ServerConfig);

	public static class Utils {
		public static IExtraCallProcessorFactory noExtraCalls() {
			return new IExtraCallProcessorFactory() {
				@Override
				public ICallProcessor[] makeExtraCalls(IContainer api, ServerConfig ServerConfig) {
					return new ICallProcessor[0];
				}
			};
		}
	}
}
