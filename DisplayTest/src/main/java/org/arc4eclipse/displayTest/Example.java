package org.arc4eclipse.displayTest;

import java.util.Arrays;

import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayCore.api.IStyleAcceptor;
import org.arc4eclipse.displayText.DisplayText;
import org.arc4eclipse.utilities.json.Json;
import org.arc4eclipse.utilities.maps.Maps;

public class Example {

	public static void main(String[] args) {
		IDisplayContainerFactoryBuilder builder = IDisplayContainerFactoryBuilder.Utils.displayManager();


		// this can be built from
		
		//this is for validation
		
		builder.registerEntity("jar");
		builder.registerEntity("organisation");
		builder.registerEntity("project");
		
		builder.registerDisplayer("top", new DisplayText()); // this is the search bar at the top
		builder.registerDisplayer("text", new DisplayText());
		builder.registerDisplayer("digest", new DisplayJarDetails());
		builder.registerDisplayer("url", new DisplayUrl());
		builder.registerDisplayer("container", new DisplayContainer());
		builder.registerDisplayer("attributeValueList", new DisplayAttributeValueList());
		
		builder.registerEditor("displayOnlyText", String.class, new NoEditorText());
		builder.registerEditor("text", String.class, new EditText());
		builder.registerEditor("styledText", String.class, new EditStyledText());
		builder.registerEditor("url", String.class, new EditText());
		
		builder.registerLineEditor("attributeValuePair", String.class, new EditAttributeValuePair());
		
		builder.registerValidator("url", new UrlValidator());
		builder.registerValidator("mail", new MailValidator());
		builder.registerAttributeValuePairValidator("valueIsUrl", new ValueIsUrlValidator());
		builder.registerAttributeValuePairValidator("valueIsMail", new ValueIsMailValidator());
		
		{"jar": [{"key":"digest", "display":"top"},
				{"key":"projectUrl", "display":"container", "entity":"project"},
				{"key":"organisationUrl", "display":"container", "entity":"organisation", "validator":"url"}],
	     "project": [{"key":"url", "display":"top"},
	                 {"key":"name", "display":"text"},
	                 {"key":"description", "display": "styledText"},
	                 {"key":"issues", "display":"attributeValueList", "lineEditor":"attributeValuePair", "lineValidator":"valueIsMail"}]
		 "organisation": [{"key":"url", "display":"top"},
		             {"key":"name", "display":"text"},
		             {"key":"description", "display": "styledText"},
		             {"key":"issues", "display":"attributeValueList", "lineEditor":"attributeValuePair", "lineValidator":"valueIsMail"}]
	     
	}
}
