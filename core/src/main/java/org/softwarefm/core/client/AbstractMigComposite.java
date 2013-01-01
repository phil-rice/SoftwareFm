package org.softwarefm.core.client;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.swt.HasComposite;

public class AbstractMigComposite extends HasComposite {

	public AbstractMigComposite(Composite parent, MigLayout layout) {
		super(parent);
		setLayout(layout);
	}

	protected void async(final Runnable runnable) {
		new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

}
