/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.Security;
import java.util.concurrent.Callable;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tools.ant.filters.StringInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class Crypto {

	private static String algorithm = "AES";
	private static String provider = "BC";
	private static int keySize = 128;

	private static boolean initialised;

	public static String digest(String salt, String password) {
		return Strings.toHex(Files.digest(new StringInputStream(salt + "$$" + password)));
	}

	public static String digest(String string) {
		return Strings.toHex(Files.digest(new StringInputStream(string)));
	}

	public static Callable<String> makeKeyCallable() {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				return makeKey();
			}
		};

	}

	public static String makeKey() {
		try {
			init();
			KeyGenerator generator = KeyGenerator.getInstance(algorithm, provider);
			generator.init(keySize);
			Key key = generator.generateKey();
			byte[] encoded = key.getEncoded();
			return Strings.toHex(encoded);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static String aesDecrypt(String hexKey, String hexCoded) {
		try {
			init();
			SecretKeySpec key = new SecretKeySpec(Strings.fromHex(hexKey),algorithm);
			Cipher cipher = Cipher.getInstance(algorithm+"/ECB/PKCS5Padding", provider);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] codedBytes = Strings.fromHex(hexCoded);
			CipherInputStream inputStream = new CipherInputStream(new ByteArrayInputStream(codedBytes), cipher);
			byte[] bytes = Files.getBytes(inputStream, 256);
			String result = new String(bytes, "UTF-8");
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static String aesEncrypt(String hexKey, String input) {
		try {
			init();
			SecretKeySpec key = new SecretKeySpec(Strings.fromHex(hexKey), algorithm);

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(input.length());
			CipherOutputStream outputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
			Files.setText(outputStream, input);
			byte[] outputBytes = byteArrayOutputStream.toByteArray();
			String output = new String(Strings.toHex(outputBytes));
			return output;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private static void init() {
		if (initialised)
			return;
		Security.addProvider(new BouncyCastleProvider());
		initialised = true;

	}

	public static IFunction1<String, String> decryptFn(final String key) {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return Crypto.aesDecrypt(key, from);
			}
		};
	}
}