package org.softwareFm.utilities.crypto;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class IdentifyCryptoProviders {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		Provider providers[];
		if (null != args && 0 < args.length) {
			providers = new Provider[args.length];
			for (int i = 0; i < args.length; i++)
				providers[i] = Security.getProvider(args[i]);

		} else {
			providers = Security.getProviders();
		}
		for (int i = 0; i < providers.length; i++) {
			Provider p = providers[i];
			System.out.println("Provider: " + p);
			System.out.println("===============================");
			System.out.println("provider properties:");
			ArrayList keys = new ArrayList(p.keySet());
			Collections.sort(keys);
			String key;
			for (Iterator j = keys.iterator(); j.hasNext(); System.out.println(key + "=" + p.get(key)))
				key = (String) j.next();

			System.out.println("-------------------------------");
		}
	}
}
