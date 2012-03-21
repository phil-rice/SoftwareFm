package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.utilities.crypto.Crypto;

public interface ICryptoGenerators {

	String userCrypto();

	String groupCrypto();

	String otherCrypto(String key);

	public static class Utils {
		public static ICryptoGenerators mock(final String[] userCryptos, final String[] groupCryptos){
			return new ICryptoGenerators() {
				private int userIndex;
				private int groupIndex;
				@Override
				public String userCrypto() {
					return userCryptos[userIndex++];
				}
				
				@Override
				public String otherCrypto(String key) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public String groupCrypto() {
					return groupCryptos[groupIndex++];
				}
			};
		}
		
		public static ICryptoGenerators cryptoGenerators(){
			return new ICryptoGenerators() {
				@Override
				public String userCrypto() {
					return Crypto.makeKey();
				}
				
				@Override
				public String otherCrypto(String key) {
					return Crypto.makeKey();
				}
				
				@Override
				public String groupCrypto() {
					return Crypto.makeKey();
				}
			};
		}
	}
}
