package org.softwareFm.explorer.eclipse;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class MasterDetail implements IHasComposite {

	static class MasterDetailComposite extends SashForm {

		private final Composite master;
		private final Composite detail;
		private final StackLayout masterLayout;
		private final StackLayout detailLayout;

		public MasterDetailComposite(Composite parent, int style) {
			super(parent, style);
			master = new Composite(this, SWT.NULL);
			detail = new Composite(this, SWT.NULL);
			masterLayout = new StackLayout();
			detailLayout = new StackLayout();
			master.setLayout(masterLayout);
			detail.setLayout(detailLayout);
			setWeights(new int[] { 1, 2 });
		}
	}

	private final MasterDetailComposite content;
	private final Set<Control> preserve = Sets.newSet();

	public MasterDetail(Composite parent, int style) {
		content = new MasterDetailComposite(parent, style);
	}

	public <T extends IHasControl> T createMaster(IFunction1<Composite, T> builder, boolean preserve) {
		T result = Functions.call(builder, content.master);
		if (preserve)
			this.preserve.add(result.getControl());
		return result;
	}

	public <T extends IHasControl> T createAndShowDetail(IFunction1<Composite, T> builder) {
		try {
			T detail = createDetail(builder, false);
			setDetail(detail == null ? null : detail.getControl());
			return detail;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public <T extends IHasControl> T createDetail(IFunction1<Composite, T> builder, boolean preserve) {
		T result = Functions.call(builder, content.detail);
		if (preserve)
			this.preserve.add(result.getControl());
		return result;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public void setMaster(Control master) {
		clean(content.master, master);
		content.masterLayout.topControl = master;
		content.master.layout();
	}

	public void setDetail(Control detail) {
		clean(content.detail, detail);
		content.detailLayout.topControl = detail;
			content.detail.layout();
		Swts.layoutIfComposite(detail);

	}

	private void clean(Composite composite, Control newValue) {
		for (Control control : composite.getChildren())
			if (!preserve.contains(control))
				if (control != newValue)
					control.dispose();
	}

	public static void main(String[] args) {
		Swts.xUnit(MasterDetail.class.getSimpleName(), new ISituationListAndBuilder<MasterDetail>() {
			private final Map<String, Control> masterMap = Maps.newMap();
			private final Map<String, Control> detailMap = Maps.newMap();

			@Override
			public void selected(final MasterDetail masterDetail, final String context, final Object value) throws Exception {
				Control master = Maps.findOrCreate(masterMap, context, new Callable<Control>() {
					@Override
					public Control call() throws Exception {
						IHasControl master = masterDetail.createMaster(Swts.styledTextFn(context, SWT.WRAP), true);
						return master.getControl();
					}
				});
				Control detail = Maps.findOrCreate(detailMap, context, new Callable<Control>() {
					@Override
					public Control call() throws Exception {
						IFunction1<Composite, IHasControl> fn = (IFunction1<Composite, IHasControl>) value;
						IHasControl detail = masterDetail.createDetail(fn, true);
						return detail.getControl();
					}
				});
				masterDetail.setMaster(master);
				masterDetail.setDetail(detail);

			}

			@Override
			public MasterDetail makeChild(Composite parent) throws Exception {
				return new MasterDetail(parent, SWT.NULL);
			}
		}, Maps.stringObjectLinkedMap(//
				"text", Swts.styledTextFn("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", SWT.WRAP), //
				"emptyText", Swts.styledTextFn("", SWT.WRAP), //
				"label", Swts.labelFn("label")));
	}
}
