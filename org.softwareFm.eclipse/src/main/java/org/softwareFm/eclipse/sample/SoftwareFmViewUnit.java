package org.softwareFm.eclipse.sample;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.SoftwareFmDataComposite;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.eclipse.fixture.SoftwareFmFixture;
import org.softwareFm.jdtBinding.api.RippedResult;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.json.Json;

public class SoftwareFmViewUnit {

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		Swts.xUnit("View", root, "json", new ISituationListAndBuilder<SoftwareFmDataComposite>() {
			private SoftwareFmFixture softwareFmFixture;

			@Override
			public SoftwareFmDataComposite makeChild(Composite from) throws Exception {
				softwareFmFixture = new SoftwareFmFixture(from.getDisplay());
				SoftwareFmDataComposite composite = softwareFmFixture.makeComposite(from);
				return composite;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void selected(SoftwareFmDataComposite hasControl, String fileName, String text) throws Exception {
				Map<String, Object> map = Json.mapFromString(text);
				Map<String, String> rippedMap = (Map<String, String>) map.get("ripped");
				RippedResult result = rippedMap == null ? null : new RippedResult(rippedMap.get("hexDigest"), rippedMap.get("javaProject"), rippedMap.get("jarPath"), rippedMap.get("jarName"), rippedMap.get("javadoc"), rippedMap.get("source"), ICallback.Utils.<String> sysoutCallback(), ICallback.Utils.<String> sysoutCallback());
				softwareFmFixture.dataStore.setRawData("jar", result);
				softwareFmFixture.forceData(fileName, "jar", map);
				softwareFmFixture.forceData(fileName, "project", map);
				softwareFmFixture.forceData(fileName, "organisation", map);
			}
		});
	}

}
