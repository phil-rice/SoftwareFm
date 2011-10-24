package org.softwareFm.utilities.collections;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.softwareFm.utilities.constants.UtilityMessages;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class Lists {

	public static <T> int indexOf(List<T> list, T object) {
		int indexOne = identityIndexOf(list, object);
		if (indexOne != -1)
			return indexOne;
		int indexTwo = equalsIndexOf(list, object);
		return indexTwo;
	}

	public static <T> void addAllUnique(List<T> target, Iterable<T> toBeAdded) {
		for (T t : toBeAdded)
			if (!target.contains(t))
				target.add(t);
	}

	private final static Random random = new Random(System.nanoTime());

	public static <T> List<T> shuffle(List<T> list) {
		return shuffle(random, list);
	}

	public static <T> List<T> shuffle(Random random, List<T> list) {
		List<T> result = new ArrayList<T>(list);
		for (int i = result.size() - 1; i > 0; i--) {
			int n = random.nextInt(i + 1);
			swap(result, i, n);
		}
		return result;
	}

	public static <T> void swap(List<T> list, int i, int j) {
		T temp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, temp);
	}

	public static <T> int indexOf(List<T> listOne, List<T> listTwo, T object) {
		int indexOne = identityIndexOf(listOne, object);
		if (indexOne != -1)
			return indexOne;
		int indexTwo = identityIndexOf(listTwo, object);
		if (indexTwo != -1)
			return indexTwo + listOne.size();
		int indexOneA = equalsIndexOf(listOne, object);
		if (indexOneA != -1)
			return indexOneA;
		int indexTwoB = equalsIndexOf(listTwo, object);
		if (indexTwoB != -1)
			return indexTwoB + listOne.size();
		return -1;
	}

	private static <T> int equalsIndexOf(List<T> list, T object) {
		for (int i = 0; i < list.size(); i++)
			if (object.equals(list.get(i)))
				return i;
		return -1;
	}

	private static <T> int identityIndexOf(List<T> list, T object) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) == object)
				return i;
		return -1;
	}

	private static <To, From> List<To> makeListFor(Iterable<From> from) {
		int size = from instanceof List ? ((List<From>) from).size() : 0;
		List<To> result = new ArrayList<To>(size);
		return result;
	}

	public static <T> List<T>[] partition(int num, List<T> input) {
		assert input.size() % num == 0;
		if (num <= 0)
			throw new IllegalArgumentException(MessageFormat.format(UtilityMessages.needPositivePartitionSize, num));
		int size = input.size() / num;
		@SuppressWarnings("unchecked")
		List<T>[] result = (List<T>[]) Array.newInstance(List.class, num);
		for (int i = 0; i < num; i++)
			result[i] = new ArrayList<T>(size);
		int i = 0;
		for (Iterator<T> iterator = input.iterator(); iterator.hasNext();)
			result[i++ % num].add(iterator.next());
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static <T> Map<Class, List<T>> partitionByClass(Iterable<T> input, Class... partitionClasses) {
		List<Class> classList = Arrays.asList(partitionClasses);
		Map<Class, List<T>> result = Maps.newMap();
		for (T item : input) {
			int index = findClass(classList, item);
			if (index == -1)
				throw new IllegalArgumentException("Item of " + item.getClass() + " found, which is not in " + classList);
			Class key = classList.get(index);
			Maps.addToList(result, key, item);
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> int findClass(List<Class> classList, T item) {
		Class<? extends Object> itemClass = item.getClass();
		int i = 0;
		for (Class clazz : classList) {
			if (clazz.isAssignableFrom(itemClass))
				return i;
			i++;
		}
		return -1;
	}

	public static <T> List<T> newList() {
		return new ArrayList<T>();
	}

	public static <T> List<T> newList(int size) {
		return new ArrayList<T>(size);
	}

	public static <T extends Comparable<T>> List<T> sort(Iterable<T> from) {
		List<T> result = makeListFor(from);
		for (T t : from)
			result.add(t);
		Collections.sort(result);
		return result;
	}

	public static <T> List<T> sort(Iterable<T> from, Comparator<T> comparator) {
		List<T> result = makeListFor(from);
		for (T t : from)
			result.add(t);
		Collections.sort(result, comparator);
		return result;
	}

	public static <T extends Comparable<T>> Comparator<T> byListOrder(final List<T> masterList) {
		return new Comparator<T>() {
			@Override
			public int compare(T arg0, T arg1) {
				int i0 = masterList.indexOf(arg0);
				int i1 = masterList.indexOf(arg1);
				return i0 - i1;
			}
		};
	}

	public static <T> List<T> append(List<T> initial, T... more) {
		List<T> result = new ArrayList<T>(initial.size() + more.length);
		result.addAll(initial);
		result.addAll(Arrays.asList(more));
		return result;
	}

	public static <T> List<T> addAtStart(List<T> initial, T... more) {
		List<T> result = new ArrayList<T>(initial.size() + more.length);
		result.addAll(Arrays.asList(more));
		result.addAll(initial);
		return result;
	}

	public static <T> List<T> addAtStart(Iterable<T> initial, T... more) {
		List<T> result = new ArrayList<T>();
		result.addAll(Arrays.asList(more));
		for (T t : initial)
			result.add(t);
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static <From, To> List<To> map(Iterable<From> fromList, IFunction1<From, To> mappingFunction) {
		try {
			List<To> result = (fromList instanceof List) ? new ArrayList<To>(((List) fromList).size()) : new ArrayList<To>();
			for (From from : fromList)
				result.add(mappingFunction.apply(from));
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <T> List<T> merge(Collection<T>... ts) {
		List<T> result = newList();
		for (Collection<T> t : ts)
			result.addAll(t);
		return result;
	}

	public static <T> List<T> fromArray(T[] ts) {
		return new ArrayList<T>(Arrays.asList(ts));
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> nullSafe(List<T> ts) {
		return ts == null ? Collections.EMPTY_LIST : ts;
	}

	public static List<Byte> asList(byte[] data) {
		ArrayList<Byte> result = new ArrayList<Byte>(data.length);
		for (byte b : data)
			result.add(b);
		return result;
	}

	public static <T> List<T> remove(List<T> oldList, int index) {
		List<T> newList = Lists.newList();
		newList.addAll(oldList);
		newList.remove(index);
		return newList;
	}

	public static <T> List<T> immutableCopy(List<T> ts) {
		return ts == null ? null : Collections.unmodifiableList(new ArrayList<T>(ts));
	}

	public static <T> void removeAllAfter(List<T> list, int lastToKeep) {
		while (list.size() > lastToKeep + 1)
			list.remove(list.size() - 1);
	}

	public static <T extends Comparable<T>> Comparator<T> orderedComparator(T... order) {
		final List<T> list = Arrays.asList(order);
		return new Comparator<T>() {
			@Override
			public int compare(T left, T right) {
				int leftIndex = list.indexOf(left);
				int rightIndex = list.indexOf(right);
				if (leftIndex == -1)
					if (rightIndex == -1)
						return left.compareTo(right);
					else
						return 1;
				else if (rightIndex == -1)
					return -1;
				else
					return leftIndex - rightIndex;
			}
		};

	}

	public static <From, To> Comparator<From> comparator(final IFunction1<From, To> transformer, final Comparator<To> comparator) {
		return new Comparator<From>() {
			@Override
			public int compare(From o1, From o2) {
				To left = Functions.call(transformer, o1);
				To right = Functions.call(transformer, o2);
				return comparator.compare(left, right);
			}
		};

	}

	public static <From, To extends Comparable<To>> Comparator<From> comparator(final IFunction1<From, To> transformer) {
		return new Comparator<From>() {
			@Override
			public int compare(From o1, From o2) {
				To left = Functions.call(transformer, o1);
				To right = Functions.call(transformer, o2);
				return left.compareTo(right);
			}
		};

	}

	// final List<String> list = Arrays.asList(order);
	// return new Comparator<KeyValue>() {
	//
	// @Override
	// public int compare(KeyValue o1, KeyValue o2) {
	// String left = o1.key;
	// String right = o2.key;
	// int leftIndex = list.indexOf(left);
	// int rightIndex = list.indexOf(right);
	// if (leftIndex == -1)
	// if (rightIndex == -1)
	// return left.compareTo(right);
	// else
	// return 1;
	// else if (rightIndex == -1)
	// return -1;
	// else
	// return leftIndex - rightIndex;
	// }
	// };
	// }
}
