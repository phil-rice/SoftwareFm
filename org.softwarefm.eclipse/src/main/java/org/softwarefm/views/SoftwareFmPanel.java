package org.softwarefm.views;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.constants.MessageKeys;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.swt.HasComposite;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

abstract public class SoftwareFmPanel extends HasComposite {
	private final List<ISelectedBindingListener> listeners = Lists.newList();
	private final ISelectedBindingManager<?> selectionBindingManager;
	private final IResourceGetter resourceGetter;

	public SoftwareFmPanel(Composite parent, SoftwareFmContainer<?> container) {
		super(parent);
		this.selectionBindingManager = container.selectedBindingManager;
		this.resourceGetter = container.resourceGetter;
	}

	protected void addListener(ISelectedBindingListener listener) {
		listeners.add(listener);
		selectionBindingManager.addSelectedArtifactSelectionListener(listener);

	}

	public String msg(String key, Object... args) {
		return IResourceGetter.Utils.getMessageOrException(resourceGetter, key, args);
	}

	public String searchingMsg() {
		return Strings.oneLine(msg(MessageKeys.search));
	}

	public String digestDeterminedMsg(FileNameAndDigest fileNameAndDigest) {
		assert fileNameAndDigest.digest != null : fileNameAndDigest;
		return msg(MessageKeys.foundDigest, fileNameAndDigest.fileName, Strings.firstNCharacters(fileNameAndDigest.digest, 6), fileNameAndDigest.digest);
	}

	public String unknownDigestMsg(FileNameAndDigest fileNameAndDigest){
		assert fileNameAndDigest.digest != null : fileNameAndDigest;
		return msg(MessageKeys.unrecognisedDigest, fileNameAndDigest.fileName, Strings.firstNCharacters(fileNameAndDigest.digest, 6), fileNameAndDigest.digest);
	}
	
	public String projectDeterminedMsg(ProjectData projectData) {
		FileNameAndDigest fileNameAndDigest = projectData.fileNameAndDigest;
		return msg(MessageKeys.foundProjectData, //
				fileNameAndDigest.fileName, Strings.firstNCharacters(fileNameAndDigest.digest, 6), fileNameAndDigest.digest,//
				projectData,//
				projectData.groupId, projectData.artefactId, projectData.version);

	}

	public String notInAJarMsg(FileNameAndDigest fileNameAndDigest) {
		assert fileNameAndDigest.digest == null : fileNameAndDigest;
		return msg(MessageKeys.notAJar, fileNameAndDigest.fileName);

	}

	@Override
	public void dispose() {
		for (ISelectedBindingListener listener : listeners)
			selectionBindingManager.removeSelectedArtifactSelectionListener(listener);
		super.dispose();
	}

	public ISelectedBindingManager<?> getSelectionBindingManager() {
		return selectionBindingManager;
	}

	protected String digestUrl(FileNameAndDigest fileNameAndDigest) {
		return CommonConstants.softwareFmHostAndPrefix + "digest/" + fileNameAndDigest.digest;
	}

	public String notJavaElementMsg() {
		return msg(MessageKeys.notJavaElement);
	}

}
