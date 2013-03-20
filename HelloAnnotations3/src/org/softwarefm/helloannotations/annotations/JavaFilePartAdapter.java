package org.softwarefm.helloannotations.annotations;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

public abstract class JavaFilePartAdapter extends PartAdapter{
	
	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof AbstractDecoratedTextEditor){
			AbstractDecoratedTextEditor editor = (AbstractDecoratedTextEditor) part;
			IResource resource = ResourceUtil.getResource(editor.getEditorInput());
			if (resource instanceof IFile ){
				IFile file = (IFile) resource;
				if (file.getFileExtension().equals("java")){
					javaEditorOpened(editor, file);
					
				}
			}
				
			
		}
	}

	abstract protected void javaEditorOpened(AbstractDecoratedTextEditor editor, IFile file);

}
