package org.softwarefm.eclipse.annotations;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.softwarefm.eclipse.Jdts;
import org.softwarefm.eclipse.jobs.Jobs;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.maps.Maps;

public class MarkUpResource {
	private final IMarkerStore store;
	private final IJavaElementToUrl javaElementToUrl;

	public MarkUpResource(IMarkerStore store, IJavaElementToUrl javaElementToUrl) {
		this.store = store;
		this.javaElementToUrl = javaElementToUrl;
	}

	public void markup(final IFile file, AbstractDecoratedTextEditor editor) {
		ITypeRoot typeRoot = Jdts.getTypeRoot(file, editor);
		final Map<String, IMarkerCallback> callbacks = Maps.newMap(LinkedHashMap.class);//linked for debugging
		TypeRootWalker.visit(typeRoot, new ITypeRootVistor() {
			@Override
			public void visitType(String sfmTypeId, final IType type) throws JavaModelException {
				callbacks.put(sfmTypeId, mark(file, sfmTypeId, type.getNameRange()));
			}

			@Override
			public void visitMethod(final String sfmMethodId, IType type, final IMethod method) throws JavaModelException {
				callbacks.put(sfmMethodId, mark(file, sfmMethodId, method.getNameRange()));
			}
		}, javaElementToUrl);
		Jobs.run("Loading markers for " + (file == null?" internal ":file.getFullPath()), new Runnable() {
			@Override
			public void run() {
				for (Entry<String, IMarkerCallback> entry : callbacks.entrySet())
					store.makerFor(entry.getKey(), entry.getValue());
			}
		});

	}

	private IMarkerCallback mark(final IFile file, final String sfmId, final ISourceRange nameRange) {
		return new IMarkerCallback() {
			@Override
			public void mark(final String type, final String sfmId, final String markerValue) {
				if (markerValue != null && file != null) //TODO 'Internal classes come here with a null file - because they were a null file, don't know how to add annotations to them
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								IMarker marker = findExistingMarkerOrNull(file, sfmId, type);
								if (marker == null) {
									marker = file.createMarker(type);
									marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
									marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
									marker.setAttribute("SfmId", sfmId);
									marker.setAttribute("SfmType", type);
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

	private IMarker findExistingMarkerOrNull(IFile file, String typeString, String type) throws CoreException {
		for (IMarker marker : file.findMarkers(type, true, IResource.DEPTH_ZERO)) {
			Object existingId = marker.getAttribute("SfmId");
			Object existingType = marker.getAttribute("SfmType");
			if (typeString.equals(existingId) && type.equals(existingType))
				return marker;
		}
		return null;
	}
}
