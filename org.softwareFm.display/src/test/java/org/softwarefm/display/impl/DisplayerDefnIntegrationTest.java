package org.softwareFm.display.impl;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionDefn;
import org.softwareFm.display.composites.AbstractTitleAndText;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.DataGetterMock;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.display.displayer.CompressedText;
import org.softwareFm.display.displayer.CompressedTextDisplayerFactory;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.editor.TextEditor;
import org.softwareFm.display.smallButtons.SimpleImageControl;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.tests.IIntegrationTest;
import org.softwareFm.utilities.tests.Tests;

public class DisplayerDefnIntegrationTest extends AbstractDisplayerEditorIntegrationTest<CompressedText, TextEditor> implements IIntegrationTest {

	private ActionContext actionContext;
	private IDataGetter dataGetter;

	public void testCreateWithNoButtonsAndIconSet() {
		IDisplayer displayer = displayerDefn.icon(ArtifactsAnchor.artifactKey).createDisplayer(shell, actionContext);
		checkNoButtons(displayer, ArtifactsAnchor.artifactKey, null);
	}

	public void testCreateWithNoButtonsAndIconAndOverlaySet() {
		IDisplayer displayer = displayerDefn.icon(ArtifactsAnchor.issuesKey, OverlaysAnchor.deleteKey).createDisplayer(shell, actionContext);
		checkNoButtons(displayer, ArtifactsAnchor.issuesKey, OverlaysAnchor.deleteKey);
	}

	public void testNoIconIfNoIconSet() {
		IDisplayer displayer = displayerDefn.noIcon().createDisplayer(shell, actionContext);
		Control[] children = displayer.getButtonComposite().getChildren();
		assertEquals(0, children.length);

	}

	public void testIconMustBeSetIfNoDefaultAction() {
		IllegalStateException e = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				displayerDefn.createDisplayer(shell, actionContext);
			}
		});
		assertEquals("Must have a iconImage in DisplayerDefn [title=someTitle, dataKey=null, defaultAction=null, tooltip=null, editorId=null, listEditorId=null, listActionDefns=null, guardKeys=null, iconImageId=null, iconOverlayId=null, actionDefns=[]]", e.getMessage());
	}

	public void testHelpAppearsInHelpArea() {
		DisplayerDefn defn = displayerDefn.help("helpKey").noIcon().editor("someEditor");
		CompressedText displayer = (CompressedText) defn.createDisplayer(shell, actionContext);
		Label label = displayer.getLabel();
		label.notifyListeners(SWT.MouseDown, new Event());
		assertEquals("helpValue", editor.getHelpControl().getText());
	}

	public void testBlankAppearsInHelpAreaWhenHelpNotSpecified() {
		DisplayerDefn defn = displayerDefn.noIcon().editor("someEditor");
		CompressedText displayer = (CompressedText) defn.createDisplayer(shell, actionContext);
		Label label = displayer.getLabel();
		label.notifyListeners(SWT.MouseDown, new Event());
		assertEquals("", editor.getHelpControl().getText());
	}

	public void testWithOneAction() {
		DisplayerDefn dispDefnWithButton = displayerDefn.actions(new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).tooltip("tooltip0"));
		IDisplayer displayer = dispDefnWithButton.createDisplayer(shell, actionContext);
		checkButtons(displayer, ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey);
	}

	public void testWithTwoActions() {
		DisplayerDefn dispDefnWithButton = displayerDefn.actions(//
				new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).tooltip("tooltip0"),//
				new ActionDefn("someId", ArtifactsAnchor.facebookKey, OverlaysAnchor.addKey).tooltip("tooltip1"));
		IDisplayer displayer = dispDefnWithButton.createDisplayer(shell, actionContext);
		checkButtons(displayer, ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey);
	}

	public void testWithDefaultActionNotTheFirstOne() {
		DisplayerDefn dispDefnWithButton = displayerDefn.actions(//
				new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).tooltip("tooltip0"),//
				new ActionDefn("someId", ArtifactsAnchor.facebookKey, null).tooltip("tooltip1").thisIsDefault());
		IDisplayer displayer = dispDefnWithButton.createDisplayer(shell, actionContext);
		checkButtons(displayer, ArtifactsAnchor.facebookKey, null);
	}

	@SuppressWarnings("unchecked")
	public void testPressingButtonCausesActionToFireWithParametersFromDataGetter() {
		DisplayerDefn dispDefnWithButton = displayerDefn.//
				actions(new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).params("a"));
		IDisplayer displayer = dispDefnWithButton.createDisplayer(shell, actionContext);
		clickOnEditor(displayer);
		assertEquals(Arrays.asList(displayer), actionMock.displayers);
		assertEquals(Arrays.asList(Arrays.asList("a")), actionMock.formalParams);
		// assertEquals(Arrays.asList(Arrays.asList(1)), actionMock.actualParams);

	}

	public void testTooltipisSetSetWithData() {
		DisplayerDefn dispDefnWithButton = displayerDefn.//
				data("dataKey").//
				actions(new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).params("param1").tooltip("someActionTooltip")).tooltip("someDataTooltip");
		IDisplayer displayer = dispDefnWithButton.createDisplayer(shell, actionContext);
		SimpleImageControl control = (SimpleImageControl) displayer.getButtonComposite().getChildren()[0];
		assertEquals(null, control.getToolTipText());

		ActionContext actionContext = new ActionContext(null, actionStore, dataGetter, null, null, null, null, null, null);
		dispDefnWithButton.data(actionContext, dispDefnWithButton, displayer, "someENtity", "someUrl");
		assertEquals("someActionTooltipValue", control.getToolTipText());

		assertEquals("someDataTooltipValue", displayer.getControl().getToolTipText());
	}

	public void testTextIsSetWithData() {
		DisplayerDefn dispDefnWithButton = displayerDefn.//
				data("dataKey").//
				actions(new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).params("param1").tooltip("someActionTooltip")).tooltip("someDataTooltip");
		IDisplayer displayer = dispDefnWithButton.createDisplayer(shell, actionContext);
		SimpleImageControl control = (SimpleImageControl) displayer.getButtonComposite().getChildren()[0];
		assertEquals(null, control.getToolTipText());

		ActionContext actionContext = new ActionContext(null, actionStore, dataGetter, null, null, null, null, null, null);
		dispDefnWithButton.data(actionContext, dispDefnWithButton, displayer, "someENtity", "someUrl");
		assertEquals("someActionTooltipValue", control.getToolTipText());

		assertEquals("value1", ((CompressedText) displayer).getText());
	}

	public void testMainControlCausesEditorToBeDisplayed() {
		DisplayerDefn dispDefnWithButton = displayerDefn.//
				data("data.entity.key").//
				editor("someEditor").actions(new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).params("param1").tooltip("someActionTooltip")).tooltip("someDataTooltip");
		CompressedText displayer = (CompressedText) dispDefnWithButton.createDisplayer(shell, actionContext);
		Label label = displayer.getLabel();
		label.notifyListeners(SWT.MouseDown, new Event());
		AbstractTitleAndText<Text> text = editor.getText();
		assertEquals("value", text.getText()); // extracted from data getter
		assertEquals(editor.getControl(), rightHandSide.getVisibleControl());

	}

	public void testEditorIsDisabledIfGuardPresent() {
		DisplayerDefn defn = displayerDefn.//
				guard("data.entity.notIn", "guardKey").//
				data("data.entity.key").//
				editor("someEditor").actions(new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).params("param1").tooltip("someActionTooltip")).tooltip("someDataTooltip");
		CompressedText displayer = (CompressedText) defn.createDisplayer(shell, actionContext);
		defn.data(actionContext, defn, displayer, "entity", "url");
		Label label = displayer.getLabel();
		assertEquals("guardTitle", label.getText()); // extracted from data getter
		assertFalse(displayer.isShouldBeEnabled());// cannot actually tell if it is enabled. Think this is because the control is invisible
	}

	public void testEditorIsEnabledIfToldToIgnoreMissingGuard() {
		DisplayerDefn defn = displayerDefn.//
				guard("data.entity.notIn", "guardKey").//
				data("data.entity.key").//
				editor("someEditor").editorIgnoreGuard("data.entity.notIn").actions(new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).params("param1").tooltip("someActionTooltip")).tooltip("someDataTooltip");
		CompressedText displayer = (CompressedText) defn.createDisplayer(shell, actionContext);
		defn.data(actionContext, defn, displayer, "entity", "url");
		Label label = displayer.getLabel();
		assertEquals("guardTitle", label.getText()); // extracted from data getter
		assertTrue(displayer.isShouldBeEnabled());// cannot actually tell if it is enabled. Think this is because the control is invisible
	}

	public void testEditorIsDisabledIfToldToIgnoreMissingGuardButOtherGuardExists() {
		DisplayerDefn defn = displayerDefn.//
				guard("data.entity.alsoNotIn", "guardKey2", "data.entity.notIn", "guardKey").//
				data("data.entity.key").//
				editor("someEditor").editorIgnoreGuard("data.entity.notIn").actions(new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).params("param1").tooltip("someActionTooltip")).tooltip("someDataTooltip");
		CompressedText displayer = (CompressedText) defn.createDisplayer(shell, actionContext);
		defn.data(actionContext, defn, displayer, "entity", "url");
		Label label = displayer.getLabel();
		assertEquals("guardTitle2", label.getText()); // extracted from data getter
		assertFalse(displayer.isShouldBeEnabled());// cannot actually tell if it is enabled. Think this is because the control is invisible
	}

	public void testEditorIsDisabledIfToldToIgnoreMissingGuardButOtherGuardExistsWithGuardsInDifferentOrder() {
		DisplayerDefn defn = displayerDefn.//
				guard("data.entity.notIn", "guardKey", "data.entity.alsoNotIn", "guardKey2").//
				data("data.entity.key").//
				editor("someEditor").editorIgnoreGuard("data.entity.notIn").actions(new ActionDefn("someId", ArtifactsAnchor.artifactKey, OverlaysAnchor.deleteKey).params("param1").tooltip("someActionTooltip")).tooltip("someDataTooltip");
		CompressedText displayer = (CompressedText) defn.createDisplayer(shell, actionContext);
		defn.data(actionContext, defn, displayer, "entity", "url");
		Label label = displayer.getLabel();
		assertEquals("guardTitle", label.getText()); // extracted from data getter
		assertFalse(displayer.isShouldBeEnabled());// cannot actually tell if it is enabled. Think this is because the control is invisible
	}

	private void checkNoButtons(IDisplayer displayer, String expectedIcon, String expectedOverlay) {
		Control[] children = displayer.getButtonComposite().getChildren();
		assertEquals(1, children.length);
		SimpleImageControl control = (SimpleImageControl) children[0];
		assertEquals(expectedIcon, control.config.mainImage);
		assertEquals(expectedOverlay, control.config.overlayImage);

	}

	private void checkButtons(IDisplayer displayer, String main, String overlay) {
		Control[] children = displayer.getButtonComposite().getChildren();
		assertEquals(1, children.length);
		SimpleImageControl control = (SimpleImageControl) children[0];
		assertEquals(false, control.value());
		assertEquals(main, control.config.mainImage);
		assertEquals(overlay, control.config.overlayImage);
		assertNull(control.getToolTipText());
	}

	@Override
	protected TextEditor makeEditor() {
		return new TextEditor();
	}

	@Override
	protected DisplayerDefn makeBaseDisplayDefn() {
		return new DisplayerDefn(new CompressedTextDisplayerFactory()).title("someTitle");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataGetter = new DataGetterMock(//
				"data.entity.key", "value",//
				"guardKey", "guardTitle",//
				"guardKey2", "guardTitle2",//
				"someActionTooltip", "someActionTooltipValue",//
				"someDataTooltip", "someDataTooltipValue",//
				"dataKey", "value1");
		actionContext = makeActionContext(dataGetter);
	}

	@Override
	protected IResourceGetter makeResources() {
		IResourceGetter resourceGetter = new ResourceGetterMock(//
				"someTitle", "registeredTitle", //
				DisplayConstants.buttonCancelTitle, "cancelValue", //
				DisplayConstants.buttonOkTitle, "okValue", "helpKey", "helpValue");
		return resourceGetter;
	}
}
