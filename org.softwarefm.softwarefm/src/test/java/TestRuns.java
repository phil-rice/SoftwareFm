

import junit.framework.TestCase;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class TestRuns extends TestCase{

	public void test() throws ClassNotFoundException{
		Class.forName(AbstractUIPlugin.class.getName());
	}
	
}