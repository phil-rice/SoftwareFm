package org.softwarefm.httpServer;

import java.text.MessageFormat;

import org.softwarefm.utilities.arrays.ArrayHelper;
import org.softwarefm.utilities.functions.IFunction1;

public interface IRegistryConfigurator {   
     
	void registerWith(IHttpRegistry registry);    
	  
	public static class Utils{    
		  
		public static String defn(String pattern, String...fields){
			String[] params = ArrayHelper.map(String.class, fields, new IFunction1<String,String>() {
				@Override 
				public String apply(String from) throws Exception { 
					return "[" + from +"]";    
				}  
			}); 
			
			return MessageFormat.format(pattern, (Object[])params);
		}
	}
	 
} 
  