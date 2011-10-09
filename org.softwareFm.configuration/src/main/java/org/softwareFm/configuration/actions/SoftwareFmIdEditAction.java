package org.softwareFm.configuration.actions;

import java.util.Map;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.IAction;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;

public class SoftwareFmIdEditAction implements IAction {
	private final String editorKey;

	public SoftwareFmIdEditAction(String editorKey) {
		this.editorKey = editorKey;
	}

	@Override
	public void execute(final ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, final ActionData actionData) throws Exception {
		IDataGetter dataGetter = actionContext.dataGetter;
		Object initialValue = Maps.makeMap(ConfigurationConstants.groupId, dataGetter.getDataFor(ConfigurationConstants.dataJarGroupId),//
				ConfigurationConstants.artifactId, dataGetter.getDataFor(ConfigurationConstants.dataJarArtifactId),//
				ConfigurationConstants.version,dataGetter.getDataFor( ConfigurationConstants.dataJarVersion));

		actionContext.editorFactory.displayEditor(displayer, editorKey, displayerDefn, actionContext, actionData, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				String url = actionData.urlMap.get(ConfigurationConstants.primaryEntity);
				@SuppressWarnings("unchecked")
				Map<String,Object> map = (Map<String, Object>) t;
				actionContext.updateStore.update(ConfigurationConstants.primaryEntity, url, map);
			}
		}, initialValue);

	}

}
