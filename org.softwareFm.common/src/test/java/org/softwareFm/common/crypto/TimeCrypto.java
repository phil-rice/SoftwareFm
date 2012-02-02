package org.softwareFm.common.crypto;

import org.softwareFm.common.crypto.Crypto;

public class TimeCrypto {
	public static void main(String[] args) {
		String key = Crypto.makeKey();
		for (int i = 0; i < 10; i++)
			Crypto.aesDecrypt(key, Crypto.aesEncrypt(key, "this is a block of uninteresting data with nothing in it"));
		System.out.println("Starting");
		for (int i = 0; i < 10; i++) {
			timeEncrypt(key);
			timeDecrypt(key);
		}
	}

	private static void timeEncrypt(String key) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++)
			Crypto.aesEncrypt(key, "this is a block of uninteresting data with nothing in it");
		System.out.println("Encrypt: " + (System.currentTimeMillis() - start));
	}

	private static void timeDecrypt(String key) {
		String coded = Crypto.aesEncrypt(key, "this is a block of uninteresting data with nothing in it");
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++)
			Crypto.aesDecrypt(key, coded);
		System.out.println("Decrypt: " + (System.currentTimeMillis() - start));
	}
}
