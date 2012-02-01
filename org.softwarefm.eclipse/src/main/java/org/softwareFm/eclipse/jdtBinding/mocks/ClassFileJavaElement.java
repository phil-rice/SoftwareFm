/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding.mocks;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IBufferFactory;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICodeCompletionRequestor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;

@SuppressWarnings("deprecation")
public class ClassFileJavaElement extends JavaElementAdapter implements IClassFile {

	public ClassFileJavaElement(Path path, JavaElementData parentData, JavaElementData childData) {
		super(path, parentData, childData);
	}

	@Override
	public IType findPrimaryType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement getElementAt(int arg0) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ICompilationUnit getWorkingCopy(WorkingCopyOwner arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement[] getChildren() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChildren() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String findRecommendedLineSeparator() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IBuffer getBuffer() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasUnsavedChanges() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isConsistent() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOpen() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void makeConsistent(IProgressMonitor arg0) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void open(IProgressMonitor arg0) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSource() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISourceRange getSourceRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void codeComplete(int arg0, ICodeCompletionRequestor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void codeComplete(int arg0, ICompletionRequestor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void codeComplete(int arg0, CompletionRequestor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void codeComplete(int arg0, CompletionRequestor arg1, IProgressMonitor arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void codeComplete(int arg0, ICompletionRequestor arg1, WorkingCopyOwner arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void codeComplete(int arg0, CompletionRequestor arg1, WorkingCopyOwner arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void codeComplete(int arg0, CompletionRequestor arg1, WorkingCopyOwner arg2, IProgressMonitor arg3) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement[] codeSelect(int arg0, int arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement[] codeSelect(int arg0, int arg1, WorkingCopyOwner arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ICompilationUnit becomeWorkingCopy(IProblemRequestor arg0, WorkingCopyOwner arg1, IProgressMonitor arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getBytes() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement getWorkingCopy(IProgressMonitor arg0, IBufferFactory arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isClass() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInterface() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISourceRange getNameRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

}