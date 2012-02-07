package org.softwareFm.server.processors;

import java.util.List;
import java.util.Map;

public interface IGenerateUsageReportGenerator {

	/** groupId -> artifactId -> userName -> list of days */
	Map<String, Map<String, Map<String, List<Integer>>>> generateReport(String groupId, String groupCryptoKey, String month);
}
