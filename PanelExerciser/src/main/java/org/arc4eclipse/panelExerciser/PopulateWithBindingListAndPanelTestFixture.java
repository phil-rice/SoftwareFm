package org.arc4eclipse.panelExerciser;

import java.util.Map;

import org.arc4eclipse.binding.path.BindingPathCalculator;
import org.arc4eclipse.binding.path.JavaElementRipper;
import org.arc4eclipse.binding.path.JavaElementRipperResult;
import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryClient.paths.impl.PathCalculatorThin;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.IBinding;

public class PopulateWithBindingListAndPanelTestFixture {
	public static void main(String[] args) throws Exception {
		BindingPathCalculator pathCalculator = new BindingPathCalculator(new PathCalculatorThin());
		IHttpClient client = IHttpClient.Utils.builder().withCredentials("admin", "admin");
		IRepositoryClient<IBinding, IEntityType> repositoryClient = IRepositoryClient.Utils.repositoryClient(pathCalculator, client);
		IRepositoryFacard<IPath, IBinding, IEntityType, Map<Object, Object>> facard = IRepositoryFacard.Utils.facard(repositoryClient);
		IRepositoryFacardCallback<IPath, IBinding, IEntityType, Map<Object, Object>> callback = new IRepositoryFacardCallback<IPath, IBinding, IEntityType, Map<Object, Object>>() {
			
			public void process(IPath key, IBinding binding, IEntityType entityType, Map<Object, Object> data) {
				System.out.println(binding + " " + entityType + " " + data);
			}
		};

		for (IBinding binding : PanelExerciserTestFixture.bindings) {
			JavaElementRipperResult result = JavaElementRipper.rip(binding);
			JavaElementRipperResult ripperResult = JavaElementRipper.rip(binding);
			IPath path = ripperResult.path;
			facard.setDetails(path, binding, IEntityType.PROJECT, Maps.<Object, Object> makeMap(//
					"comment", "Project for " + result.path.toOSString() + " not known yet"), callback);
			facard.setDetails(path, binding, IEntityType.RELEASE, Maps.<Object, Object> makeMap(//
					"comment", "The release for " + result.path.toOSString()), callback);
			facard.setDetails(path, binding, IEntityType.PACKAGE, Maps.<Object, Object> makeMap(//
					"package", result.packageName,//
					"comment", "Some comment about " + result.packageName), callback);
			facard.setDetails(path, binding, IEntityType.CLASS, Maps.<Object, Object> makeMap(//
					"package", result.packageName,//
					"class", result.className,//
					"comment", "Comment for " + result.className), callback);
			if (ripperResult.methodName != null)
				facard.setDetails(path, binding, IEntityType.METHOD, Maps.<Object, Object> makeMap(//
						"class", ripperResult.className,//
						"method", ripperResult.methodName,//
						"comment", "The " + result.methodName + " method"), callback);
		}
	}
}
