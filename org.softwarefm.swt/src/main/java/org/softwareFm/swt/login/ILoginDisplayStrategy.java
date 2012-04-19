/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.login;

public interface ILoginDisplayStrategy {

	void showLogin(String sessionSalt, String initialEmail);

	void showForgotPassword(String sessionSalt, String initialEmail);

	void showSignup(String sessionSalt, String initialEmail);

	void showChangePassword(String email);

	abstract public static class Utils {

		public static ILoginDisplayStrategy sysoutDisplayStrategy() {
			return new ILoginDisplayStrategy() {

				@Override
				public void showSignup(String sessionSalt, String initialEmail) {
					System.out.println("showSignUp: " + sessionSalt + ", " + initialEmail);
				}

				@Override
				public void showLogin(String sessionSalt, String initialEmail) {
					System.out.println("showLogin: " + sessionSalt + ", " + initialEmail);
				}

				@Override
				public void showForgotPassword(String sessionSalt, String initialEmail) {
					System.out.println("showForgotPassword: " + sessionSalt + ", " + initialEmail);
				}

				@Override
				public void showChangePassword(String email) {
					System.out.println("showChangePassword: " + email);

				}
			};
		}
	}

}