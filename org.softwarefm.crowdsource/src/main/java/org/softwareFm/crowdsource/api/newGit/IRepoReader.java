package org.softwareFm.crowdsource.api.newGit;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.crowdsource.utilities.collections.AbstractFindNextIterable;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.maps.Maps;

/**
 * Each transaction will have its own unique IRepoData<br />
 * 
 * These methods must all be called in a transaction, or they will fail. None of the changes will take place until the transaction is committed.<br />
 * 
 * The interface doesn't define whether it is using pessimistic or optimistic locking. In either case a RedoTransactionException maybe thrown (although it will be thrown from different methods: pessimistic can throw from any method, while optimistic only from the prepare phase)
 */
public interface IRepoReader extends ISingleRowReader {

	List<String> readRaw(ISingleSource singleSource);

	RepoLocation findRepository(ISingleSource singleSource);

	public static class Utils {

		public static int countLines(IRepoReader reader, ISingleSource source) {
			return reader.readRaw(source).size();
		}

		public static Map<ISingleSource, Integer> countLines(IRepoReader reader, ISources sources) {
			Map<ISingleSource, Integer> result = Maps.newMap();
			for (ISingleSource source : sources.singleSources(reader))
				result.put(source, countLines(reader, source));
			return result;
		}

		public static List<Map<String, Object>> readAllRows(IRepoReader reader, ISingleSource singleSource) {
			List<String> readRaw = reader.readRaw(singleSource);
			List<Map<String, Object>> result = Lists.newList();
			for (int i = 0; i < readRaw.size(); i++)
				result.add(reader.readRow(singleSource, i));
			return result;
		}

		public static Collection<RepoLocation> findRepositories(IRepoReader reader, ISources sources) {
			Set<RepoLocation> result = Sets.newSet();
			for (ISingleSource source : sources.singleSources(reader)) {
				RepoLocation repoLocation = reader.findRepository(source);
				result.add(repoLocation);
			}
			return result;
		}

		public static Map<String, Object> readFirstRow(IRepoReader reader, ISingleSource singleSource) {
			if (countLines(reader, singleSource) > 0)
				return reader.readRow(singleSource, 0);
			else
				return Maps.emptyStringObjectMap();
		}

		public static String readPropertyFromFirstLine(IRepoReader reader, ISingleSource singleSource, String cryptoKey) {
			return (String) readFirstRow(reader, singleSource).get(cryptoKey);
		}

		public static Iterable<SourcedMap> read(final IRepoReader reader, final ISingleSource source, final int offset) {
			List<String> raw = reader.readRaw(source);
			final int size = raw.size();
			if (offset >= size)
				return Collections.emptyList();
			return new AbstractFindNextIterable<SourcedMap, AtomicInteger>() {
				@Override
				protected SourcedMap findNext(AtomicInteger context) throws Exception {
					int index = context.getAndIncrement();
					if (index >= size)
						return null;
					return new SourcedMap(source, index, reader.readRow(source, index));
				}

				@Override
				protected AtomicInteger reset() throws Exception {
					return new AtomicInteger(offset);
				}
			};
		}

		public static Iterable<SourcedMap> read(final IRepoReader repoReader, final ISources sources, final int offset) {
			return new AbstractFindNextIterable<SourcedMap, ReadContext>() {

				@Override
				protected SourcedMap findNext(ReadContext context) throws Exception {
					if (context == null)
						return null;
					while (!context.iterator.hasNext() && context.singleSouceIterator.hasNext())
						context.iterator = read(repoReader, context.singleSouceIterator.next(), 0).iterator();
					if (!context.iterator.hasNext())
						return null;
					return context.iterator.next();
				}

				@Override
				protected ReadContext reset() throws Exception {
					List<ISingleSource> singleSources = sources.singleSources(repoReader);
					int sum = 0;
					Iterator<ISingleSource> singleSouceIterator = singleSources.iterator();
					while (true) {
						if (!singleSouceIterator.hasNext())
							return null;
						ISingleSource singleSource = singleSouceIterator.next();
						int lines = countLines(repoReader, singleSource);
						if (sum + lines >= offset)
							return new ReadContext(singleSouceIterator, read(repoReader, singleSource, offset - sum).iterator());
						sum += lines;
					}
				}
			};
		}

		static class ReadContext {

			final Iterator<ISingleSource> singleSouceIterator;
			Iterator<SourcedMap> iterator;

			public ReadContext(Iterator<ISingleSource> singleSouceIterator, Iterator<SourcedMap> iterator) {
				this.singleSouceIterator = singleSouceIterator;
				this.iterator = iterator;
			}

		}
	}
}
