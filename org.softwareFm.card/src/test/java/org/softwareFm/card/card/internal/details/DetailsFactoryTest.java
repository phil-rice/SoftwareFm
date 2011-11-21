package org.softwareFm.card.card.internal.details;

import java.util.Collections;

import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.internal.DetailFactory;

public class DetailsFactoryTest extends AbstractDetailTest {

	public void testWithNoAddersGetNull() {
		DetailFactory detailFactory = new DetailFactory(Collections.<IDetailAdder> emptyList());
		checkGetNull(detailFactory, mapValue);
	}

}
