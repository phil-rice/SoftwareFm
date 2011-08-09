package org.arc4eclipse.debugMessagePanel.views;

import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.FoundClassPathEntry;
import org.arc4eclipse.jdtBinding.api.JavaProjects;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.text.TitleAndStyledTextField;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.indent.Indent;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class DebugBindingPanel extends Composite implements ISelectedBindingListener {
	private final TitleAndStyledTextField titleAndStyledTextField;

	public DebugBindingPanel(Composite parent, int style, IImageFactory imageFactory, ISelectedBindingManager selectedBindingManager) {
		super(parent, style);
		setLayout(new FormLayout());

		titleAndStyledTextField = new TitleAndStyledTextField(this, SWT.NONE, imageFactory, "Binding");
		FormData fd_titleAndStyledTextField = new FormData();
		fd_titleAndStyledTextField.bottom = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.right = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.top = new FormAttachment(0, 0);
		fd_titleAndStyledTextField.left = new FormAttachment(0, 0);
		titleAndStyledTextField.setLayoutData(fd_titleAndStyledTextField);
		selectedBindingManager.addSelectedArtifactSelectionListener(this);
	}

	@Override
	public void selectionOccured(final ITypeBinding binding, final BindingRipperResult ripperResult) {
		getDisplay().asyncExec(new Runnable() {
			StringBuilder builder = new StringBuilder();

			@Override
			public void run() {
				try {
					add("Binding", binding);
					if (binding != null) {
						IJavaElement element = binding.getJavaElement();
						add("Element", element);
						if (element != null) {
							IJavaModel javaModel = element.getJavaModel();
							IJavaProject javaProject = element.getJavaProject();
							IResource resource = element.getResource();
							add("Parent", element.getParent());
							IJavaElement primaryElement = element.getPrimaryElement();
							add("Primary", primaryElement);
							add("Path", element.getPath());
							add("Resource", resource);
							add("Underlying Resource", element.getUnderlyingResource());
							add("Corr Resource", element.getCorrespondingResource());
							add("JavaModel", javaModel);
							add("JavaProject", javaProject);
							if (javaProject != null) {

								IPackageFragmentRoot packageFragmentRoot = javaProject.findPackageFragmentRoot(element.getPath());
								add("PackageFragmentRoot", packageFragmentRoot);
								if (packageFragmentRoot != null) {
									add("Src Att Path", packageFragmentRoot.getSourceAttachmentPath());
									add("Src Att Root Path", packageFragmentRoot.getSourceAttachmentRootPath());
									IClasspathEntry classpathEntry = packageFragmentRoot.getResolvedClasspathEntry();
									FoundClassPathEntry foundClassPathEntry = JavaProjects.findClassPathEntry(javaProject, classpathEntry);
									add("FoundClassPathEntry", foundClassPathEntry);
									add("ClassPathEntry", classpathEntry);
									if (classpathEntry != null) {
										IClasspathAttribute[] extraAttributes = classpathEntry.getExtraAttributes();
										add("Content Kind", classpathEntry.getContentKind());
										add("Entry Kind", classpathEntry.getEntryKind());
										for (IClasspathAttribute classpathAttribute : extraAttributes)
											add("  " + classpathAttribute.getName(), classpathAttribute.getValue());
										add("Source attachmentpath", classpathEntry.getSourceAttachmentPath());
										add("Source attachmentroot path", classpathEntry.getSourceAttachmentRootPath());
										if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
											IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(classpathEntry.getPath(), javaProject);
											add("Container", classpathContainer);
										}
									}
								}
								display(javaProject, javaProject.getRawClasspath(), new Indent());
							}
						}

					}
				} catch (JavaModelException e) {
					throw WrappedException.wrap(e);
				}
				titleAndStyledTextField.setText(builder.toString() + "\n" + ripperResult.toString());
			}

			private void display(IJavaProject javaProject, IClasspathEntry[] rawClasspath, Indent indent) throws JavaModelException {
				for (IClasspathEntry rawCasspathEntry : rawClasspath) {
					add(indent.prefix() + "RawClassPathEntry", rawCasspathEntry);
					IClasspathEntry resolvedClasspathEntry = JavaCore.getResolvedClasspathEntry(rawCasspathEntry);
					if (resolvedClasspathEntry != rawCasspathEntry)
						add(indent.prefix() + "--Resolved to ", resolvedClasspathEntry);
					if (rawCasspathEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
						IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(rawCasspathEntry.getPath(), javaProject);
						display(javaProject, classpathContainer.getClasspathEntries(), indent.indent());
					}
				}
			}

			private void add(String name, Object element) {
				builder.append(String.format("%-20s %s\n", name + ":", Strings.oneLine(element)));
			}
		});
	}
}
