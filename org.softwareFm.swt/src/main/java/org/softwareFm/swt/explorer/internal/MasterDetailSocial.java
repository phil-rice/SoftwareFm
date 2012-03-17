/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.swt.ISituationListAndBuilder;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.swt.Swts.Show;

public class MasterDetailSocial implements IMasterDetailSocial {

	static class MasterDetailComposite extends SashForm {

		private final Composite master;

		final SashForm detailSocial;

		final Composite detail;
		private final Composite social;

		int layoutCount;

		int detailSocialLayoutCount;

		public MasterDetailComposite(Composite parent, int style) {
			super(parent, style);
			master = Swts.newComposite(this, SWT.NULL, "Master");
			detailSocial = new SashForm(this, SWT.VERTICAL) {
				@Override
				public void layout(boolean changed) {
					super.layout(changed);
					detailSocialLayoutCount++;
				}

				@Override
				public String toString() {
					return super.toString() + "." + "DetailSocial";
				}
			};
			detail = Swts.newComposite(detailSocial, SWT.NULL, "Detail");
			social = Swts.newComposite(detailSocial, SWT.NULL, "Social");

			detailSocial.setWeights(new int[] { 1, 1 });

			master.setLayout(new StackLayout());
			detail.setLayout(new StackLayout());
			social.setLayout(new StackLayout());
			setWeights(new int[] { 1, 2 });
		}

		@Override
		public void layout(boolean changed) {
			super.layout(changed);
			layoutCount++;
		}

		@Override
		public void dispose() {
			master.dispose();
			detailSocial.dispose();
		}
	}

	final MasterDetailComposite content;
	private final Set<Control> preserve = Sets.newSet();

	public MasterDetailSocial(Composite parent, int style) {
		content = new MasterDetailComposite(parent, style);
		Swts.Size.resizeMeToParentsSizeWithLayout(this);
	}

	@Override
	public void dispose() {
		content.dispose();
		for (Control control: preserve)
			control.dispose();
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
	public <T extends IHasControl> T createAndShowMaster(IFunction1<Composite, T> builder) {
		return createAndShow(content.master, builder);
	}

	@Override
	public <T extends IHasControl> T createAndShowDetail(IFunction1<Composite, T> builder) {
		return createAndShow(content.detail, builder);
	}

	@Override
	public <T extends IHasControl> T createAndShowSocial(IFunction1<Composite, T> builder) {
		return createAndShow(content.social, builder);
	}

	private <T extends IHasControl> T createAndShow(final Composite composite, final IFunction1<Composite, T> builder) {
		final AtomicReference<T> result = new AtomicReference<T>();
		Swts.syncExec(composite, new Runnable() {
			@Override
			public void run() {
				T control = create(composite, builder, false);
				set(composite, control == null ? null : control.getControl());
				result.set(control);
			}
		});
		return result.get();
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
	}

	@Override
	public void hideSocial() {
		setMaximizedControlAndLayout(content.detailSocial, content.detail);
	}

	@Override
	public void showSocial() {
		setMaximizedControlAndLayout(content.detailSocial, null);
	}


	private void setMaximizedControlAndLayout(SashForm sashForm, Control control) {
		if (sashForm.getMaximizedControl() != control) {
			sashForm.setMaximizedControl(control);
		}
	}

	private void set(Composite composite, Control control) {
		clean(composite, control);
		StackLayout layout = (StackLayout) composite.getLayout();
		layout.topControl = control;
		composite.layout();
		Swts.layoutIfComposite(control);
	}

	private void clean(Composite composite, Control newValue) {
		for (Control control : composite.getChildren())
			if (!preserve.contains(control))
				if (control != newValue)
					control.dispose();
	}

	public Control getMasterContent() {
		return getContent(content.master);
	}

	public Control getDetailContent() {
		return getContent(content.detail);
	}

	public Control getSocialContent() {
		return getContent(content.social);
	}

	private Control getContent(Composite holder) {
		Composite composite = holder;
		StackLayout layout = (StackLayout) composite.getLayout();
		return layout.topControl;
	}

	@Override
	public void putDetailOverSocial() {
		if (content.detailSocial.getChildren()[0] == content.detail)
			return;
		content.detail.moveAbove(content.social);
		content.detailSocial.layout();

	}

	@Override
	public void putSocialOverDetail() {
		if (content.detailSocial.getChildren()[0] == content.social)
			return;
		content.detail.moveBelow(content.social);
		content.detailSocial.layout();
	}

	public static void main(String[] args) {
		Show.xUnit(MasterDetailSocial.class.getSimpleName(), new ISituationListAndBuilder<MasterDetailSocial>() {
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