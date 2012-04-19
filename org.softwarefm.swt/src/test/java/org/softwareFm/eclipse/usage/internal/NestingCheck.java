/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.LocalConfig;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;
import org.softwareFm.jarAndClassPath.api.ISoftwareFmApiFactory;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.ISwtSoftwareFmFactory;
import org.softwareFm.swt.swt.Swts;

public class NestingCheck {

	public static void main(String[] args) {
		Swts.Show.display(NestingCheck.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ITransactionManager transactionManager = ISwtSoftwareFmFactory.Utils.getSwtTransactionManager(from.getDisplay(), CommonConstants.threadPoolSizeForTests, CommonConstants.testTimeOutMs);
				LocalConfig localConfig = ISoftwareFmApiFactory.Utils.getLocalConfig(false, CommonConstants.clientTimeOut);
				final IContainer container = ICrowdSourcedApi.Utils.forClient(localConfig, transactionManager).makeContainer();
				Composite composite = new Composite(from, SWT.NULL);
				Swts.Buttons.makePushButton(composite, "One", new Runnable() {
					@Override
					public void run() {
						System.out.println("one");
						String root = container.accessWithCallbackFn(IGitReader.class, new IFunction1<IGitReader, File>() {
							@Override
							public File apply(IGitReader reader) throws Exception {
								return reader.getRoot().getAbsoluteFile();
							}
						}, new ISwtFunction1<File, String>() {
							@Override
							public String apply(File from) throws Exception {
								return from.toString();
							}
						}).get();
						System.out.println("root is: " + root);
					}
				});
				Swts.Buttons.makePushButton(composite, "Two", new Runnable() {
					@Override
					public void run() {
						System.out.println("two");
						String root = container.accessWithCallbackFn(IGitReader.class, new IFunction1<IGitReader, File>() {
							@Override
							public File apply(IGitReader reader) throws Exception {
								return container.access(IGitReader.class, new IFunction1<IGitReader, File>() {
									@Override
									public File apply(IGitReader from) throws Exception {
										return from.getRoot().getAbsoluteFile();
									}
								}).get(10);
							}
						}, new ISwtFunction1<File, String>() {
							@Override
							public String apply(final File file) throws Exception {
								return container.access(IGitReader.class, new IFunction1<IGitReader, String>() {
									@Override
									public String apply(IGitReader gitReader) throws Exception {
										return file.toString();
									}
								}).get(10);
							}
						}).get();
						System.out.println("root is: " + root);
					}
				});
				Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}
		});
	}
}