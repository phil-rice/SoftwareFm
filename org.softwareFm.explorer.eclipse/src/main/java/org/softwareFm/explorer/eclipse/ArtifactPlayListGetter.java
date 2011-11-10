package org.softwareFm.explorer.eclipse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Future;

import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.timeline.IPlayList;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.future.GatedFuture;

/** The play list name is the url of the artifact */
public class ArtifactPlayListGetter implements IPlayListGetter {

	static interface IPlayListBuilder {
		String collectionName();

		List<PlayItem> process(Map<String, Object> collectionMap);
	}

	abstract static class AbstractPlayListBuilder implements IPlayListBuilder {

		private final String collectionName;

		public AbstractPlayListBuilder(String collectionName) {
			this.collectionName = collectionName;
		}

		@Override
		public String collectionName() {
			return collectionName;
		}

		@Override
		public List<PlayItem> process(Map<String, Object> collectionMap) {
			List<PlayItem> result = Lists.newList();
			for (Entry<String, Object> entry : collectionMap.entrySet()) {
				if (entry.getValue() instanceof Map<?, ?>) {
					Map<String, Object> value = (Map<String, Object>) entry.getValue();
					PlayItem item = makePlayItemFrom(value);
					if (item != null)
						result.add(item);
				}
			}
			return result;
		}

		abstract protected PlayItem makePlayItemFrom(Map<String, Object> value);

	}

	static class TweetPlayListBuilder extends AbstractPlayListBuilder {

		public TweetPlayListBuilder() {
			super("tweet");
		}

		@Override
		protected PlayItem makePlayItemFrom(Map<String, Object> value) {
			String tweetName = (String) value.get("tweet");
			return new PlayItem(DisplayConstants.browserFeedType, "http://mobile.twitter.com/" + tweetName);
		}

	}

	static class RssPlayListBuilder extends AbstractPlayListBuilder {

		public RssPlayListBuilder() {
			super("rss");
		}

		@Override
		protected PlayItem makePlayItemFrom(Map<String, Object> value) {
			String url = (String) value.get("url");
			return new PlayItem(DisplayConstants.rssFeedType, url);
		}

	}

	private final ICardDataStore cardDataStore;
	private final List<IPlayListBuilder> builders;
	private final Random random = new Random();

	public ArtifactPlayListGetter(ICardDataStore cardDataStore) {
		this.cardDataStore = cardDataStore;
		builders = Arrays.<IPlayListBuilder> asList(new RssPlayListBuilder(), new TweetPlayListBuilder());
		random.setSeed(System.nanoTime());
	}

	@Override
	public Future<IPlayList> getPlayListFor(final String playListName, final ICallback<IPlayList> callback) throws Exception {
		final GatedFuture<IPlayList> gatedFuture = Futures.gatedFuture();
		cardDataStore.processDataFor(playListName, new ICardDataStoreCallback<Void>() {
			@Override
			public Void process(final String url, Map<String, Object> artifactMap) throws Exception {
				List<PlayItem> items = Lists.newList();
				processBuilder(items, 0);
				return null;
			}

			private void processBuilder(final List<PlayItem> items, final int i) {
				if (i >= builders.size())
					if (items.size() == 0)
						processNoPlayItems();
					else
						processResult(items);
				final IPlayListBuilder builder = builders.get(i);
				cardDataStore.processDataFor(playListName + "/" + builder.collectionName(), new ICardDataStoreCallback<IPlayList>() {
					@Override
					public IPlayList process(String url, Map<String, Object> collectionMap) throws Exception {
						List<PlayItem> newItems = builder.process(collectionMap);
						items.addAll(newItems);
						processBuilder(items, i + 1);
						return null;
					}

					@Override
					public IPlayList noData(String url) throws Exception {
						return process(url, Collections.<String, Object> emptyMap());
					}
				});

			}

			private void processResult(final List<PlayItem> items) {
				List<PlayItem> shuffled = Lists.shuffle(random, items);
				IPlayList result = IPlayList.Utils.make(playListName, shuffled);
				ICallback.Utils.call(callback, result);
				gatedFuture.done(result);
			}

			private void processNoPlayItems() {
				processResult(Arrays.asList(new PlayItem(DisplayConstants.browserFeedType, "www.bbc.co.uk/news")));
			}

			@Override
			public Void noData(String url) throws Exception {
				processNoPlayItems();
				return null;
			}

		});
		return gatedFuture;
	}
}
