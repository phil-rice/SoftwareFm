package org.softwarefm.core.composite;

import org.eclipse.swt.widgets.Control;
import org.softwarefm.core.friends.ISocialUsageListenerWithValid;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.maps.ISimpleMap;

public class SocialUsageAdapter implements ISocialUsageListenerWithValid {

	private final Control marker;

	public SocialUsageAdapter(Control marker) {
		this.marker = marker;
	}
	
	@Override
	public void codeUsage(String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage) {
	}

	@Override
	public void artifactUsage(String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage) {
	}

	@Override
	public void noArtifactUsage() {
	}

	@Override
	public boolean isValid() {
		return !marker.isDisposed();
	}

}
