package org.softwareFm.configuration.editor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.configuration.displayers.JavadocSourceSummaryDisplayerFactory;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.data.DataGetterMock;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.display.displayer.CompressedText;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.display.impl.AbstractDisplayerEditorIntegrationTest;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.jdtBinding.api.IJavadocSourceMutator;
import org.softwareFm.jdtBinding.api.IJavadocSourceMutatorCallback;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.tests.IIntegrationTest;

public abstract class AbstractJavadocSourceEditorTest extends AbstractDisplayerEditorIntegrationTest<CompressedText, JavadocSourceEditor> implements IIntegrationTest {

	abstract protected DataGetterMock makeDataGetter(String eclipseValue, String softwareFmValue);

	abstract protected String getUrlTitleKey();

	abstract protected String getKeyForUpdateing();

	abstract protected String getEclipseValueKey();

	abstract protected String getSoftwareFmValueKey();

	abstract protected String getMutatorKey();

	private final List<String> mutatorValues = Lists.newList();
	private final List<String> entities = Lists.newList();
	private final List<String> urls = Lists.newList();
	private final List<Map<String, Object>> datas = Lists.newList();

	public void testDisplayerTitle() {
		ActionContext actionContext = makeActionContext(new DataGetterMock());
		IDisplayer displayer = displayerDefn.createDisplayer(shell, actionContext);
		checkTitle(displayer, "http://sfm", "http://ecl", "doesnt_match");
		checkTitle(displayer, "data", "data", "matches");
		checkTitle(displayer, "data", "", "copy_to_eclipse");
		checkTitle(displayer, "", "data", "notUrl");
		checkTitle(displayer, "", "http://data", "copy_to_sfm");
	}

	public void testEditorText() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		assertEquals("http://ecl", editor.getTxtEclipse().getText());
		assertEquals("http://sfm", editor.getTxtUrl().getText());
	}

	public void testCreatingEditorDoesntMutate() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		assertEquals(0, mutatorValues.size());
	}

	public void testNoCopyButtonEnabledIfNotLegalUrl() {
		checkNoCopyButtonEnabled("", "", "");
		checkNoCopyButtonEnabled("", "http://something", "");
		checkNoCopyButtonEnabled("http://something", "http://something", "");
		checkNoCopyButtonEnabled("http://something", "", "");

		checkNoCopyButtonEnabled("", "", "notlegal");
		checkNoCopyButtonEnabled("", "http://something", "notlegal");
		checkNoCopyButtonEnabled("http://something", "http://something", "notlegal");
		checkNoCopyButtonEnabled("http://something", "", "notlegal");
	}

	private void checkNoCopyButtonEnabled(String eclipse, String softwareFm, String url) {
		createDisplayerAndEditor(eclipse, softwareFm);
		editor.getTxtUrl().setText(url);
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);

	}

	public void testInitialButtonStateWithBlanks() {
		createDisplayerAndEditor("", "");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithEclipseAndSoftwareFmNotMatching() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithEclipseAndSoftwareFmMatching() {
		createDisplayerAndEditor("data", "data");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithEclipseAndSoftwareFmBlank() {
		createDisplayerAndEditor("", "");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithJustEclipse() {
		createDisplayerAndEditor("http://ecl", "");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithJustEclipseJar() {
		createDisplayerAndEditor("ecl.jar", "");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithJustEclipseHtml() {
		createDisplayerAndEditor("http://ecl.html", "");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertTrue(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithEclipseHtmlAndSfm() {
		createDisplayerAndEditor("http://ecl.html", "http://sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithJustSfm() {
		createDisplayerAndEditor("", "http://someAddress.jar");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithJustSfmHtml() {
		createDisplayerAndEditor("", "http://someAddress.html");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testButtonsWhenChangeToBlank() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		editor.getTxtUrl().setText("");
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testButtonsWhenChangeToNewValue() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		editor.getTxtUrl().setText("http://newvalue");
		assertTrue(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertTrue(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testButtonsWhenChangeToSfmValue() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		editor.getTxtUrl().setText("http://newvalue");
		editor.getTxtUrl().setText("http://sfm");
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testButtonsWhenChangeToEclValue() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		editor.getTxtUrl().setText("http://ecl");
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertTrue(javadocSourceButtons.copyToSoftwareFmEnabled);
		assertFalse(javadocSourceButtons.copyEclipseToSoftwareFmEnabled);
	}

	public void testCopyToEclipseButtonFromSfmValueMutatesValue() {
		createDisplayerAndEditor("http://ecl", "http://sfm");

		editor.getJavadocSourceButtons().copyToEclipseButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(Arrays.asList("http://sfm"), mutatorValues);
		checkNothingSentToSoftwareFm();
	}

	public void testCopyToEclipseButtonFromNonSfmValueMutatesValue() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		editor.getTxtUrl().setText("http://someOtherValue");

		editor.getJavadocSourceButtons().copyToEclipseButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(Arrays.asList("http://someOtherValue"), mutatorValues);
		checkNothingSentToSoftwareFm();
	}

	public void testButtonStateAFterCopyToEclipseFromSfmButton() {
		createDisplayerAndEditor("http://ecl", "http://sfm");

		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		javadocSourceButtons.copyToEclipseButton.notifyListeners(SWT.Selection, new Event());
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());

		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		checkNothingSentToSoftwareFm();
	}

	public void testButtonStateAFterCopyToEclipseFromNoneSfmButton() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		editor.getTxtUrl().setText("http://someOtherValue");

		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		javadocSourceButtons.copyToEclipseButton.notifyListeners(SWT.Selection, new Event());
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());

		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
		checkNothingSentToSoftwareFm();
	}

	public void testCopyToBothButtonMutatesValue() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		editor.getTxtUrl().setText("http://someOtherValue");

		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		javadocSourceButtons.copyToEclipseAndSoftwareFmButton.notifyListeners(SWT.Selection, new Event());
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testButtonStateAfterCopyToBothButtonMutatesValue() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		editor.getTxtUrl().setText("http://someOtherValue");

		editor.getJavadocSourceButtons().copyToEclipseAndSoftwareFmButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(Arrays.asList("http://someOtherValue"), mutatorValues);
	}

	public void testCopyToSoftwareFmButtonUpdatesSfm() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		editor.getTxtUrl().setText("http://someOtherValue");

		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		javadocSourceButtons.copyToSoftwareFmButton.notifyListeners(SWT.Selection, new Event());
		checkOneThingSentToSoftwareFm("UrlForjar", ConfigurationConstants.entityForJavadocSource, Maps.<String, Object> makeMap(getKeyForUpdateing(), "http://someOtherValue"));
	}

	public void testCopyToBothButtonUpdatesSfm() {
		createDisplayerAndEditor("http://ecl", "http://sfm");
		editor.getTxtUrl().setText("http://someOtherValue");

		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		javadocSourceButtons.copyToEclipseAndSoftwareFmButton.notifyListeners(SWT.Selection, new Event());
		checkOneThingSentToSoftwareFm("UrlForjar", ConfigurationConstants.entityForJavadocSource, Maps.<String, Object> makeMap(getKeyForUpdateing(), "http://someOtherValue"));
	}

	private CompressedText createDisplayerAndEditor(String eclipseValue, String softwareFmValue) {
		ActionContext actionContext = makeActionContext(makeDataGetter(eclipseValue, softwareFmValue), new IUpdateStore() {
			@Override
			public void update(String entity, String url, Map<String, Object> data) {
				entities.add(entity);
				urls.add(url);
				datas.add(data);
			}

			@Override
			public void update(ActionData actionData, String key, Object newValue) {
				throw new UnsupportedOperationException();
			}
		});
		IDisplayer displayer = displayerDefn.createDisplayer(shell, actionContext);
		displayerDefn.data(actionContext, displayerDefn, displayer, "entity", "url");
		clickOnEditor(displayer);
		return (CompressedText) displayer;
	}

	private void checkTitle(IDisplayer displayer, String softwareFmValue, String eclipseValue, String expectedText) {
		ActionContext actionContext = makeActionContext(makeDataGetter(eclipseValue, softwareFmValue));
		displayerDefn.data(actionContext, displayerDefn, displayer, "entity", "url");
		CompressedText text = (CompressedText) displayer;
		assertEquals(expectedText, text.getText());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mutatorValues.clear();
		entities.clear();
		urls.clear();
		datas.clear();
	}

	private void checkNothingSentToSoftwareFm() {
		assertEquals(0, entities.size());
	}

	@SuppressWarnings("unchecked")
	private void checkOneThingSentToSoftwareFm(String url, String entity, Map<String, Object> data) {
		assertEquals(Arrays.asList(url), urls);
		assertEquals(Arrays.asList(entity), entities);
		assertEquals(Arrays.asList(data), datas);
	}

	@Override
	protected void tearDown() throws Exception {
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
		super.tearDown();
	}

	protected IJavadocSourceMutator makeMutator() {
		return new IJavadocSourceMutator() {
			@Override
			public void setNewValue(String newValue, IJavadocSourceMutatorCallback whenComplete) throws Exception {
				mutatorValues.add(newValue);
				whenComplete.process(newValue, newValue);
			}
		};

	}

	@Override
	protected IResourceGetter makeResources() {
		return IResourceGetter.Utils.noResources().with(new ResourceGetterMock(//
				"dialog.eclipseValue.title", "eclipse value",//
				"dialog.softwareFmValue.title", "software fm value",//
				"button.noData.title", "no_data",//
				"button.doesntMatches.title", "doesnt_match",//
				ConfigurationConstants.buttonCopyToEclipseTitle, "copy_to_eclipse",//
				ConfigurationConstants.buttonCopyToSoftwareFmTitle, "copy_to_sfm",//
				ConfigurationConstants.buttonCopyToBothTitle, "copy_to_both",//
				ConfigurationConstants.buttonCopyEclipseToSoftwareFmTitle, "e->sfm",//
				ConfigurationConstants.urlJavadocTitle, "JavadocUrlTitle",//
				ConfigurationConstants.urlSourceTitle, "SourceUrlTitle",//
				ConfigurationConstants.buttonTestTitle, "ButtonTestTitle",//
				ConfigurationConstants.settingEclipseValue, "SettingEclipseValue",//
				ConfigurationConstants.setEclipseValue, "SetEclipseValue",//
				"button.eclipseNotUrl.title", "notUrl",//
				"button.matches.title", "matches",//
				DisplayConstants.buttonCancelsTitle, "cancelValue", //
				DisplayConstants.buttonOkTitle, "okValue", //
				"helpKey", "helpValue"));
	}

	@Override
	protected JavadocSourceEditor makeEditor() {
		return new JavadocSourceEditor(getUrlTitleKey(), getSoftwareFmValueKey(), getEclipseValueKey(), getMutatorKey(), getKeyForUpdateing());
	}

	@Override
	protected DisplayerDefn makeBaseDisplayDefn() {
		JavadocSourceSummaryDisplayerFactory displayerFactory = new JavadocSourceSummaryDisplayerFactory(getEclipseValueKey(), getSoftwareFmValueKey());
		return new DisplayerDefn(displayerFactory).title("Ttl").editor("someEditor").icon(ArtifactsAnchor.projectKey);
	}

}
