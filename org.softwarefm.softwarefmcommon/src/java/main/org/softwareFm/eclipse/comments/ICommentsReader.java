package org.softwareFm.eclipse.comments;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.collections.Lists;


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

	List<Map<String, Object>> globalComments(String baseUrl);

	List<Map<String, Object>> groupComments(String baseUrl, String softwareFmId, String userCrypto);

	List<Map<String, Object>> myComments(String baseUrl, String softwareFmId, String userCrypto);

	public static class Utils{
		
		public static List<Map<String, Object>> allComments(ICommentsReader reader, String baseUrl, String softwareFmId, String userCrypto){
			List<Map<String, Object>> result = Lists.newList();
			result.addAll(reader.globalComments(baseUrl));
			result.addAll(reader.groupComments(baseUrl, softwareFmId, userCrypto));
			result.addAll(reader.myComments(baseUrl, softwareFmId, userCrypto));
			return result;
		}
	}
	
}
