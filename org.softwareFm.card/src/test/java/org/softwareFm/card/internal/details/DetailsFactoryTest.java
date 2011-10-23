package org.softwareFm.card.internal.details;

import java.util.Collections;

public class DetailsFactoryTest extends AbstractDetailTest {
	
	public void testWithNoAddersGetNull() {
		DetailFactory detailFactory = new DetailFactory(
				Collections.<IDetailAdder> emptyList());
		checkGetNull(detailFactory, listValue);
	}

}
