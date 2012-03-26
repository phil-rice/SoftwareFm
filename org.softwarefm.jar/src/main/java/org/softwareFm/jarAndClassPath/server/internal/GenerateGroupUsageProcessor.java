/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.jarAndClassPath.api.IGenerateUsageReportGenerator;

public class GenerateGroupUsageProcessor extends AbstractCallProcessor {

	private final IContainer readWriteApi;

	public GenerateGroupUsageProcessor(IContainer readWriteApi) {
		super(CommonConstants.POST, GroupConstants.generateGroupReportPrefix);
		this.readWriteApi = readWriteApi;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, GroupConstants.groupIdKey, GroupConstants.monthKey, GroupConstants.monthKey);
		final AtomicReference<String> report = new AtomicReference<String>();
		readWriteApi.access(IGroups.class, IGenerateUsageReportGenerator.class, new ICallback2<IGroups, IGenerateUsageReportGenerator>() {
			@Override
			public void process(final IGroups groups, IGenerateUsageReportGenerator generateUsageReportGenerator) throws Exception {
				String groupId = (String) parameters.get(GroupConstants.groupIdKey);
				String groupCryptoKey = (String) parameters.get(GroupConstants.groupCryptoKey);
				String month = (String) parameters.get(GroupConstants.monthKey);
				if (logger.isDebugEnabled()) {
					String message = MessageFormat.format("GroupId: {0},  Month: {1}", groupId, month);
					logger.debug(message);
					System.out.println(message);
				}
				Map<String, Map<String, Map<String, List<Integer>>>> reportMap = generateUsageReportGenerator.generateReport(groupId, groupCryptoKey, month);
				groups.setReport(groupId, groupCryptoKey, month, (Map) reportMap);
				if (logger.isDebugEnabled()) {
					String message = MessageFormat.format("Result for GroupId: {0},  Month{1} is {2}", groupId, month, reportMap);
					logger.debug(message);
					System.out.println(message);
				}
				report.set(Strings.nullSafeToString(reportMap));
			}
		});
		return IProcessResult.Utils.processString(report.get());
	}

}