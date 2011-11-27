package org.softwarefm.collections.explorer.internal;

import org.eclipse.swt.SWT;
import org.junit.Test;
import org.softwareFm.display.swt.SwtIntegrationTest;

public class MasterDetailSocialTest extends SwtIntegrationTest {

	private MasterDetailSocial masterDetailSocial;

	@Test
	public void testConstructorStartsWithEverythingVisible() {
		assertEquals(null, masterDetailSocial.content.getMaximizedControl());
		assertEquals(null, masterDetailSocial.content.detailSocial.getMaximizedControl());
	}

	public void testHideShowMasterAdjustContentGetMaximizedControl() {
		masterDetailSocial.hideMaster();
		assertEquals(masterDetailSocial.content.detailSocial, masterDetailSocial.content.getMaximizedControl());
		masterDetailSocial.showMaster();
		assertEquals(null, masterDetailSocial.content.getMaximizedControl());
	}

	public void testHideShowSocialAdjustsDetailContentGetMaximizedControl() {
		masterDetailSocial.hideSocial();
		assertEquals(masterDetailSocial.content.detail, masterDetailSocial.content.detailSocial.getMaximizedControl());
		masterDetailSocial.showSocial();
		assertEquals(null, masterDetailSocial.content.detailSocial.getMaximizedControl());
	}
	
	public void testHideShowMasterCausesLayout(){
		int initialCount = masterDetailSocial.content.layoutCount;
		masterDetailSocial.hideMaster();
		assertEquals(initialCount+1, masterDetailSocial.content.layoutCount);
		masterDetailSocial.showMaster();
		assertEquals(initialCount+2, masterDetailSocial.content.layoutCount);
	}
	
	public void testHideShowSocialCausesLayout(){
		int initialLayoutCount = masterDetailSocial.content.layoutCount;
		int initialDetailSocialLayoutCount = masterDetailSocial.content.detailSocialLayoutCount;
		masterDetailSocial.hideSocial();
		assertEquals(initialDetailSocialLayoutCount+1, masterDetailSocial.content.detailSocialLayoutCount);
		masterDetailSocial.showSocial();
		assertEquals(initialDetailSocialLayoutCount+2, masterDetailSocial.content.detailSocialLayoutCount);
		assertEquals(initialLayoutCount, masterDetailSocial.content.layoutCount);
	}
	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
	}
}
