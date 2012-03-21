package org.softwareFm.crowdsource.api;

import java.util.Map;

import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public interface ITakeOnEnrichmentProvider {

	Map<String,Object> takeOn(Map<String,Object> initial, String userCrypto, ICrowdSourceReaderApi reader);
	
	public static class Utils {
		public static ITakeOnEnrichmentProvider noEnrichment(){
			return  new ITakeOnEnrichmentProvider() {
				@Override
				public Map<String, Object> takeOn(Map<String, Object> initial, String userCrypto, ICrowdSourceReaderApi reader) {
					return initial;
				}
			};
		}
		public static ITakeOnEnrichmentProvider enrichmentWithUserProperty(final String propertyName){
			return  new ITakeOnEnrichmentProvider() {
				@Override
				public Map<String, Object> takeOn(Map<String, Object> initial, final String userCrypto, ICrowdSourceReaderApi reader) {
					final String userId = (String) initial.get(LoginConstants.softwareFmIdKey);
					String propertyValue = reader.accessUserReader(new IFunction1<IUserReader, String>() {
						@Override
						public String apply(IUserReader from) throws Exception {
							return from.getUserProperty(userId, userCrypto, propertyName);
						}
					});
					return Maps.with(initial,propertyName, propertyValue);
				}
			};
		}
	}
}
