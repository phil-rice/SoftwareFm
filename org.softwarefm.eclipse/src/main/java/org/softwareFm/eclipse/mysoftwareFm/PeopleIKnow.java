package org.softwareFm.eclipse.mysoftwareFm;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.explorer.internal.UserData;

public class PeopleIKnow implements IHasComposite {

	static class PeopleIKnowComposite extends Composite {

		private final StyledText text;

		public PeopleIKnowComposite(Composite parent, int style) {
			super(parent, style);
			this.text = new StyledText(this, SWT.READ_ONLY | SWT.WRAP);
		}

		public void setSoftwareFmIds(Set<String> softwareFmIds) {
			text.setText(Strings.join(softwareFmIds, "\n"));
		}

	}

	private final PeopleIKnowComposite content;
	private final UserData userData;
	private final IUserMembershipReader membershipReader;
	private final IGroupsReader groupsReader;
	private final IProjectTimeGetter timeGetter;

	public PeopleIKnow(Composite parent, UserData userData, IUserMembershipReader membershipReader, IGroupsReader groupsReader, IProjectTimeGetter timeGetter) {
		this.userData = userData;
		this.membershipReader = membershipReader;
		this.groupsReader = groupsReader;
		this.timeGetter = timeGetter;
		this.content = new PeopleIKnowComposite(parent, SWT.NULL);
		content.setLayout(new FillLayout());

	}

	public void setData(String groupId, String artifactId) {
		Set<String> softwareFmIds = Sets.newSet();
		for (Map<String, Object> groupData : membershipReader.walkGroupsFor(userData.softwareFmId)) {
			String groupsId = (String) groupData.get(GroupConstants.groupCryptoKey);
			String groupsCrypto = (String) groupData.get(GroupConstants.groupIdKey);
			if (groupsId == null)
				throw new NullPointerException(groupsId);
			if (groupsCrypto == null)
				throw new NullPointerException(groupsCrypto);
			for (String month : timeGetter.lastNMonths(3)) {
				Map<String, Map<String, Map<String, List<Integer>>>> report = (Map) groupsReader.getUsageReport(groupId, groupsCrypto, month);
				for (Entry<String, Map<String, Map<String, List<Integer>>>> groupEntry : report.entrySet())
					if (groupId.equals(groupEntry.getKey()))
						for (Entry<String, Map<String, List<Integer>>> artifactEntry : groupEntry.getValue().entrySet())
							if (artifactId.equals(artifactEntry.getKey()))
								for (Entry<String, List<Integer>> e : artifactEntry.getValue().entrySet())
									softwareFmIds.add(e.getKey());
			}
		}
		content.setSoftwareFmIds(softwareFmIds);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

}
