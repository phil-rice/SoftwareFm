/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import org.eclipse.swt.SWT;
import org.junit.Test;
import org.softwareFm.swt.explorer.internal.MasterDetailSocial;
import org.softwareFm.swt.swt.SwtTest;

public class MasterDetailSocialTest extends SwtTest {

	private MasterDetailSocial masterDetailSocial;

	@Test
	public void testConstructorStartsWithEverythingVisible() {
		assertEquals(null, masterDetailSocial.content.getMaximizedControl());
		assertEquals(null, masterDetailSocial.content.detailSocial.getMaximizedControl());
	}


	public void testHideShowSocialAdjustsDetailContentGetMaximizedControl() {
		masterDetailSocial.hideSocial();
		assertEquals(masterDetailSocial.content.detail, masterDetailSocial.content.detailSocial.getMaximizedControl());
		masterDetailSocial.showSocial();
		assertEquals(null, masterDetailSocial.content.detailSocial.getMaximizedControl());
	}

	public void testHideShowSocialCausesLayout() {
		int initialLayoutCount = masterDetailSocial.content.layoutCount;
		int initialDetailSocialLayoutCount = masterDetailSocial.content.detailSocialLayoutCount;
		masterDetailSocial.hideSocial();
		assertEquals(initialDetailSocialLayoutCount + 1, masterDetailSocial.content.detailSocialLayoutCount);
		masterDetailSocial.showSocial();
		assertEquals(initialDetailSocialLayoutCount + 2, masterDetailSocial.content.detailSocialLayoutCount);
		assertEquals(initialLayoutCount, masterDetailSocial.content.layoutCount);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
	}
	@Override
	protected void tearDown() throws Exception {
		masterDetailSocial.dispose();
		super.tearDown();
	}
}