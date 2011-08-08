package org.arc4eclipse.displayJavadoc;

import java.util.ArrayList;
import java.util.List;

import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.FoundClassPathEntry;
import org.arc4eclipse.jdtBinding.api.JavaProjects;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class JavadocPanel extends Composite {

	private final ImageButton btnAttach;
	private final TitleAndTextField txtLocal;
	private final BoundTitleAndTextField txtRepository;
	private BindingRipperResult ripped;

	public JavadocPanel(Composite parent, int style, DisplayerContext context, String entity, NameSpaceAndName nameSpaceAndName, String title) {
		super(parent, style);
		setLayout(new GridLayout());
		txtRepository = new BoundTitleAndTextField(this, SWT.NULL, context, entity, nameSpaceAndName, title);
		btnAttach = txtRepository.addButton(context.imageFactory.makeImages(getDisplay()).getLinkImage(), "Attach", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Linking ");
				setJavadocLocationAttribute(ripped.javaProject, ripped.classpathEntry);
			}
		});
		txtRepository.addHelpButton(DisplayJavadocConstants.helpValueInRepository);

		txtLocal = new TitleAndTextField(this, SWT.NULL, context.imageFactory, "Current setting", false);
		txtLocal.addHelpButton(DisplayJavadocConstants.helpCurrentValue);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
	}

	public void setValue(String url, BindingRipperResult ripped, String value) {
		this.ripped = ripped;
		txtRepository.setUrl(url);
		txtRepository.setText(value);
		txtLocal.setText(value);
		btnAttach.setEnabled(!"".equals(value));
	}

	private void setJavadocLocationAttribute(IJavaProject project, IClasspathEntry entry) {
		assert ripped != null;
		FoundClassPathEntry found = JavaProjects.findClassPathEntry(project, entry);
		System.out.println("Setting. Found: " + found);
		JavaProjects.updateFoundClassPath(found, new IFunction1<IClasspathEntry, IClasspathEntry>() {
			@Override
			public IClasspathEntry apply(IClasspathEntry from) throws Exception {
				IClasspathAttribute[] extraAttributes = from.getExtraAttributes();
				List<IClasspathAttribute> newAttributes = new ArrayList<IClasspathAttribute>();
				boolean found = false;
				IClasspathAttribute newClasspathAttribute = JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, txtRepository.getText());
				for (IClasspathAttribute oldAttribute : extraAttributes)
					if (oldAttribute.getName().equals(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME)) {
						newAttributes.add(newClasspathAttribute);
						found = true;
					} else
						newAttributes.add(oldAttribute);
				if (!found)
					newAttributes.add(newClasspathAttribute);
				IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(from.getPath(), from.getSourceAttachmentPath(), from.getSourceAttachmentRootPath(), from.getAccessRules(), newAttributes.toArray(new IClasspathAttribute[0]), from.isExported());
				return newLibraryEntry;
			}
		});
	}
	// public static IClasspathEntry configureJavadocLocation(Shell shell, IClasspathEntry initialEntry) {
	// if (initialEntry == null) {
	// throw new IllegalArgumentException();
	// }
	// int entryKind = initialEntry.getEntryKind();
	// if (entryKind != IClasspathEntry.CPE_LIBRARY && entryKind != IClasspathEntry.CPE_VARIABLE) {
	// throw new IllegalArgumentException();
	// }
	//
	// URL location = JavaUI.getLibraryJavadocLocation(initialEntry);
	// JavadocLocationDialog dialog = new JavadocLocationDialog(shell, initialEntry.getPath().toString(), location);
	// if (dialog.open() == Window.OK) {
	// CPListElement element = CPListElement.createFromExisting(initialEntry, null);
	// URL res = dialog.getResult();
	// element.setAttribute(CPListElement.JAVADOC, res != null ? res.toExternalForm() : null);
	// return element.getClasspathEntry();
	// }
	// return null;
	// }
	//
	// public static CPListElement[] createFromExisting(IJavaProject project) throws JavaModelException {
	// IClasspathEntry[] rawClasspath = project.getRawClasspath();
	// CPListElement[] result = new CPListElement[rawClasspath.length];
	// for (int i = 0; i < rawClasspath.length; i++) {
	// result[i] = CPListElement.createFromExisting(rawClasspath[i], project);
	// }
	// return result;
	// }
	//
	// public CPListElementAttribute setAttribute(String key, Object value) {
	// CPListElementAttribute attribute = findAttributeElement(key);
	// if (attribute == null) {
	// return null;
	// }
	// if (key.equals(EXCLUSION) || key.equals(INCLUSION)) {
	// Assert.isTrue(value != null || fEntryKind != IClasspathEntry.CPE_SOURCE);
	// }
	//
	// if (key.equals(ACCESSRULES)) {
	// Assert.isTrue(value != null || fEntryKind == IClasspathEntry.CPE_SOURCE);
	// }
	// if (key.equals(COMBINE_ACCESSRULES)) {
	// Assert.isTrue(value instanceof Boolean);
	// }
	//
	// attribute.setValue(value);
	// return attribute;
	// }
	//
	// public CPListElementAttribute findAttributeElement(String key) {
	// for (int i = 0; i < fChildren.size(); i++) {
	// Object curr = fChildren.get(i);
	// if (curr instanceof CPListElementAttribute) {
	// CPListElementAttribute elem = (CPListElementAttribute) curr;
	// if (key.equals(elem.getKey())) {
	// return elem;
	// }
	// }
	// }
	// return null;
	// }

}
