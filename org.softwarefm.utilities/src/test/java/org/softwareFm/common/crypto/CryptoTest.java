/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.crypto;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.strings.Strings;

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