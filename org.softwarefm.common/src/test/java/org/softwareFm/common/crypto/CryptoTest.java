package org.softwareFm.common.crypto;

import junit.framework.TestCase;

import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.strings.Strings;

public class CryptoTest extends TestCase{

	public void testEncryptDecrypt() {
		checkCrypto("here is some text");
	}
	
	public void testDigest(){
		String one = "string one";
		String two = "string two";
		String digest1 = Crypto.digest(one);
		String digest2 = Crypto.digest(two);
		
		assertEquals(digest1 ,Crypto.digest(one));
		assertEquals(digest2 ,Crypto.digest(two));
		
		assertFalse(digest1.equals(digest2));
		
		Strings.fromHex(digest1);
		Strings.fromHex(digest2);
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
