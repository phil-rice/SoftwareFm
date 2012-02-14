package org.softwareFm.swt.explorer.internal;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.LoginConstants;

public interface IMySoftwareFmLoggedInStrategy {
	IUserReader userReader();

	void logout();

	void showMyData();

	void showMyGroups();


	List<String> displayProperties();

	public static class Utils {
		public static IMySoftwareFmLoggedInStrategy sysout(final Object... nameAndValues) {
			return new IMySoftwareFmLoggedInStrategy() {
				@Override
				public IUserReader userReader() {
					return IUserReader.Utils.mockReader(nameAndValues);
				}

				@Override
				public void showMyGroups() {
					System.out.println("Show My Groups");
				}

				@Override
				public void showMyData() {
					System.out.println("Show My Data");
				}

				@Override
				public void logout() {
					System.out.println("Logout");
				}

				@Override
				public List<String> displayProperties() {
					return Arrays.asList(LoginConstants.emailKey, LoginConstants.monikerKey);
				}
			};
		}
	}
}
