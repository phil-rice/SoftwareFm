package org.softwareFm.configuration.editor;

import org.softwareFm.configuration.displayers.JavadocSourceSummaryDisplayerFactory;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.data.DataGetterMock;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.display.displayer.CompressedText;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.impl.AbstractDisplayerEditorIntegrationTest;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.tests.IIntegrationTest;

public abstract class AbstractJavadocSourceEditorTest extends AbstractDisplayerEditorIntegrationTest<CompressedText, JavadocSourceEditor> implements IIntegrationTest {

	abstract protected DataGetterMock makeDataGetter(String eclipseValue, String softwareFmValue);

	abstract protected String getEclipseValueKey();

	abstract protected String getSoftwareFmValueKey();

	abstract protected String getMutatorKey();

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
		assertEquals("sfm", editor.getTxtSoftwareFm().getText());
	}

	public void testOkState() {
		 createDisplayerAndEditor("ecl", "sfm");
		 assertFalse(editor.getOkCancel().isOkEnabled());
		 editor.getTxtEclipse().setText("ecl");
		 assertFalse(editor.getOkCancel().isOkEnabled()); //same value
		 editor.getTxtEclipse().setText("ecla");
		 assertTrue(editor.getOkCancel().isOkEnabled()); //new value
		 editor.getTxtEclipse().setText("ecl");
		 assertFalse(editor.getOkCancel().isOkEnabled()); //same value
	}
	
	public void testCannotEditEclipseValueIfSoftwareFmValueHasChangedAndViceVerse(){
		createDisplayerAndEditor("ecl", "sfm");
		editor.getTxtEclipse().setText("ecl");
		assertTrue(editor.getTxtEclipse().isEditable());
		assertTrue(editor.getTxtSoftwareFm().isEditable());
		
		editor.getTxtEclipse().setText("ecla");
		assertTrue(editor.getTxtEclipse().isEditable());
		assertFalse(editor.getTxtSoftwareFm().isEditable());

		editor.getTxtEclipse().setText("ecl");
		assertTrue(editor.getTxtEclipse().isEditable());
		assertTrue(editor.getTxtSoftwareFm().isEditable());
		
		editor.getTxtSoftwareFm().setText("sfma");
		assertFalse(editor.getTxtEclipse().isEditable());
		assertTrue(editor.getTxtSoftwareFm().isEditable());

		editor.getTxtSoftwareFm().setText("sfm");
		assertTrue(editor.getTxtEclipse().isEditable());
		assertTrue(editor.getTxtSoftwareFm().isEditable());
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
	protected IResourceGetter makeResources() {
		return IResourceGetter.Utils.noResources().with(new ResourceGetterMock(//
				"dialog.eclipseValue.title", "eclipse value",//
				"dialog.softwareFmValue.title", "software fm value",//
				"button.noData.title", "no_data",//
				"button.doesntMatches.title", "doesnt_match",//
				"button.copyToEclipse.title", "copy_to_eclipse",//
				"button.copyToSoftwareFm.title", "copy_to_sfm",//
				"button.eclipseNotUrl.title", "notUrl",//
				"button.matches.title", "matches",//
				"button.cancel.title", "cancelValue", //
				"button.ok.title", "okValue", "helpKey", "helpValue"));
	}

	@Override
	protected JavadocSourceEditor makeEditor() {
		return new JavadocSourceEditor(getEclipseValueKey(), getSoftwareFmValueKey(), getMutatorKey());
	}

	@Override
	protected DisplayerDefn makeBaseDisplayDefn() {
		JavadocSourceSummaryDisplayerFactory displayerFactory = new JavadocSourceSummaryDisplayerFactory(getEclipseValueKey(), getSoftwareFmValueKey());
		return new DisplayerDefn(displayerFactory).title("Ttl").editor("someEditor").icon(ArtifactsAnchor.projectKey);
	}

}
