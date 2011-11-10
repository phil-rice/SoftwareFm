package org.softwareFm.eclipse.sample;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.display.SoftwareFmDataComposite;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.jdtBinding.api.IJavadocSourceMutator;
import org.softwareFm.jdtBinding.api.RippedResult;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.services.IServiceExecutor;

public class SoftwareFmViewUnit {

	public static final class SoftwareFmViewUnitBuilder implements ISituationListAndBuilder<SoftwareFmDataComposite> {
		private SoftwareFmFixture softwareFmFixture;
		private final IFunction1<SoftwareFmFixture, LargeButtonDefn[]> largeButtons;
		private SoftwareFmDataComposite softwareFmComposite;
		private final IServiceExecutor service;

		public SoftwareFmViewUnitBuilder(IFunction1<SoftwareFmFixture, LargeButtonDefn[]> largeButtons) {
			this.largeButtons = largeButtons;
			service = IServiceExecutor.Utils.defaultExecutor();
		}

		@Override
		public SoftwareFmDataComposite makeChild(Composite from) throws Exception {
			softwareFmFixture = new SoftwareFmFixture(from.getDisplay(), service);
			softwareFmComposite = softwareFmFixture.makeComposite(from, largeButtons.apply(softwareFmFixture));
			Swts.asyncExec(softwareFmComposite, new Runnable() {
				@Override
				public void run() {
					softwareFmComposite.browserComposite.processUrl(DisplayConstants.browserFeedType, "www.bbc.co.uk");
				}
			});
			return softwareFmComposite;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void selected(SoftwareFmDataComposite hasControl, String fileName, Object text) throws Exception {
			Map<String, Object> map = Json.mapFromString(text.toString());
			Map<String, String> rippedMap = (Map<String, String>) map.get("ripped");
			RippedResult result = rippedMap == null ? null : new RippedResult(rippedMap.get("hexDigest"), rippedMap.get("javaProject"), rippedMap.get("jarPath"), rippedMap.get("jarName"), rippedMap.get("javadoc"), rippedMap.get("source"), IJavadocSourceMutator.Utils.sysout("set javadoc in eclipse to : {0}"), IJavadocSourceMutator.Utils.sysout("set source in eclipse to: {0}"));
			softwareFmFixture.dataStore.setRawData(ConfigurationConstants.primaryEntity, result);
			softwareFmFixture.forceData(fileName, "jar", map);
			softwareFmFixture.forceData(fileName, "artifact", map);
			softwareFmFixture.forceData(fileName, "group", map);
			softwareFmFixture.editorFactory.cancel();
		}

		public void shutdown() {
			service.shutdown();
		}
	}

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		SoftwareFmViewUnitBuilder builder = new SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return from.allLargeButtons;
			}
		});
		try {
			Show.xUnit("View", root, "json", builder);
		} finally {
			builder.shutdown();
		}
	}

}
