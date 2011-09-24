package org.softwareFm.display.actions;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;
import org.softwareFm.display.IFeedCallback;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class BrowseRssAction implements IAction {

	private final ClassPathResource transformerResource;

	public BrowseRssAction() {
		transformerResource = new ClassPathResource("RssToHtml.xslt", getClass());
	}

	@Override
	public void execute(final ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) {
		final String key = Actions.getDataKey(displayerDefn, actionData.formalParams);
		final String param = Actions.getString(actionContext, key, index);
		if (param != null) {
			actionContext.browserService.processUrl(param, new IFeedCallback() {
				@Override
				public void process(int statusCode, String page) {
					try {
						String result = rssToHtml(page, transformerResource);
						System.out.println("internal rss browsed: " + param);
						actionContext.internalBrowser.process(result);
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}

			});
		}
	}

	public static String rssToHtml(String page, Resource transformerResource) throws JDOMException, IOException, XSLTransformException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(page));

		XSLTransformer transformer = new XSLTransformer(transformerResource.getInputStream());
		Document doc2 = transformer.transform(doc);

		// Write the resulting document to file 'dvds.htm'
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		StringWriter stringWriter = new StringWriter();
		out.output(doc2, stringWriter);
		return stringWriter.toString();
	}
}
