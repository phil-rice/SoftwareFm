package org.softwareFm.eclipse;

import java.util.concurrent.Future;

public interface IRequestGroupReportGeneration {

	Future<?> request(String groupId, String groupCryptoKey, String month);

}
