/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.server.processors.IGenerateUsageReportGenerator;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.internal.AbstractCommandProcessor;

public class GenerateGroupUsageProcessor extends AbstractCommandProcessor {

	private final IGenerateUsageReportGenerator generateUsageReportGenerator;
	private final IGroups groups;

	public GenerateGroupUsageProcessor(IGitOperations gitOperations, IGenerateUsageReportGenerator generateUsageReportGenerator, IGroups groups) {
		super(gitOperations, CommonConstants.POST, GroupConstants.generateGroupReportPrefix);
		this.generateUsageReportGenerator = generateUsageReportGenerator;
		this.groups = groups;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		checkForParameter(parameters, GroupConstants.groupIdKey, GroupConstants.monthKey, GroupConstants.monthKey);
		String groupId = (String) parameters.get(GroupConstants.groupIdKey);
		String groupCryptoKey = (String) parameters.get(GroupConstants.groupCryptoKey);
		String month = (String) parameters.get(GroupConstants.monthKey);
		Map<String, Map<String, Map<String, List<Integer>>>> report = generateUsageReportGenerator.generateReport(groupId, groupCryptoKey, month);
		groups.setReport(groupId, groupCryptoKey, month, (Map) report);
		return IProcessResult.Utils.processString(report.toString());
	}

}