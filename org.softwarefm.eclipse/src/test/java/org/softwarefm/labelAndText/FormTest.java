package org.softwarefm.labelAndText;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.SwtTest;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.collections.Lists;

public class FormTest extends SwtTest {
	private final static RGB red = new RGB(255, 0, 0);
	private final static RGB white = new RGB(0, 0, 0);

	private final static List<KeyAndProblem> noProblemList = Collections.emptyList();

	private final static KeyAndProblem groupIdProblem = new KeyAndProblem(SwtConstants.groupIdKey, "groupIdProblem");
	private final static KeyAndProblem groupIdProblem2 = new KeyAndProblem(SwtConstants.groupIdKey, "groupIdProblem2");
	private final static KeyAndProblem artifactIdProblem = new KeyAndProblem(SwtConstants.artifactIdKey, "artifactIdProblem");
	private final static KeyAndProblem versionProblem = new KeyAndProblem(SwtConstants.versionKey, "versionProblem");

	private final static List<KeyAndProblem> groupIdProblemList = Arrays.asList(groupIdProblem);
	private final static List<KeyAndProblem> artifactIdProblemList = Arrays.asList(artifactIdProblem);
	private final static List<KeyAndProblem> versionProblemList = Arrays.asList(versionProblem);
	private final static List<KeyAndProblem> groupId12AndArtifactIdProblemList = Arrays.asList(groupIdProblem, groupIdProblem2, artifactIdProblem);

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

	public void testUsesButtonExecutorsOnConstructorToSetButtonStatus() {
		checkCanExecuteCounts(1, 1);
		assertFalse(form.getButton(SwtConstants.okButton).isEnabled());
		assertTrue(form.getButton(SwtConstants.cancelButton).isEnabled());

		ok.canExecute = noProblemList;
		form = new Form(shell, SWT.NULL, container, buttonConfigurator, keys);
		assertTrue(form.getButton(SwtConstants.okButton).isEnabled());
		assertTrue(form.getButton(SwtConstants.cancelButton).isEnabled());
	}

	public void testSetTextCausesButtonStatusToChange() {
		for (Text text : texts) {
			assertFalse(form.getButton(SwtConstants.okButton).isEnabled());

			ok.canExecute = noProblemList;
			text.notifyListeners(SWT.Modify, new Event());
			assertTrue(form.getButton(SwtConstants.okButton).isEnabled());

			ok.canExecute = groupIdProblemList;
			text.notifyListeners(SWT.Modify, new Event());

			assertFalse(form.getButton(SwtConstants.okButton).isEnabled());
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
		assertEquals(3, texts.size());
		return texts;
	}

	public void testTypingCausesButtonStatusToChange() {
		assertFalse(form.getButton(SwtConstants.okButton).isEnabled());
		ok.canExecute = noProblemList;
		form.setText(SwtConstants.groupIdKey, "g");
		assertTrue(form.getButton(SwtConstants.okButton).isEnabled());
	}

	public void testUsesButtonExecutorsWhenSetText() {
		checkCanExecuteCounts(1, 1);
		form.setText(SwtConstants.groupIdKey, "g");
		checkCanExecuteCounts(2, 2);
		form.setText(SwtConstants.artifactIdKey, "a");
		checkCanExecuteCounts(3, 3);
		form.setText(SwtConstants.versionKey, "v");
		checkCanExecuteCounts(4, 4);
		form.setText(SwtConstants.groupIdKey, "g");
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
		Swts.Buttons.press(form.getButton(SwtConstants.okButton));
		checkCounts(1, 0);
		Swts.Buttons.press(form.getButton(SwtConstants.cancelButton));
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
		form.setText(SwtConstants.groupIdKey, g);
		form.setText(SwtConstants.artifactIdKey, a);
		form.setText(SwtConstants.versionKey, v);

	}

	private void checkText(String g, String a, String v) {
		assertEquals(g, form.getText(SwtConstants.groupIdKey));
		assertEquals(a, form.getText(SwtConstants.artifactIdKey));
		assertEquals(v, form.getText(SwtConstants.versionKey));

		assertEquals(g, groupIdText.getText());
		assertEquals(a, artifactIdText.getText());
		assertEquals(v, versionText.getText());

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ok = new ButtonExecutorMock(SwtConstants.okButton, groupIdProblemList);
		cancel = new ButtonExecutorMock(SwtConstants.cancelButton, noProblemList);
		container = SoftwareFmContainer.makeForTests();
		buttonConfigurator = IButtonConfigurator.Utils.make(ok, cancel);
		keys = new String[] { SwtConstants.groupIdKey, SwtConstants.artifactIdKey, SwtConstants.versionKey };
		form = new Form(shell, SWT.NULL, container, buttonConfigurator, keys);
		texts = getTexts();
		groupIdText = texts.get(0);
		artifactIdText = texts.get(1);
		versionText = texts.get(2);
	}
}
