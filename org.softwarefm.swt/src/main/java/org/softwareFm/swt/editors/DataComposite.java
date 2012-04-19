/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.internal.DataBodyComposite;
import org.softwareFm.swt.title.TitleSpec;
import org.softwareFm.swt.title.TitleWithTitlePaintListener;

abstract public class DataComposite<T extends Control> extends Composite implements IDataComposite<T> {
	private final CardConfig cardConfig;
	private TitleWithTitlePaintListener titleWithTitlePaintListener;
	private final DataBodyComposite body;
	private String cardType;

	static class DataCompositeHeaderLayout {

	}

	public DataComposite(Composite parent, CardConfig cardConfig, String cardType, String title) {
		this(parent, cardConfig, cardType, title, false);
	}

	public DataComposite(Composite parent, CardConfig cardConfig, String cardType, String title, boolean titleIsKey) {
		super(parent, SWT.NULL);
		this.cardConfig = cardConfig;
		this.cardType = cardType;
		body = new DataBodyComposite(this, cardConfig, new Callable<TitleSpec>() {
			@Override
			public TitleSpec call() throws Exception {
				return getTitleSpec();
			}
		});
		String titleText = titleIsKey ? IResourceGetter.Utils.getOrException(getResourceGetter(), title) : title;
		setTitleAndImage(titleText, "", cardType);
	}

	protected IResourceGetter getResourceGetter() {
		return Functions.call(cardConfig.resourceGetterFn, cardType);
	}

	@Override
	public CardConfig getCardConfig() {
		return cardConfig;
	}

	@Override
	public String getCardType() {
		return cardType;
	}

	@Override
	public TitleWithTitlePaintListener getTitle() {
		return titleWithTitlePaintListener;
	}

	@Override
	public TitleSpec getTitleSpec() {
		return titleWithTitlePaintListener.getTitleSpec();
	}

	@Override
	public Composite getBody() {
		return body;
	}

	@Override
	public Composite getInnerBody() {
		return body.getInnerBody();
	}

	@Override
	public boolean useAllHeight() {
		return true;
	}

	protected void setTitleAndImage(String titleText, String tooltipText, String cardType) {
		this.cardType = cardType;
		TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, ICardData.Utils.create(cardConfig, cardType, null, Maps.emptyStringObjectMap()));
		if (titleWithTitlePaintListener == null)
			titleWithTitlePaintListener = new TitleWithTitlePaintListener(this, cardConfig, titleSpec, titleText, "");
		else
			titleWithTitlePaintListener.setTitleAndImage(titleText, tooltipText, titleSpec);
		getInnerBody().setBackground(titleSpec.background);
		getBody().setBackground(titleSpec.background);
		if (this instanceof IDataCompositeWithFooter) {
			IHasControl footer = ((IDataCompositeWithFooter<?, ?>) this).getFooter();
			if (footer != null)
				footer.getControl().setBackground(titleSpec.background);
		}
	}

}