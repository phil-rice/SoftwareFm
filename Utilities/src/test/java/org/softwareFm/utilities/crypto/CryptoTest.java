package org.softwareFm.utilities.crypto;

import junit.framework.TestCase;

import org.softwareFm.utilities.strings.Strings;

public class CryptoTest extends TestCase{

	public void testEncryptDecrypt() {
		checkCrypto("here is some text");
	}

	private void checkCrypto(String string) {
		String key = Crypto.makeKey();
		String coded1 = Crypto.aesEncrypt(key, string);
		String result = Crypto.aesDecrypt(key, coded1);
		assertEquals(string,result);
		assertFalse(coded1.equals(result));
		Strings.fromHex(coded1); //barfs if not hex

		String coded2 = Crypto.aesEncrypt(key, string);
		assertEquals(coded1, coded2);
	}

}
