package org.softwareFm.crowdsource.constants;

import java.util.List;

import org.softwareFm.crowdsource.api.newGit.SourceType;
import org.softwareFm.crowdsource.utilities.collections.Lists;

public class GitConstants {

	public final List<String> me = Lists.immutable(SourceType.me);
	public final List<String> groups = Lists.immutable(SourceType.myGroups);
	public final List<String> global = Lists.immutable(SourceType.global);
	public final List<String> all = Lists.immutable(SourceType.me, SourceType.myGroups, SourceType.global);

}
