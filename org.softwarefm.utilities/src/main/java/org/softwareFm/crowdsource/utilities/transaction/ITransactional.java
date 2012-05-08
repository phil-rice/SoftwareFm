/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.transaction;

public interface ITransactional {

	/**
	 * Commit is the place where the transactional persists the changes. <br />
	 * Commit <em>should not</em> throw any exceptions, but if it does, the remaining resources will commit, then they will all be rolledback. (actually I am not sure what is best here, so I went with the approach that allowed me to reason with it easiest)
	 */
	void commit();

	/** Rollback is called if the transaction fails, and the changes need to be "undone" */
	void rollback();
}