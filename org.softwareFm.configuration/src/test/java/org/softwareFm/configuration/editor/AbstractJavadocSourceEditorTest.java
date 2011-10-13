package org.softwareFm.configuration.editor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.configuration.displayers.JavadocSourceSummaryDisplayerFactory;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.DataGetterMock;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.display.displayer.CompressedText;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.impl.AbstractDisplayerEditorIntegrationTest;
import org.softwareFm.jdtBinding.api.IJavadocSourceMutator;
import org.softwareFm.jdtBinding.api.IJavadocSourceMutatorCallback;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.tests.IIntegrationTest;

public abstract class AbstractJavadocSourceEditorTest extends AbstractDisplayerEditorIntegrationTest<CompressedText, JavadocSourceEditor> implements IIntegrationTest {

	abstract protected DataGetterMock makeDataGetter(String eclipseValue, String softwareFmValue);

	abstract protected String getUrlTitleKey();

	abstract protected String getEclipseValueKey();

	abstract protected String getSoftwareFmValueKey();

	abstract protected String getMutatorKey();

	private final List<String> mutatorValues = Lists.newList();

	public void testDisplayerTitle() {
		ActionContext actionContext = makeActionContext(new DataGetterMock());
		IDisplayer displayer = displayerDefn.createDisplayer(shell, actionContext);
		checkTitle(displayer, "sfm", "ecl", "doesnt_match");
		checkTitle(displayer, "data", "data", "matches");
		checkTitle(displayer, "data", "", "copy_to_eclipse");
		checkTitle(displayer, "", "data", "notUrl");
		checkTitle(displayer, "", "http://data", "copy_to_sfm");
	}

	public void testEditorText() {
		createDisplayerAndEditor("ecl", "sfm");
		assertEquals("ecl", editor.getTxtEclipse().getText());
		assertEquals("sfm", editor.getTxtUrl().getText());
	}

	public void testCreatingEditorDoesntMutate() {
		createDisplayerAndEditor("ecl", "sfm");
		assertEquals(0, mutatorValues.size());
	}

	public void testInitialButtonStateWithBlanks() {
		createDisplayerAndEditor("", "");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithEclipseAndSoftwareFmNotMatching() {
		createDisplayerAndEditor("ecl", "sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithEclipseAndSoftwareFmMatching() {
		createDisplayerAndEditor("data", "data");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithEclipseAndSoftwareFmBlank() {
		createDisplayerAndEditor("", "");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithJustEclipse() {
		createDisplayerAndEditor("ecl", "");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithJustEclipseJar() {
		createDisplayerAndEditor("ecl.jar", "");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithJustSfm() {
		createDisplayerAndEditor("", "http://someAddress.jar");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testInitialButtonStateWithJustSfmHtml() {
		createDisplayerAndEditor("", "http://someAddress.html");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testButtonsWhenChangeToBlank() {
		createDisplayerAndEditor("ecl", "sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		editor.getTxtUrl().setText("");
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testButtonsWhenChangeToNewValue() {
		createDisplayerAndEditor("ecl", "sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		editor.getTxtUrl().setText("newvalue");
		assertTrue(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertTrue(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testButtonsWhenChangeToSfmValue() {
		createDisplayerAndEditor("ecl", "sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		editor.getTxtUrl().setText("newvalue");
		editor.getTxtUrl().setText("sfm");
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertTrue(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testButtonsWhenChangeToEclValue() {
		createDisplayerAndEditor("ecl", "sfm");
		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		editor.getTxtUrl().setText("ecl");
		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertTrue(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testCopyToEclipseButtonFromSfmValueMutatesValue() {
		createDisplayerAndEditor("ecl", "sfm");

		editor.getJavadocSourceButtons().copyToEclipseButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(Arrays.asList("sfm"), mutatorValues);
	}

	public void testCopyToEclipseButtonFromNonSfmValueMutatesValue() {
		createDisplayerAndEditor("ecl", "sfm");
		editor.getTxtUrl().setText("someOtherValue");

		editor.getJavadocSourceButtons().copyToEclipseButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(Arrays.asList("someOtherValue"), mutatorValues);
	}

	public void testButtonStateAFterCopyToEclipseFromSfmButton() {
		createDisplayerAndEditor("ecl", "sfm");

		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		javadocSourceButtons.copyToEclipseButton.notifyListeners(SWT.Selection, new Event());

		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testButtonStateAFterCopyToEclipseFromNoneSfmButton() {
		createDisplayerAndEditor("ecl", "sfm");
		editor.getTxtUrl().setText("someOtherValue");

		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		javadocSourceButtons.copyToEclipseButton.notifyListeners(SWT.Selection, new Event());

		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertTrue(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testCopyToBothButtonMutatesValue() {
		createDisplayerAndEditor("ecl", "sfm");
		editor.getTxtUrl().setText("someOtherValue");

		JavadocSourceButtons javadocSourceButtons = editor.getJavadocSourceButtons();
		javadocSourceButtons.copyToEclipseAndSoftwareFmButton.notifyListeners(SWT.Selection, new Event());

		assertFalse(javadocSourceButtons.copyToEclipseAndSfmEnabled);
		assertFalse(javadocSourceButtons.copyToEclipseEnabled);
		assertFalse(javadocSourceButtons.copyToSoftwareFmEnabled);
	}

	public void testButtonStateAfterCopyToBothButtonMutatesValue() {
		createDisplayerAndEditor("ecl", "sfm");
		editor.getTxtUrl().setText("someOtherValue");

		editor.getJavadocSourceButtons().copyToEclipseAndSoftwareFmButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(Arrays.asList("someOtherValue"), mutatorValues);
	}

	public void testUpdatesSfm() {
		fail("Need to write this");
	}

	private CompressedText createDisplayerAndEditor(String eclipseValue, String softwareFmValue) {
		ActionContext actionContext = makeActionContext(makeDataGetter(eclipseValue, softwareFmValue));
		IDisplayer displayer = displayerDefn.createDisplayer(shell, actionContext);
		displayerDefn.data(actionContext, displayerDefn, displayer, "entity", "url");
		clickOnEditor(displayer);
		return (CompressedText) displayer;
	}

	private void updateDisplayerAndClickEditor(String eclipseValue, String softwareFmValue, IDisplayer displayer) {
		ActionContext actionContext = makeActionContext(makeDataGetter(eclipseValue, softwareFmValue));
		displayerDefn.data(actionContext, displayerDefn, displayer, "entity", "url");
		clickOnEditor(displayer);
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
				ConfigurationConstants.urlJavadocTitle, "JavadocUrlTitle",//
				ConfigurationConstants.urlSourceTitle, "SourceUrlTitle",//
				ConfigurationConstants.buttonTestTitle, "ButtonTestTitle",//
				"button.eclipseNotUrl.title", "notUrl",//
				"button.matches.title", "matches",//
				DisplayConstants.buttonCancelsTitle, "cancelValue", //
				DisplayConstants.buttonOkTitle, "okValue", //
				"helpKey", "helpValue"));
	}

	@Override
	protected JavadocSourceEditor makeEditor() {
		return new JavadocSourceEditor(getUrlTitleKey(), getSoftwareFmValueKey(), getEclipseValueKey(), getMutatorKey());
	}

	@Override
	protected DisplayerDefn makeBaseDisplayDefn() {
		JavadocSourceSummaryDisplayerFactory displayerFactory = new JavadocSourceSummaryDisplayerFactory(getEclipseValueKey(), getSoftwareFmValueKey());
		return new DisplayerDefn(displayerFactory).title("Ttl").editor("someEditor").icon(ArtifactsAnchor.projectKey);
	}

}
