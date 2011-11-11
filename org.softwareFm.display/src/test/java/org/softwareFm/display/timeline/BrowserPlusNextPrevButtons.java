package org.softwareFm.display.timeline;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.browser.IBrowserComposite;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Button;
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.utilities.functions.IFunction1;

public class BrowserPlusNextPrevButtons implements IHasControl {

	private final Composite content;
	private final Composite playlist;
	private final CompositeConfig config;
	private TimeLine timeLine;
	private final IPlayListGetter playListGetter;
	private final IBrowserComposite browser;

	public BrowserPlusNextPrevButtons(Composite parent, int style, CompositeConfig config, IFunction1<Composite, IBrowserComposite> browserCreator, IPlayListGetter playListGetter) throws Exception {
		this.config = config;
		this.playListGetter = playListGetter;
		content = Swts.newComposite(parent, style, getClass().getSimpleName());
		Composite buttons = Swts.newComposite(content, SWT.NULL, "buttons");
		playlist = Swts.newComposite(content, SWT.NULL, "playList");
		browser = browserCreator.apply(content);
		timeLine = new TimeLine(playListGetter);
		Button.makePushButton(buttons, config.resourceGetter, "browser.next.title", new Runnable() {
			@Override
			public void run() {
				timeLine.next();
			}
		});
		Button.makePushButton(buttons, config.resourceGetter, "browser.prev.title", new Runnable() {
			@Override
			public void run() {
				if (timeLine.hasPrevious())
					timeLine.previous();
			}
		});
		buttons.setLayout(new RowLayout());
		playlist.setLayout(new RowLayout());
		Grid.addGrabHorizontalAndFillGridDataToAllChildren(content);
		browser.getControl().setLayoutData(Grid.makeGrabHorizonalVerticalAndFillGridData());
	}

	@Override
	public Control getControl() {
		return content;
	}

	public void addPlayLists(Set<String> keySet) {
		timeLine = new TimeLine(playListGetter);
		Swts.removeAllChildren(playlist);
		for (final String key : keySet)
			Button.makePushButton(playlist, config.resourceGetter, key, false, new Runnable() {
				@Override
				public void run() {
					timeLine.selectAndNext(key);
				}
			});
	}

}
