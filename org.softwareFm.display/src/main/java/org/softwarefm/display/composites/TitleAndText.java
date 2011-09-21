package org.softwareFm.display.composites;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.Swts;
import org.softwareFm.display.smallButtons.ImageButtonConfig;
import org.softwareFm.display.smallButtons.SimpleImageButton;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class TitleAndText extends TitleAnd {

	private final Text text;

	public TitleAndText(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey) {
		super(config, parent, titleOrTitleKey, titleIsKey);
		text = new Text(getComposite(), SWT.NULL);
		text.setLayoutData(new RowData(config.layout.valueWidth, config.layout.textHeight));
	}

	public void setText(String text) {
		this.text.setText(text);
	}

	public void setEditable(boolean editable) {
		text.setEditable(editable);
	}


	public String getText() {
		return text.getText();
	}
	
	
	public static void main(String[] args) {
		Swts.display("TitleAndText", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = BasicImageRegisterConfigurator.forTests(from);
				IResourceGetter resourceGetter = IResourceGetter.Utils.noResources();
				ImageButtonConfig config = ImageButtonConfig.forTests(imageRegistry);
				TitleAndText titleAndText = new TitleAndText(new CompositeConfig(from.getDisplay(), new SoftwareFmLayout(), imageRegistry, resourceGetter), from, "Value", false);
				new SimpleImageButton(titleAndText, config.withImage(ArtifactsAnchor.jarKey));
				new SimpleImageButton(titleAndText, config.withImage(ArtifactsAnchor.projectKey));
				return titleAndText.getComposite();
			}
		}) ;
	}

	public void addCrListener(final Listener listener) {
		text.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				listener.handleEvent(new Event());
			}
		});
	}


}
