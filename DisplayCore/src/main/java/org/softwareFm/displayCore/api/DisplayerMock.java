package org.softwareFm.displayCore.api;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class DisplayerMock implements IDisplayer<IHasControl, IHasControl> {

	public String prefix;
	public List<Label> largeControls = Collections.synchronizedList(Lists.<Label> newList());
	public List<Label> smallControls = Collections.synchronizedList(Lists.<Label> newList());
	public AtomicInteger largeIndex = new AtomicInteger();
	public AtomicInteger smallIndex = new AtomicInteger();
	private BindingContext bindingContext;

	public Map<IHasControl, List<Object>> largeValues = Collections.synchronizedMap(Maps.<IHasControl, List<Object>> newMap(LinkedHashMap.class));
	public Map<IHasControl, List<Object>> smallValues = Collections.synchronizedMap(Maps.<IHasControl, List<Object>> newMap(LinkedHashMap.class));
	public DisplayerContext displayerContext;
	public ITopButtonState topButtonState;
	public Composite smallParent;
	public Composite largeParent;
	public final List<DisplayerDetails> smallDisplayerDetails = Lists.newList();
	public final List<DisplayerDetails> largeDisplayerDetails = Lists.newList();

	@Override
	public IHasControl createSmallControl(DisplayerContext displayerContext, IRegisteredItems registeredItems, ITopButtonState topButtonState, Composite parent, DisplayerDetails displayerDetails) {
		this.topButtonState = topButtonState;
		this.smallParent = parent;
		checkDisplayedContext(displayerContext);
		this.smallDisplayerDetails.add(displayerDetails);
		Label result = new Label(parent, SWT.NULL);
		smallControls.add(result);
		result.setText(prefix + " small " + smallControls.size());
		return IHasControl.Utils.toHasControl(result);
	}

	@Override
	public IHasControl createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		this.largeParent = parent;
		checkDisplayedContext(displayerContext);
		this.largeDisplayerDetails.add(displayerDetails);
		Label result = new Label(parent, SWT.NULL);
		largeControls.add(result);
		result.setText(prefix + " large " + largeControls.size());
		return IHasControl.Utils.toHasControl(result);
	}

	private void checkDisplayedContext(DisplayerContext displayerContext) {
		if (this.displayerContext == null)
			this.displayerContext = displayerContext;
		Assert.assertSame(displayerContext, this.displayerContext);

	}

	public DisplayerMock(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void populateSmallControl(BindingContext bindingContext, IHasControl smallControl, Object value) {
		checkBindingContext(bindingContext);
		Assert.assertTrue(smallControls.contains(smallControl));
		Maps.addToList(smallValues, smallControl, value);
		Label label = (Label) smallControl.getControl();
		label.setText(Strings.nullSafeToString(value));

	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, IHasControl largeControl, Object value) {
		Assert.assertTrue(largeControls.contains(largeControl));
		checkBindingContext(bindingContext);
		Maps.addToList(largeValues, largeControl, value);
		Label label = (Label) largeControl.getControl();
		label.setText(Strings.nullSafeToString(value));
	}

	private void checkBindingContext(BindingContext bindingContext) {
		if (bindingContext != null)
			this.bindingContext = bindingContext;
		Assert.assertSame(this.bindingContext, bindingContext);
	}

	public Collection<Label> labelsFor(int index) {
		Collection<Label> result = Lists.newList();
		result.add(largeControls.get(index));
		result.add(smallControls.get(index));
		return result;
	}
}
