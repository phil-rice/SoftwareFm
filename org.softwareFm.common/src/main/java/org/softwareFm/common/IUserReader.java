package org.softwareFm.common;

import java.util.Map;

import org.softwareFm.common.internal.LocalUserReader;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;

public interface IUserReader {
	<T> T getUserProperty(String userId, String cryptoKey, String property);

	void refresh(String userId);

	abstract public static class Utils {
		public static IUserReader localUserReader(IGitLocal gitLocal, IUrlGenerator userGenerator) {
			return new LocalUserReader(gitLocal, userGenerator);
		}
		
		public static IUserReader mockReader(final Object...nameAndVariables){
			return new IUserReader() {
				private final Map<String,Object> map = Maps.makeMap(nameAndVariables);
				@Override
				public void refresh(String userId) {
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public <T> T getUserProperty(String userId, String cryptoKey, String property) {
					return (T) map.get(property);
				}
			};
		}
		
		public static IUserReader exceptionUserReader(){
			return new IUserReader() {
				
				@Override
				public void refresh(String userId) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public <T> T getUserProperty(String userId, String cryptoKey, String property) {
					throw new UnsupportedOperationException();
				}
			};
		}

	}
}
