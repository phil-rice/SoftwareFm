package org.softwareFm.explorer.eclipse;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class MasterDetailSocial implements IMasterDetailSocial {

	static class MasterDetailComposite extends SashForm {

		private final Composite master;

		private final SashForm detailSocial;

		private final Composite detail;
		private final Composite social;

		public MasterDetailComposite(Composite parent, int style) {
			super(parent, style);
			master = Swts.newComposite(this, SWT.NULL, "Master");
			detailSocial = Swts.newSashForm(this, SWT.VERTICAL, "DetailSocial");
			detail = Swts.newComposite(detailSocial, SWT.NULL, "Detail");
			social = Swts.newComposite(detailSocial, SWT.NULL, "Social");

			detailSocial.setWeights(new int[] { 1, 1 });

			master.setLayout(new StackLayout());
			detail.setLayout(new StackLayout());
			social.setLayout(new StackLayout());
			setWeights(new int[] { 1, 2 });
		}
	}

	private final MasterDetailComposite content;
	private final Set<Control> preserve = Sets.newSet();

	public MasterDetailSocial(Composite parent, int style) {
		content = new MasterDetailComposite(parent, style);
		Swts.resizeMeToParentsSizeWithLayout(this);
	}

	@Override
	public <T extends IHasControl> T createMaster(IFunction1<Composite, T> builder, boolean preserve) {
		return create(content.master, builder, preserve);
	}

	@Override
	public <T extends IHasControl> T createDetail(IFunction1<Composite, T> builder, boolean preserve) {
		return create(content.detail, builder, preserve);
	}

	@Override
	public <T extends IHasControl> T createSocial(IFunction1<Composite, T> builder, boolean preserve) {
		return create(content.social, builder, preserve);
	}

	private <T extends IHasControl> T create(Composite composite, IFunction1<Composite, T> builder, boolean preserve) {
		T result = Functions.call(builder, composite);
		if (preserve)
			this.preserve.add(result.getControl());
		return result;

	}

	@Override
	public <T extends IHasControl> T createAndShowDetail(IFunction1<Composite, T> builder) {
		return createAndSHow(content.detail, builder);
	}

	@Override
	public <T extends IHasControl> T createAndShowSocial(IFunction1<Composite, T> builder) {
		return createAndSHow(content.social, builder);
	}

	private <T extends IHasControl> T createAndSHow(Composite composite, IFunction1<Composite, T> builder) {
		T control = create(composite, builder, false);
		set(composite, control == null ? null : control.getControl());
		return control;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public void setMaster(Control master) {
		set(content.master, master);
	}

	@Override
	public void setDetail(Control detail) {
		set(content.detail, detail);
		content.detailSocial.layout();
	}

	@Override
	public void setSocial(Control social) {
		set(content.social, social);
		content.detailSocial.layout();
	}

	@Override
	public void hideSocial() {
		content.detailSocial.setMaximizedControl(content.detail);
	}

	@Override
	public void showSocial() {
		content.detailSocial.setMaximizedControl(null);
	}

	private void set(Composite composite, Control social) {
		clean(composite, social);
		StackLayout layout = (StackLayout) composite.getLayout();
		layout.topControl = social;
		composite.layout();
		Swts.layoutIfComposite(social);
	}

	private void clean(Composite composite, Control newValue) {
		for (Control control : composite.getChildren())
			if (!preserve.contains(control))
				if (control != newValue)
					control.dispose();
	}

	public static void main(String[] args) {
		Swts.xUnit(MasterDetailSocial.class.getSimpleName(), new ISituationListAndBuilder<MasterDetailSocial>() {
			private final Map<String, Control> masterMap = Maps.newMap();
			private final Map<String, Control> detailMap = Maps.newMap();

			@Override
			public void selected(final MasterDetailSocial masterDetailSocial, final String context, final Object value) throws Exception {
				Control master = Maps.findOrCreate(masterMap, context, new Callable<Control>() {
					@Override
					public Control call() throws Exception {
						IHasControl master = masterDetailSocial.createMaster(Swts.styledTextFn(context, SWT.WRAP), true);
						return master.getControl();
					}
				});
				Control detail = Maps.findOrCreate(detailMap, context, new Callable<Control>() {
					@Override
					public Control call() throws Exception {
						@SuppressWarnings("unchecked")
						IFunction1<Composite, IHasControl> fn = (IFunction1<Composite, IHasControl>) value;
						IHasControl detail = masterDetailSocial.createDetail(fn, true);
						return detail.getControl();
					}
				});
				masterDetailSocial.setMaster(master);
				masterDetailSocial.setDetail(detail);

			}

			@Override
			public MasterDetailSocial makeChild(Composite parent) throws Exception {
				return new MasterDetailSocial(parent, SWT.NULL);
			}
		}, Maps.stringObjectLinkedMap(//
				"text", Swts.styledTextFn("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", SWT.WRAP), //
				"emptyText", Swts.styledTextFn("", SWT.WRAP), //
				"label", Swts.labelFn("label")));
	}
}
