/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.rss;

import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.XSLTransformer;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.display.swt.Swts.Show;
import org.springframework.core.io.ClassPathResource;

public class RssFeedTransformer implements IFunction1<String, String> {

	private final ClassPathResource transformerResource;

	public RssFeedTransformer() {
		transformerResource = new ClassPathResource("RssToHtml.xslt", getClass());
	}

	@Override
	public String apply(String page) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		String clean = page.replaceAll("<\\?xml-stylesheet.*\\?>", "").trim();

		Document doc = builder.build(new StringReader(clean));

		XSLTransformer transformer = new XSLTransformer(transformerResource.getInputStream());
		Document doc2 = transformer.transform(doc);

		// Write the resulting document to file 'dvds.htm'
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		StringWriter stringWriter = new StringWriter();
		out.output(doc2, stringWriter);
		return stringWriter.toString();
	}

	public static void main(String[] args) {
		Show.display("Browser", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Browser browser = new Browser(from, SWT.NULL);
				String rss = Files.getTextFromClassPath(getClass(), "RawGeekzone.xml");
				System.out.println(rss);
				RssFeedTransformer rssFeedTransformer = new RssFeedTransformer();
				String html = rssFeedTransformer.apply(rss);
				browser.setText(html);
				System.out.println(html);
				return browser;
			}
		});
	}

}