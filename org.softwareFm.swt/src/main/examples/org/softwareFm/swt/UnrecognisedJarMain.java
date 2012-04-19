/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.composites.CardShapedHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.internal.SoftwareFmCardConfigurator;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.unrecognisedJar.GroupidArtifactVersion;
import org.softwareFm.swt.unrecognisedJar.UnrecognisedJar;
import org.softwareFm.swt.unrecognisedJar.UnrecognisedJarData;

public class UnrecognisedJarMain {
	public static void main(String[] args) {

		Swts.Show.display(UnrecognisedJar.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardConfig cardConfig = new SoftwareFmCardConfigurator().configure(from.getDisplay(), new CardConfig(ICardFactory.Utils.cardFactory(), CardDataStoreFixture.rawCardStore()));
				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(Swts.titleAndContentLayout(30));
				Composite titleBar = new Composite(composite, SWT.NULL);
				titleBar.setLayout(new RowLayout());
				CardShapedHolder<UnrecognisedJar> holder = new CardShapedHolder<UnrecognisedJar>(composite, cardConfig, UnrecognisedJar.makeTitleSpec(cardConfig), Swts.labelFn("Title"), new IFunction1<Composite, UnrecognisedJar>() {
					@Override
					public UnrecognisedJar apply(Composite from) throws Exception {
						UnrecognisedJar unrecognisedJar = new UnrecognisedJar(from, cardConfig, UnrecognisedJarData.forTests("projectName", new File("artifactId-1.0.0.jar")), ICallback.Utils.<GroupidArtifactVersion> sysoutCallback());
						return unrecognisedJar;
					}
				});
				for (int i = 0; i < 3; i++)
					makeButton(titleBar, holder, i);
				Swts.Row.setRowDataFor(30, SWT.DEFAULT, titleBar.getChildren());
				return composite;
			}

			private Button makeButton(Composite titleBar, final CardShapedHolder<UnrecognisedJar> holder, final int count) {
				return Swts.Buttons.makePushButton(titleBar, null, Integer.toString(count), false, new Runnable() {
					@Override
					public void run() {
						List<GroupidArtifactVersion> gavs = Lists.newList();
						for (int i = 0; i < count; i++)
							gavs.add(new GroupidArtifactVersion("groupid" + i, "artifactId", "version" + i));
						holder.getBody().populate(gavs);
					}
				});
			}

		});
	}

}