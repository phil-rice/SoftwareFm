package org.softwareFm.display.composites;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.display.AllSoftwareFmDisplayTests;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.smallButtons.ImageButtonConfig;
import org.softwareFm.display.smallButtons.SimpleImageButton;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class TitleAndText extends AbstractTitleAndText<Text> {

	public TitleAndText(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey) {
		super(config, parent, titleOrTitleKey, titleIsKey, config.layout.textHeight);
	}

	@Override
	public void addCrListener(final Listener listener) {
		text.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				listener.handleEvent(new Event());
			}
		});
	}

	@Override
	protected Text makeText() {
		return new Text(getComposite(), SWT.NULL);
	}

	@Override
	public void setText(String text) {
		this.text.setText(text);
	}

	public void setEditable(boolean editable) {
		text.setEditable(editable);
	}

	@Override
	public String getText() {
		return text.getText();
	}

	@Override
	public void addModifyListener(ModifyListener listener) {
		text.addModifyListener(listener);
	}
	@Override
	public void removeModifyListener(ModifyListener listener) {
		text.removeModifyListener(listener);
	}
	public static void main(String[] args) {
		Swts.display("TitleAndText", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = BasicImageRegisterConfigurator.forTests(from);
				IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(AllSoftwareFmDisplayTests.class, "Test");
				;
				ImageButtonConfig config = ImageButtonConfig.forTests(imageRegistry);
				TitleAndText titleAndText = new TitleAndText(new CompositeConfig(from.getDisplay(), new SoftwareFmLayout(), imageRegistry, resourceGetter), from, "Value", false);
				new SimpleImageButton(titleAndText, config.withImage(ArtifactsAnchor.jarKey), false);
				new SimpleImageButton(titleAndText, config.withImage(ArtifactsAnchor.projectKey), false);
				return titleAndText.getComposite();
			}
		});
	}

	public boolean isEditable() {
		return text.getEditable();
	}

}
