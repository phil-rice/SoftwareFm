package org.softwareFm.card.configuration;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.configuration.internal.BasicCardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.softwareFm.internal.SoftwareFmCardConfigurator;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.softwareFmImages.IImageRegisterConfigurator;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.resources.NestedResourceGetterFn;
public interface ICardConfigurator {

	public CardConfig configure(Display device, CardConfig config);
	
	public static class Utils{
		
		public static ICardConfigurator basicConfigurator(){
			return new BasicCardConfigurator();
		}
		
		
		public static ICardConfigurator softwareFmConfigurator(){
			return new SoftwareFmCardConfigurator();
		}
		
		public static IFunction1<String, IResourceGetter> resourceGetterFn(Class<?> anchorClass, String rootName){
			final IResourceGetter baseResourceGetter = IResourceGetter.Utils.noResources().with(anchorClass, rootName);
			final IFunction1<String, IResourceGetter> resourceGetterFn = new NestedResourceGetterFn(baseResourceGetter, CardConfig.class);
			return resourceGetterFn;
		}

		public static IUrlGeneratorMap makeSoftwareFmUrlGeneratorMap(String prefix) {
			final IUrlGeneratorMap urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap(//
					CardConstants.urlGroupKey, new UrlGenerator(prefix + "{3}/{2}", "groupId"),// hash, hash, groupId, groundIdWithSlash
					CardConstants.artifactUrlKey, new UrlGenerator(prefix + "{3}/{2}/artifact/{4}", "groupId", "artifactId"),// 0,1: hash, 2,3: groupId, 4,5: artifactId
					CardConstants.versionUrlKey, new UrlGenerator(prefix + "{3}/{2}/artifact/{4}/version/{6}", "groupId", "artifactId", "version"),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
					CardConstants.digestUrlKey, new UrlGenerator(prefix + "{3}/{2}/artifact/{4}/version/{6}/digest/{8}", "groupId", "artifactId", "version", CardConstants.digest),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version, 8,9: digest
					CardConstants.jarUrlKey, new UrlGenerator("/softwareFm/jars/{0}/{1}/{2}", CardConstants.digest),// 0,1: hash, 2,3: digest
					CardConstants.userUrlKey, new UrlGenerator("/softwareFm/users/guid/{0}/{1}/{2}", "notSure"));// hash and guid
			return urlGeneratorMap;
		}
		public static IFunction1<String,Image> imageFn(Display display, IImageRegisterConfigurator configurator){
			final ImageRegistry imageRegistry = new ImageRegistry(display);
			configurator.registerWith(display, imageRegistry);
			IFunction1<String, Image> imageFn = new IFunction1<String, Image>() {
				@Override
				public Image apply(String from) throws Exception {
					return imageRegistry.get(from);
				}
			};
			return imageFn;
		}
	}
	
	
}
