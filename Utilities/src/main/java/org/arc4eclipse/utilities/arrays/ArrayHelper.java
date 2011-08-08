package org.arc4eclipse.utilities.arrays;

import java.lang.reflect.Array;

import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;

public class ArrayHelper {

	public static <From, To> To[] map(Class<To> clazz, From[] old, IFunction1<From, To> mutator) {
		try {
			@SuppressWarnings({ "unchecked" })
			To[] result = (To[]) Array.newInstance(clazz, old.length);
			for (int i = 0; i < old.length; i++)
				result[i] = mutator.apply(old[i]);
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}
