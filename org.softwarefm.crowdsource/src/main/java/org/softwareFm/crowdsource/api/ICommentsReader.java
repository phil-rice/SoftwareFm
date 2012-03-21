package org.softwareFm.crowdsource.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;

/**
 * There are three types of comments in softwarefm: global, group based, user based. It is likely that more categories will be added.
 * 
 * <ul>
 * <li>global comments are stored in the file global.comments next to the 'data.json' that they are about. They are stored in plain text
 * <li>group comments are stored in the same directory as the global comments, with a name {uuid}.comments. If they are encrypted, each line is encrypted separately to allow git to work well
 * <li>user comments are treated the same as group comments
 * </ul>
 * 
 */
public interface ICommentsReader {

	List<Map<String, Object>> globalComments(String baseUrl, String source);

	List<Map<String, Object>> groupComments(String baseUrl, String softwareFmId, String userCrypto);

	List<Map<String, Object>> myComments(String baseUrl, String softwareFmId, String userCrypto, String source);

	public static class Utils {

		public static List<Map<String, Object>> allComments(ICommentsReader reader, String baseUrl, String softwareFmId, String userCrypto, String globalSource, String mySource) {
			List<Map<String, Object>> result = Lists.newList();
			result.addAll(reader.globalComments(baseUrl, globalSource));
			if (softwareFmId != null) {
				result.addAll(reader.groupComments(baseUrl, softwareFmId, userCrypto));
				result.addAll(reader.myComments(baseUrl, softwareFmId, userCrypto, mySource));
			}
			return result;
		}

		public static ICommentsReader mockReader(final String softwareFmId, final String moniker, final long time, final String... texts) {
			return new ICommentsReader() {

				@Override
				public List<Map<String, Object>> myComments(String baseUrl, String softwareFmId, String userCrypto, String source) {
					return makeComments("myComments", source);
				}

				protected List<Map<String, Object>> makeComments(final String prefix, final String source) {
					return Lists.map(Arrays.asList(texts), new IFunction1<String, Map<String, Object>>() {
						@Override
						public Map<String, Object> apply(String from) throws Exception {
							return Maps.stringObjectMap(//
									LoginConstants.softwareFmIdKey, softwareFmId, //
									CommentConstants.creatorKey, moniker, //
									CommentConstants.textKey, prefix + "/" + from, //
									CommentConstants.timeKey, time,//
									CommentConstants.sourceKey, source);
						}
					});
				}

				@Override
				public List<Map<String, Object>> groupComments(String baseUrl, String softwareFmId, String userCrypto) {
					return makeComments("groupComments", "someGroup");
				}

				@Override
				public List<Map<String, Object>> globalComments(String baseUrl, String source) {
					return makeComments("globalComments", source);
				}
			};
		}

		public static ICommentsReader exceptionCommentsReader() {
			return new ICommentsReader() {
				@Override
				public List<Map<String, Object>> myComments(String baseUrl, String softwareFmId, String userCrypto, String source) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public List<Map<String, Object>> groupComments(String baseUrl, String softwareFmId, String userCrypto) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public List<Map<String, Object>> globalComments(String baseUrl, String source) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

}
