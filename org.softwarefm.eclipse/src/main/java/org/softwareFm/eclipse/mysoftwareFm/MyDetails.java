package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.ProjectFixture;
import org.softwareFm.eclipse.user.ProjectTimeGetterFixture;
import org.softwareFm.eclipse.user.UserMock;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.swt.Swts;

public class MyDetails implements IHasComposite {

	public static List<String> displayProperties = Arrays.asList(LoginConstants.emailKey, LoginConstants.monikerKey);

	static class MyProjectComposite extends Composite {

		private final Table userDetails;
		private final Table projectDetails;

		public MyProjectComposite(Composite parent, int style, String cryptoKey, IUser user, IProject project, IProjectTimeGetter timeGetter, Map<String, Object> userDetailMap) {
			super(parent, style);
			this.userDetails = new Table(this, SWT.FULL_SELECTION);
			userDetails.setHeaderVisible(false);
			for (int i = 0; i < 2; i++)
				new TableColumn(userDetails, SWT.NULL);
			for (String property : displayProperties) {
				TableItem item = new TableItem(userDetails, SWT.NULL);
				Object value = user.getUserProperty(userDetailMap, cryptoKey, property);
				item.setText(new String[] { property, Strings.nullSafeToString(value) });
			}

			Map<String, Map<String, Map<String, Integer>>> groupToArtifactToMonthToCount = Maps.newMap();
			Iterable<String> lastNMonths = timeGetter.lastNMonths(3);
			this.projectDetails = new Table(this, SWT.FULL_SELECTION);
			projectDetails.setHeaderVisible(true);
			new TableColumn(projectDetails, SWT.NULL).setText("Group ID");
			new TableColumn(projectDetails, SWT.NULL).setText("Artifact ID");
			for (String month : lastNMonths) {
				TableColumn column = new TableColumn(projectDetails, SWT.NULL);
				column.setText(month);
			}

			for (String month : lastNMonths) {
				Map<String, Map<String, List<Integer>>> monthDetails = project.getProjectDetails(userDetailMap, month);
				for (Entry<String, Map<String, List<Integer>>> groupEntry : monthDetails.entrySet())
					for (Entry<String, List<Integer>> artifactEntry : groupEntry.getValue().entrySet())
						Maps.addToMapOfMapOfMaps(groupToArtifactToMonthToCount, HashMap.class, groupEntry.getKey(), artifactEntry.getKey(), month, artifactEntry.getValue().size());
			}

			for (String groupId : Lists.sort(groupToArtifactToMonthToCount.keySet())) {
				Map<String, Map<String, Integer>> groupMap = groupToArtifactToMonthToCount.get(groupId);
				for (String artifactId : Lists.sort(groupMap.keySet())) {
					Map<String, Integer> artifactMap = groupMap.get(artifactId);
					TableItem item = new TableItem(projectDetails, SWT.FULL_SELECTION);
					item.setText(0, groupId);
					item.setText(1, artifactId);
					int index = 2;
					for (String month : lastNMonths) {
						Integer count = Maps.intFor(artifactMap, month);
						item.setText(index++, Integer.toString(count));
					}
				}
			}
			Swts.packTables(userDetails, projectDetails);
		}

	}

	private final MyProjectComposite content;

	public MyDetails(Composite parent, String cryptoKey, IUser user, IProject project, IProjectTimeGetter timeGetter, Map<String, Object> userDetailMap) {
		this.content = new MyProjectComposite(parent, SWT.NULL, cryptoKey, user, project, timeGetter, userDetailMap);
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithLastGrabingVertical(content);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public static void main(String[] args) {
		Swts.Show.display(MyDetails.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				String cryptoKey = Crypto.makeKey();
				Map<String, Object> userDetails = Maps.makeImmutableMap("some", "user details");
				UserMock user = new UserMock(cryptoKey, userDetails, LoginConstants.emailKey, "someEmail", LoginConstants.monikerKey, "someMoniker");
				ProjectFixture project = new ProjectFixture(userDetails);
				return new MyDetails(from, cryptoKey, user, project, new ProjectTimeGetterFixture(), userDetails).getComposite();
			}
		});
	}
}
