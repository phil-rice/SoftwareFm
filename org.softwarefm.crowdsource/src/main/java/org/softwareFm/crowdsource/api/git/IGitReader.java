package org.softwareFm.crowdsource.api.git;

import java.io.File;
import java.util.Map;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.IHasCache;

public interface IGitReader extends IHasCache {
	/** Where is the root of this tree of repositories located */
	File getRoot();

	/** loads a file as a string */
	String getFileAsString(IFileDescription fileDescription);

	/** loads a file as a map */
	Map<String, Object> getFile(IFileDescription fileDescription);

	/** loads a file as a iterable of maps. Each line is a map, and optionally encrypted. This returns an Iterable rather than list on average, as this will often halve the processing time */
	Iterable<Map<String, Object>> getFileAsListOfMaps(IFileDescription fileDescription);

	/** This avoids having to decrypt the file to work out how many items are in it. It just counts the lines */
	int countOfFileAsListsOfMap(IFileDescription fileDescription);

	/** creates a map which is the aggregate of the file and it's descendants. The descendants are in sub directories with the same crypto key and name */
	Map<String, Object> getFileAndDescendants(IFileDescription fileDescription);

	public static class Utils {

		public static Iterable<Map<String, Object>> getFileAsListOfMaps(IContainer container, final IFileDescription fileDescription) {
			return container.accessGitReader(new IFunction1<IGitReader, Iterable<Map<String, Object>>>() {
				@Override
				public Iterable<Map<String, Object>> apply(IGitReader gitReader) throws Exception {
					Iterable<Map<String, Object>> result = gitReader.getFileAsListOfMaps(fileDescription);
					return result;
				}
			}).get(container.defaultTimeOutMs());
		}

		public static String getFileAsString(IContainer container, final IFileDescription fileDescription) {
			return container.accessGitReader(new IFunction1<IGitReader, String>() {
				@Override
				public String apply(IGitReader gitReader) throws Exception {
					String result = gitReader.getFileAsString(fileDescription);
					return result;
				}
			}).get(container.defaultTimeOutMs());
		}

		public static Map<String, Object> getFileAsMap(IContainer container, final IFileDescription fileDescription) {
			return container.accessGitReader(new IFunction1<IGitReader, Map<String, Object>>() {
				@Override
				public Map<String, Object> apply(IGitReader gitReader) throws Exception {
					Map<String, Object> result = gitReader.getFile(fileDescription);
					return result;
				}
			}).get(container.defaultTimeOutMs());
		}

		public static Integer countOfFileAsListsOfMap(IContainer container, final IFileDescription fileDescription) {
			return container.accessGitReader(new IFunction1<IGitReader, Integer>() {
				@Override
				public Integer apply(IGitReader gitReader) throws Exception {
					int result = gitReader.countOfFileAsListsOfMap(fileDescription);
					return result;
				}
			}).get(container.defaultTimeOutMs());
		}

		public static void clearCache(IContainer container) {
			container.accessGitReader(new IFunction1<IGitReader, Void>() {
				@Override
				public Void apply(IGitReader gitReader) throws Exception {
					gitReader.clearCaches();
					return null;
				}
			});
		}

		public static Map<String,Object> getFileAndDescendants(IContainer container, final IFileDescription fileDescription) {
			return container.accessGitReader(new IFunction1<IGitReader, Map<String,Object>>() {
				@Override
				public Map<String,Object> apply(IGitReader gitReader) throws Exception {
					return gitReader.getFileAndDescendants(fileDescription);
				}
			}).get(container.defaultTimeOutMs());
		}

	}

}
