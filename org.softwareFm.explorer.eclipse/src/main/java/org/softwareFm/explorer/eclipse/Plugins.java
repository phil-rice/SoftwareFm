package org.softwareFm.explorer.eclipse;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;

public class Plugins {
	
	public static void walkSelectionServices(ICallback<ISelectionService> callback) {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
			for (int i = 0; i < workbenchWindows.length; i++) {
				IWorkbenchWindow workbenchWindow = workbench.getWorkbenchWindows()[i];
				ISelectionService selectionService = workbenchWindow.getSelectionService();
				callback.process(selectionService);
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <T> List<T> makeListFrom(String id, final ICallback<Throwable> exceptions){
		final List<T> result = Lists.newList();
		useClasses(id, new IPlugInCreationCallback<T>() {

			@Override
			public void process(T t, IConfigurationElement element) throws Exception {
				result.add(t);
				
			}

			@Override
			public void onException(Throwable throwable, IConfigurationElement element) {
				try {
					exceptions.process(throwable);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> classFrom(IConfigurationElement element) {
		try {
			return (Class<T>) element.createExecutableExtension("class").getClass();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <T> void useConfigElements(String id, ICallback<IConfigurationElement> callback, ICallback<Throwable> exceptions) {
		ICallback<IConfigurationElement> safeCallback = ICallback.Utils.safeCallback(exceptions, callback);
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(id);
		for (IConfigurationElement e : config)
			try {
				safeCallback.process(e);
			} catch (Exception e1) {
				try {
					exceptions.process(e1);
				} catch (Exception e2) {
					throw WrappedException.wrap(e2);
				}
			}
	}

	public static <T> T configureMainWithCallbacks(final T main, String id, final String propertyName, ICallback<Throwable> exceptions) {
		useConfigElements(id, new ICallback<IConfigurationElement>() {
			@Override
			public void process(IConfigurationElement t) throws Exception {
				@SuppressWarnings("unchecked")
				ICallback<T> callback = (ICallback<T>) t.createExecutableExtension(propertyName);
				callback.process(main);

			}
		}, exceptions);
		return main;
	}

	@SuppressWarnings("unchecked")
	public static <T> void useClasses(String id, IPlugInCreationCallback<T> callback) {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(id);
		for (IConfigurationElement element : config)
			try {
				T t = (T) element.createExecutableExtension("class");
				callback.process(t, element);
			} catch (Exception e1) {
				try {
					callback.onException(e1, element);
				} catch (Exception e2) {
					throw WrappedException.wrap(e2);
				}
			}
	}
}