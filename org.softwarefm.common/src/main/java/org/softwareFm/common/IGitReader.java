package org.softwareFm.common;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.maps.IHasCache;

public interface IGitReader extends IHasCache{
	/** Where is the root of this tree of repositories located */
	File getRoot();

	/** loads a file as a string */
	String getFileAsString(IFileDescription fileDescription);

	/** loads a file as a map */
	Map<String, Object> getFile(IFileDescription fileDescription);

	/** loads a file as a list of maps. Each line is a map, and optionally encrypted */
	List<Map<String, Object>> getFileAsListOfMaps(IFileDescription fileDescription);

	/** creates a map which is the aggregate of the file and it's descendants. The descendants are in sub directories with the same crypto key and name */
	Map<String, Object> getFileAndDescendants(IFileDescription fileDescription);


}
