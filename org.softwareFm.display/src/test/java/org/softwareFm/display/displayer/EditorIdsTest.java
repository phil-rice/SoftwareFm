package org.softwareFm.display.displayer;

import junit.framework.TestCase;

public class EditorIdsTest extends TestCase{

	public void testNonData() {
		checkNoneData("a.b.c");
	}
	
	public void testWIthNull(){
		RippedEditorId rip = EditorIds.rip(null);
		assertFalse(rip.isData());
		assertFalse(rip.isRaw());
		assertEquals(null, rip.entity);
		assertEquals(null, rip.key);
	}
	
	public void testData(){
		checkData("data.entity.key", "entity", "key");
		checkData("data.ent.k", "ent", "k");
		checkData("data.ent.k.1", "ent", "k.1");
	}

	public void testRaw(){
		checkRawData("data.raw.entity.key", "entity", "key");
		checkRawData("data.raw.ent.k", "ent", "k");
		checkRawData("data.raw.ent.k.1", "ent", "k.1");
	}

	private void checkRawData(String editorId,String entity, String key) {
		RippedEditorId rip = EditorIds.rip(editorId);
		assertTrue(rip.isData());
		assertTrue(rip.isRaw());
		assertEquals(entity, rip.entity);
		assertEquals(key, rip.key);
		
	}

	private void checkData(String editorId,String entity, String key) {
		RippedEditorId rip = EditorIds.rip(editorId);
		assertTrue(rip.isData());
		assertFalse(rip.isRaw());
		assertEquals(entity, rip.entity);
		assertEquals(key, rip.key);
		
	}

	private void checkNoneData(String editorId) {
		RippedEditorId rip = EditorIds.rip(editorId);
		assertFalse(rip.isData());
		assertFalse(rip.isRaw());
		assertNull(rip.entity);
		assertNull(rip.key);
		
	}

}
