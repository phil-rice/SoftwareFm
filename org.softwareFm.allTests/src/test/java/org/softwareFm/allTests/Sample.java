/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.allTests;

import java.io.File;

import junit.framework.TestSuite;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.Ant;
import org.codehaus.cake.forkjoin.RecursiveTask;
import org.easymock.EasyMock;
import org.jdom.JDOMFactory;
import org.softwareFm.client.http.api.IHttpClient;

import twitter4j.Tweet;
import de.java2html.Java2Html;

public class Sample {

	@SuppressWarnings("unused")
	/** This is my comment about x*/
	public static void main(String[] args) {
		RecursiveTask<String> task;
		File file = new File("");
		Tweet tweet;
		IHttpClient client;
		HttpClient client2;
		Logger logger = Logger.getLogger(Sample.class);
		new Ant();
		TestSuite suite;
		new TestSuite();
		Math.random();
		EasyMock.createMock(IHttpClient.class);
		JDOMFactory factory;
		Java2Html html;
	}
}