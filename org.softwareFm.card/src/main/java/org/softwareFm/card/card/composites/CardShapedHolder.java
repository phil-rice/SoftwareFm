package org.softwareFm.card.card.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;

public class CardShapedHolder<Body extends IHasControl> implements IHasComposite {

	private final CardShapedComposite composite;
	private final IHasControl title;
	private final Body body;

	static class CardShapedLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			return Swts.computeSizeForVerticallyStackedComposites(wHint, hHint, composite.getChildren());
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			CardShapedComposite csc = (CardShapedComposite) composite;
			CardConfig cc = csc.cc;
			TitleSpec ts = csc.titleSpec;
			Rectangle ca = composite.getClientArea();
			int x = ca.x;
			int y = ca.y;

			csc.getTitle().setBounds(x, y, ca.width - ts.rightIndent, cc.titleHeight);
			csc.getBody().setBounds(x, y + cc.titleHeight, ca.width, ca.height - cc.titleHeight);
		}

	}

	static class CardShapedPaintListener implements PaintListener {
		private final TitleSpec ts;
		private final CardConfig cc;

		public CardShapedPaintListener(TitleSpec titleSpec, CardConfig cardConfig) {
			this.ts = titleSpec;
			this.cc = cardConfig;
		}

		@Override
		public void paintControl(PaintEvent e) {
			Composite cardComposite = (Composite) e.widget;
			Rectangle clientArea = cardComposite.getClientArea();
			drawLeftAndBottom(e, clientArea);
			drawRight(e, clientArea);
			drawTop(e, clientArea);

		}

		private void drawLeftAndBottom(PaintEvent e, Rectangle ca) {
			Rectangle clipRectangle = new Rectangle(ca.x - cc.cornerRadiusComp, ca.y + cc.cornerRadius - cc.cornerRadiusComp,//
					ca.width - cc.cornerRadius + cc.cornerRadiusComp, ca.height + cc.cornerRadiusComp + cc.cornerRadius);
			e.gc.setClipping(clipRectangle);
			// notifyListeners("drawLeftBottom-clip", clipRectangle);
			int x = ca.x - cc.cornerRadiusComp;
			int y = ca.y - cc.cornerRadius - cc.cornerRadiusComp;
			int width = ca.width + 2 * cc.cornerRadiusComp;
			int height = ca.height + cc.cornerRadius + 2 * cc.cornerRadiusComp;
			e.gc.drawRoundRectangle(x, y, width, height, cc.cornerRadius, cc.cornerRadius);
			// notifyListeners("drawLeftBottom-round", new Rectangle(x, y, width, height));
		}

		private void drawRight(PaintEvent e, Rectangle ca) {
			int comp = cc.cornerRadiusComp;
			Rectangle clipRectangle = new Rectangle(ca.x + ca.width - ts.rightIndent + comp, ca.y - comp, ca.width + 2 * comp, ca.height + 2 * comp + 1);
			e.gc.setClipping(clipRectangle); // way to wide...but who cares. Don't know why need +1, but without it bottom right doesnt appear
			// notifyListeners("drawRight-clip", clipRectangle);

			int x = ca.x + ca.width - ts.rightIndent -comp;
			int y = ca.y - comp;
			int width = ts.rightIndent+2*comp;
			int height = ca.height - cc.titleHeight + 2 * comp;
			e.gc.drawRoundRectangle(x, y + cc.titleHeight, width, height, cc.cornerRadius, cc.cornerRadius);
			// notifyListeners("drawRight-round", new Rectangle(x, y, width, height));
		}

		private void drawTop(PaintEvent e, Rectangle ca) {
			int comp = cc.cornerRadiusComp;
			Rectangle clipRectangle = new Rectangle(0, 0, ca.x + ca.width - ts.rightIndent + cc.rightMargin, cc.topMargin + cc.titleHeight - comp);
			e.gc.setClipping(clipRectangle);
			e.gc.drawRoundRectangle(ca.x - comp, ca.y - comp, ca.width - ts.rightIndent + comp * 2, cc.titleHeight + comp, cc.cornerRadius, cc.cornerRadius);

		}

	}

	static class CardShapedComposite extends Composite {
		private final CardConfig cc;
		private TitleSpec titleSpec;
		private CardShapedPaintListener listener;

		public CardShapedComposite(Composite parent, int style, CardConfig cardConfig, TitleSpec titleSpec) {
			super(parent, style);
			this.cc = cardConfig;
			setTitleSpec(titleSpec);
		}

		@Override
		public Rectangle getClientArea() {
			Rectangle ca = super.getClientArea();
			return new Rectangle(ca.x + cc.leftMargin, ca.y + cc.topMargin, ca.width - cc.leftMargin - cc.rightMargin, ca.height - cc.topMargin - cc.bottomMargin);
		}

		private void setTitleSpec(TitleSpec titleSpec) {
			this.titleSpec = titleSpec;
			if (listener != null)
				removePaintListener(listener);
			if (getChildren().length > 0) {
				getTitle().setBackground(titleSpec.titleColor);
				getBody().setBackground(titleSpec.background);
				layout(true);
			}
			addPaintListener(listener = new CardShapedPaintListener(titleSpec, cc));
		}

		protected Control getTitle() {
			assert getChildren().length == 2;
			return getChildren()[0];
		}

		protected Control getBody() {
			assert getChildren().length == 2;
			return getChildren()[1];
		}

	}

	public CardShapedHolder(Composite parent, CardConfig cardConfig, IFunction1<Composite, IHasControl> titleMaker, IFunction1<Composite, Body> bodyMaker) {
		this(parent, cardConfig, TitleSpec.noTitleSpec(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND)), titleMaker, bodyMaker);
	}

	public CardShapedHolder(Composite parent, CardConfig cardConfig, TitleSpec titleSpec, IFunction1<Composite, IHasControl> titleMaker, IFunction1<Composite, Body> bodyMaker) {
		super();
		this.composite = new CardShapedComposite(parent, SWT.NULL, cardConfig, titleSpec);
		title = Functions.call(titleMaker, composite);
		body = Functions.call(bodyMaker, composite);
		composite.setTitleSpec(titleSpec);
		composite.setLayout(new CardShapedLayout());
	}

	protected void setTitleSpec(TitleSpec titleSpec) {
		composite.setTitleSpec(titleSpec);
	}

	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public Composite getComposite() {
		return composite;
	}

	public static void main(String[] args) {

		Swts.Show.display(CardShapedHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				CardShapedHolder<IHasControl> cardShapedHolder = new CardShapedHolder<IHasControl>(from, cardConfig, Swts.labelFn("Title"), Swts.styledTextFn("body", SWT.NULL));
				cardShapedHolder.setTitleSpec(TitleSpec.noTitleSpec(getColor(from, SWT.COLOR_CYAN), getColor(from, SWT.COLOR_GREEN)));
				return cardShapedHolder.getComposite();
			}

			private Color getColor(final Composite from, int color) {
				return from.getDisplay().getSystemColor(color);
			}
		});
	}

}
