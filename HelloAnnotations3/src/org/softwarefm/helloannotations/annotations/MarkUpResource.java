package org.softwarefm.helloannotations.annotations;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.softwarefm.eclipse.Jdts;
import org.softwarefm.eclipse.jobs.Jobs;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.maps.Maps;

public class MarkUpResource {

	IMarkerStore store;
	private final String markerType;

	public MarkUpResource(IMarkerStore store, String markerType) {
		this.store = store;
		this.markerType = markerType;
	}

	public IPartListener markUpListener() {
		return new JavaFilePartAdapter() {
			@Override
			protected void javaEditorOpened(AbstractDecoratedTextEditor editor, IFile file) {
				markup(file, editor);
			}
		};
	}

	public void markup(final IFile file, AbstractDecoratedTextEditor editor) {
		ICompilationUnit compilationUnit = Jdts.getCompilationUnit(file.getFullPath());
		final Map<String, ICallback<String>> callbacks = Maps.newMap();
		SoftwareFmCompilationUnitWalker.visit(compilationUnit, new ISoftwareFmCompilationUnitVistor() {
			@Override
			public void visitType(String sfmTypeId, final IType type) throws JavaModelException {
				callbacks.put(sfmTypeId, mark(file, sfmTypeId, type.getNameRange()));
			}

			@Override
			public void visitMethod(final String sfmMethodId, IType type, final IMethod method) throws JavaModelException {
				callbacks.put(sfmMethodId, mark(file, sfmMethodId, method.getNameRange()));
			}
		});
		Jobs.run("Loading markers for " + file.getFullPath(), new Runnable() {
			public void run() {
				for (Entry<String, ICallback<String>> entry: callbacks.entrySet())
					store.makerFor(entry.getKey(), entry.getValue());
			}
		});

	}

	private ICallback<String> mark(final IFile file, final String sfmId, final ISourceRange nameRange) {
		return new ICallback<String>() {
			@Override
			public void process(final String markerValue) throws Exception {
				if (markerValue != null)
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							try {
								IMarker marker = findExistingMarkerOrNull(file, sfmId);
								if (marker == null) {
									marker = file.createMarker(markerType);
									marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
									marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
									marker.setAttribute("SfmId", sfmId);
								}
								marker.setAttribute(IMarker.MESSAGE, markerValue);
								marker.setAttribute(IMarker.CHAR_START, nameRange.getOffset());
								marker.setAttribute(IMarker.CHAR_END, nameRange.getOffset() + nameRange.getLength());
							} catch (CoreException e) {
								throw WrappedException.wrap(e);
							}
						}

					});
			}
		};
	}

	@SuppressWarnings("unchecked")
	private IMarker findExistingMarkerOrNull(IFile file, String typeString) throws CoreException {
		for (IMarker marker : file.findMarkers(markerType, true, IResource.DEPTH_ZERO)) {
			Object existingId = marker.getAttribute("SfmId");
			if (typeString.equals(existingId))
				return marker;
		}
		return null;
	}
}
