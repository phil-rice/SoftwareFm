package org.softwareFm.eclipse.sample;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.display.SoftwareFmDataComposite;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.jdtBinding.api.RippedResult;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.json.Json;

public class SoftwareFmViewUnit {

	public static final class SoftwareFmViewUnitBuilder implements ISituationListAndBuilder<SoftwareFmDataComposite> {
		private SoftwareFmFixture softwareFmFixture;
		private final IFunction1<SoftwareFmFixture, LargeButtonDefn[]> largeButtons;

	public 	SoftwareFmViewUnitBuilder(IFunction1<SoftwareFmFixture, LargeButtonDefn[]> largeButtons) {
		this.largeButtons = largeButtons;
		}

		@Override
		public SoftwareFmDataComposite makeChild(Composite from) throws Exception {
			softwareFmFixture = new SoftwareFmFixture(from.getDisplay());
			final SoftwareFmDataComposite composite = softwareFmFixture.makeComposite(from, largeButtons.apply(softwareFmFixture));
			Swts.asyncExec(composite, new Runnable() {
				@Override
				public void run() {
					composite.browserComposite.processUrl(DisplayConstants.browserFeedType, "www.bbc.co.uk");
				}
			});
			return composite;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void selected(SoftwareFmDataComposite hasControl, String fileName, String text) throws Exception {
			Map<String, Object> map = Json.mapFromString(text);
			Map<String, String> rippedMap = (Map<String, String>) map.get("ripped");
			RippedResult result = rippedMap == null ? null : new RippedResult(rippedMap.get("hexDigest"), rippedMap.get("javaProject"), rippedMap.get("jarPath"), rippedMap.get("jarName"), rippedMap.get("javadoc"), rippedMap.get("source"), ICallback.Utils.<String> sysoutCallback(), ICallback.Utils.<String> sysoutCallback());
			softwareFmFixture.dataStore.setRawData("jar", result);
			softwareFmFixture.forceData(fileName, "artifact", map);
			softwareFmFixture.forceData(fileName, "group", map);
		}
	}

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		Swts.xUnit("View", root, "json", new SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return from.allLargeButtons;
			}
		}));
	}

}
