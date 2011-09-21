package org.softwareFm.eclipse.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.softwareFm.display.Swts;
import org.softwareFm.eclipse.fixture.SoftwareFmFixture;
import org.softwareFm.jdtBinding.api.RippedResult;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.json.Json;

public class SoftwareFmDisplaySamples {

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		Swts.display("Software FM Displays", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite parent = new Composite(from, SWT.NULL);
				GridLayout layout = new GridLayout(2, true);
				parent.setLayout(layout);

				Composite leftSide = new Composite(parent, SWT.NULL);
				leftSide.setLayout(new GridLayout(1, false));
				leftSide.setLayoutData(makeGridLayoutData());
				final List situationlist = makeSituationlist(leftSide);
				final StyledText styledText = new StyledText(leftSide, SWT.BORDER);
				styledText.setLayoutData(makeGridLayoutData());

				final SoftwareFmFixture softwareFmFixture = new SoftwareFmFixture(parent.getDisplay());
				softwareFmFixture.makeComposite(parent).getComposite().setLayoutData(makeGridLayoutData());
				situationlist.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							int index = situationlist.getSelectionIndex();
							if (index == -1) {
								setText(null, "");
							} else {
								String fileName = situationlist.getItem(index);
								File file = new File(root, fileName);
								String text = Files.getText(new FileReader(file));
								setText(fileName, text);
							}
						} catch (FileNotFoundException e1) {
							throw WrappedException.wrap(e1);
						}
					}

					private void setText(String url, String text) {
						styledText.setText(text);
						Map<String, Object> map = Json.mapFromString(text);
						@SuppressWarnings("unchecked")
						Map<String,String> rippedMap = (Map<String, String>) map.get("ripped");
						RippedResult result = rippedMap == null?null:new RippedResult(rippedMap.get("hexDigest"), rippedMap.get("jarPath"), rippedMap.get("jarName"), rippedMap.get("javadoc"), rippedMap.get("source"), ICallback.Utils.<String>sysoutCallback(), ICallback.Utils.<String>sysoutCallback());
						softwareFmFixture.dataStore.setRawData(result);
						softwareFmFixture.forceData(url, "jar", map);
						softwareFmFixture.forceData(url, "project", map);
						softwareFmFixture.forceData(url, "organisation", map);
					}
				});

				return parent;
			}

			private List makeSituationlist(Composite parent) {
				List situationList = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
				GridData data = makeGridLayoutData();
				situationList.setLayoutData(data);

				for (File file : Files.walkChildrenOf(root, Files.extensionFilter("json")))
					situationList.add(file.getName());
				return situationList;
			}

			private GridData makeGridLayoutData() {
				GridData data = new GridData();
				data.verticalAlignment = GridData.FILL;
				data.horizontalAlignment = GridData.FILL;
				data.grabExcessHorizontalSpace = true;
				data.grabExcessVerticalSpace = true;
				return data;
			}
		});
	}
}
