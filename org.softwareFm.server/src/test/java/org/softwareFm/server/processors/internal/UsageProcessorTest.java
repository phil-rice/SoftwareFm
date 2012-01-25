package org.softwareFm.server.processors.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.easymock.EasyMock;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.user.IUser;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.runnable.Callables;

public class UsageProcessorTest extends AbstractProcessCallTest<UsageProcessor> {

	private IUser user;
	private final Map<String, Object> userAndProjectDetailMap = Maps.stringObjectMap("user", "detailMap");
	private final Map<String, Object> initialProjectDetail = Maps.stringObjectMap("initial", "projectDetail");
	private final Map<String, Object> modifiedProjectDetails = Maps.stringObjectMap("modified", "projectDetail");

	public void testIgnoresGetAndNonePrefix() {
		EasyMock.replay(user);

		checkIgnoresNonePosts();
		checkIgnores(ServerConstants.POST, "/command");
		EasyMock.verify(user);
	}

	public void testAddsToProjectInfoAndSavesIfHasChanged() {
		EasyMock.expect(user.getProjectDetails(userAndProjectDetailMap, "someMonth")).andReturn(initialProjectDetail);
		EasyMock.expect(user.addProjectDetails(userAndProjectDetailMap, "someMonth", 3, initialProjectDetail)).andReturn(modifiedProjectDetails);
		user.saveProjectDetails(userAndProjectDetailMap, "someMonth", modifiedProjectDetails);

		EasyMock.replay(user);

		processor.execute("/" + ServerConstants.usagePrefix, userAndProjectDetailMap);
		EasyMock.verify(user);
	}

	public void testAddsToProjectInfoAndDoesntSaveIfNotChanged() {
		EasyMock.expect(user.getProjectDetails(userAndProjectDetailMap, "someMonth")).andReturn(initialProjectDetail);
		EasyMock.expect(user.addProjectDetails(userAndProjectDetailMap, "someMonth", 3, initialProjectDetail)).andReturn(null);
		EasyMock.replay(user);

		processor.execute("/" + ServerConstants.usagePrefix, userAndProjectDetailMap);
		EasyMock.verify(user);
	}

	@Override
	protected UsageProcessor makeProcessor() {
		user = EasyMock.createMock(IUser.class);
		Callable<String> monthGetter = Callables.value("someMonth");
		Callable<Integer> dayGetter = Callables.value(3);
		return new UsageProcessor(localGitServer, user, monthGetter, dayGetter);
	}
}
