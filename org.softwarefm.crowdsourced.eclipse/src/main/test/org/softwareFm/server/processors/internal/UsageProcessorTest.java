package org.softwareFm.server.processors.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.easymock.EasyMock;
import org.softwareFm.crowdsourced.softwarefm.SoftwareFmConstants;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.user.IProject;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.runnable.Callables;

public class UsageProcessorTest extends AbstractProcessCallTest<UsageProcessor> {

	private IProject project;
	private final Map<String, Object> userAndProjectDetailMap = Maps.stringObjectMap("user", "detailMap");
	private final Map<String, Object> initialProjectDetail = Maps.stringObjectMap("initial", "projectDetail");
	private final Map<String, Object> modifiedProjectDetails = Maps.stringObjectMap("modified", "projectDetail");

	public void testIgnoresGetAndNonePrefix() {
		EasyMock.replay(project);

		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.POST, "/command");
		EasyMock.verify(project);
	}

	public void testAddsToProjectInfoAndSavesIfHasChanged() {
		EasyMock.expect(project.getProjectDetails(userAndProjectDetailMap, "someMonth")).andReturn(initialProjectDetail);
		EasyMock.expect(project.addProjectDetails(userAndProjectDetailMap, "someMonth", 3, initialProjectDetail)).andReturn(modifiedProjectDetails);
		project.saveProjectDetails(userAndProjectDetailMap, "someMonth", modifiedProjectDetails);

		EasyMock.replay(project);

		processor.execute("/" + SoftwareFmConstants.usagePrefix, userAndProjectDetailMap);
		EasyMock.verify(project);
	}

	public void testAddsToProjectInfoAndDoesntSaveIfNotChanged() {
		EasyMock.expect(project.getProjectDetails(userAndProjectDetailMap, "someMonth")).andReturn(initialProjectDetail);
		EasyMock.expect(project.addProjectDetails(userAndProjectDetailMap, "someMonth", 3, initialProjectDetail)).andReturn(null);
		EasyMock.replay(project);

		processor.execute("/" + SoftwareFmConstants.usagePrefix, userAndProjectDetailMap);
		EasyMock.verify(project);
	}

	@Override
	protected UsageProcessor makeProcessor() {
		project = EasyMock.createMock(IProject.class);
		Callable<String> monthGetter = Callables.value("someMonth");
		Callable<Integer> dayGetter = Callables.value(3);
		return new UsageProcessor(localOperations, project, monthGetter, dayGetter);
	}
}
