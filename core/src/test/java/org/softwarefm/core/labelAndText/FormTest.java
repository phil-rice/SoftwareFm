package org.softwarefm.core.labelAndText;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.ImageConstants;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.labelAndText.Form;
import org.softwarefm.core.labelAndText.IButtonConfigurator;
import org.softwarefm.core.labelAndText.IFormProblemHandler;
import org.softwarefm.core.labelAndText.KeyAndProblem;
import org.softwarefm.core.labelAndText.MemoryFormProblemHandler;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.core.tests.SwtTest;
import org.softwarefm.utilities.collections.Lists;

public class FormTest extends SwtTest {
	private final static RGB red = new RGB(255, 0, 0);
	private final static RGB white = new RGB(0, 0, 0);

	private final static List<KeyAndProblem> noProblemList = Collections.emptyList();

	private final static KeyAndProblem globalProblem1 = new KeyAndProblem(null, "globalProblem1");
	private final static KeyAndProblem globalProblem2 = new KeyAndProblem(null, "globalProblem2");
	private final static KeyAndProblem groupIdProblem = new KeyAndProblem(TextKeys.keyManualImportGroupId, "groupIdProblem");
	private final static KeyAndProblem groupIdProblem2 = new KeyAndProblem(TextKeys.keyManualImportGroupId, "groupIdProblem2");
	private final static KeyAndProblem artifactIdProblem = new KeyAndProblem(TextKeys.keyManualImportArtifactId, "artifactIdProblem");

	private final static List<KeyAndProblem> groupIdProblemList = Arrays.asList(groupIdProblem);
	private final static List<KeyAndProblem> groupId12AndArtifactIdProblemList = Arrays.asList(groupIdProblem, groupIdProblem2, artifactIdProblem);
	private final static List<KeyAndProblem> groupId12ArtifactIdAndGlobal1Global2ProblemList = Arrays.asList(groupIdProblem, groupIdProblem2, artifactIdProblem, globalProblem1, globalProblem2);

	private Form form;
	private ButtonExecutorMock ok;
	private ButtonExecutorMock cancel;
	private IButtonConfigurator buttonConfigurator;
	private SoftwareFmContainer<Object> container;
	private String[] keys;

	private List<Text> texts;

	private Text artifactIdText;

	private Text groupIdText;

	private Text versionText;
	private MemoryFormProblemHandler memoryHandler;

	public void testUsesButtonExecutorsOnConstructorToSetButtonStatus() {
		checkCanExecuteCounts(1, 1);
		assertFalse(form.getButton(TextKeys.btnSharedOk).isEnabled());
		assertTrue(form.getButton(TextKeys.btnSharedCancel).isEnabled());

		ok.canExecute = noProblemList;
		form = new Form(shell, SWT.NULL, container, buttonConfigurator, IFormProblemHandler.Utils.noHandler(), keys);
		assertTrue(form.getButton(TextKeys.btnSharedOk).isEnabled());
		assertTrue(form.getButton(TextKeys.btnSharedCancel).isEnabled());
	}

	public void testImages() {
		ImageRegistry imageRegistry = new ImageRegistry(display);
		ImageConstants.initializeImageRegistry(display, imageRegistry);
		IFormProblemHandler problemHandler = IFormProblemHandler.Utils.buttonTooltipProblemHandler(TextKeys.btnSharedProblems, imageRegistry);
		form = new Form(shell, SWT.NULL, container, buttonConfigurator, problemHandler, keys);
		Button problemButton = (Button) form.getButton(TextKeys.btnSharedProblems);
		assertEquals(imageRegistry.get(ImageConstants.exclamationAction), problemButton.getImage());

		ok.canExecute = noProblemList;
		form.updateButtonStatus();

		assertEquals(imageRegistry.get(ImageConstants.exclamationInaction), problemButton.getImage());
	}

	public void testSetTextCausesButtonStatusToChange() {
		for (Text text : texts) {
			assertFalse(form.getButton(TextKeys.btnSharedOk).isEnabled());

			ok.canExecute = noProblemList;
			text.notifyListeners(SWT.Modify, new Event());
			assertTrue(form.getButton(TextKeys.btnSharedOk).isEnabled());

			ok.canExecute = groupIdProblemList;
			text.notifyListeners(SWT.Modify, new Event());

			assertFalse(form.getButton(TextKeys.btnSharedOk).isEnabled());
		}
	}

	private List<Text> getTexts() {
		List<Text> texts = Lists.newList();
		for (Control child : form.getChildren())
			if (child instanceof Composite)
				for (Control gChild : ((Composite) child).getChildren()) {
					if (gChild instanceof Text)
						texts.add((Text) gChild);
				}
		assertEquals(4, texts.size());
		return texts;
	}

	public void testTypingCausesButtonStatusToChange() {
		assertFalse(form.getButton(TextKeys.btnSharedOk).isEnabled());
		ok.canExecute = noProblemList;
		form.setText(TextKeys.keyManualImportGroupId, "g");
		assertTrue(form.getButton(TextKeys.btnSharedOk).isEnabled());
	}

	public void testUsesButtonExecutorsWhenSetText() {
		checkCanExecuteCounts(1, 1);
		form.setText(TextKeys.keyManualImportGroupId, "g");
		checkCanExecuteCounts(2, 2);
		form.setText(TextKeys.keyManualImportArtifactId, "a");
		checkCanExecuteCounts(3, 3);
		form.setText(TextKeys.keyManualImportVersion, "v");
		checkCanExecuteCounts(4, 4);
		form.setText(TextKeys.keyManualImportGroupId, "g");
		checkCanExecuteCounts(5, 5);
	}

	private void checkCanExecuteCounts(int okCount, int cancelCount) {
		assertEquals(okCount, ok.canExecuteCount.get());
		assertEquals(Lists.times(okCount, form), ok.getTextWithKeys);

		assertEquals(cancelCount, cancel.canExecuteCount.get());
		assertEquals(Lists.times(cancelCount, form), cancel.getTextWithKeys);

	}

	public void testSetGetTextAndOkCancel() {
		checkText("", "", "");
		setText("g", "a", "v");
		checkText("g", "a", "v");
		checkCounts(0, 0);
		Swts.Buttons.press(form.getButton(TextKeys.btnSharedOk));
		checkCounts(1, 0);
		Swts.Buttons.press(form.getButton(TextKeys.btnSharedCancel));
		checkCounts(1, 1);
	}

	public void testLinesWithErrorAreRedWithTooltipDescribingProblem() {
		assertEquals(red, groupIdText.getForeground().getRGB());
		assertEquals(white, artifactIdText.getForeground().getRGB());
		assertEquals(white, versionText.getForeground().getRGB());

		assertEquals("groupIdProblem", groupIdText.getToolTipText());
		assertEquals("", artifactIdText.getToolTipText());
		assertEquals("", versionText.getToolTipText());
	}

	public void testErrorsAreReportedInFormProblemHandler() {
		assertEquals(Collections.singletonList(groupIdProblem), Lists.getOnly(memoryHandler.problems));
		assertEquals(Collections.emptyList(), Lists.getOnly(memoryHandler.globalProblems));
	}

	@SuppressWarnings("unchecked")
	public void testProblemsWithNullKeyAreGlobalProblems() {
		ok.canExecute = groupId12ArtifactIdAndGlobal1Global2ProblemList;
		form.updateButtonStatus();
		assertEquals(Arrays.asList(Collections.emptyList(), Arrays.asList("globalProblem1", "globalProblem2")), memoryHandler.globalProblems);
	}

	public void testAggregatesProblemsFromMultipleButtons() {
		ok.canExecute = groupId12AndArtifactIdProblemList;
		form.updateButtonStatus();
		assertEquals(red, groupIdText.getForeground().getRGB());
		assertEquals(red, artifactIdText.getForeground().getRGB());
		assertEquals(white, versionText.getForeground().getRGB());

		assertEquals("groupIdProblem\ngroupIdProblem2", groupIdText.getToolTipText());
		assertEquals("artifactIdProblem", artifactIdText.getToolTipText());
		assertEquals("", versionText.getToolTipText());
	}

	private void checkCounts(int okCount, int cancelCount) {
		assertEquals(okCount, ok.executeCount.get());
		assertEquals(cancelCount, cancel.executeCount.get());
	}

	private void setText(String g, String a, String v) {
		form.setText(TextKeys.keyManualImportGroupId, g);
		form.setText(TextKeys.keyManualImportArtifactId, a);
		form.setText(TextKeys.keyManualImportVersion, v);

	}

	private void checkText(String g, String a, String v) {
		assertEquals(g, form.getText(TextKeys.keyManualImportGroupId));
		assertEquals(a, form.getText(TextKeys.keyManualImportArtifactId));
		assertEquals(v, form.getText(TextKeys.keyManualImportVersion));

		assertEquals(g, groupIdText.getText());
		assertEquals(a, artifactIdText.getText());
		assertEquals(v, versionText.getText());

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ok = new ButtonExecutorMock(TextKeys.btnSharedOk, groupIdProblemList);
		cancel = new ButtonExecutorMock(TextKeys.btnSharedCancel, noProblemList);
		ButtonExecutorMock problems = new ButtonExecutorMock(TextKeys.btnSharedProblems, noProblemList);
		container = SoftwareFmContainer.makeForTests(display);
		buttonConfigurator = IButtonConfigurator.Utils.make(ok, cancel, problems);
		keys = new String[] { TextKeys.keyManualImportGroupId, TextKeys.keyManualImportArtifactId, TextKeys.keyManualImportVersion, TextKeys.btnSharedProblems };
		memoryHandler= IFormProblemHandler.Utils.memoryHandler();
		form = new Form(shell, SWT.NULL, container, buttonConfigurator, memoryHandler, keys);
		texts = getTexts();
		groupIdText = texts.get(0);
		artifactIdText = texts.get(1);
		versionText = texts.get(2);
	}
}
