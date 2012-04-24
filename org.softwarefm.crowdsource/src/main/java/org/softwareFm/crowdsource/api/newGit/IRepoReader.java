package org.softwareFm.crowdsource.api.newGit;

import java.util.Map;

import org.softwareFm.crowdsource.api.newGit.internal.Sources;

/**
 * This is effectively a mixin interface. IGitDataPrim is the methods needed by the implementor, IGitData are the helper methods that make it easier to use<br />
 * IGitData provides access to the files, represented with each file as a list of maps.
 */
public interface IRepoReader {

	/** Start from offset, returns empty iterator if offset is too big */
	Iterable<SourcedMap> read(Sources sources, int offset);

	/** Start from offset, returns empty iterator if offset is too big */
	Iterable<SourcedMap> read(ISingleSource source, int offset);

	int countLines(ISingleSource source);

	Map<ISingleSource, Integer> countLines(Sources sources);

}
