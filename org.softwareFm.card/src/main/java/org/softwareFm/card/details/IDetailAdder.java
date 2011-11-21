package org.softwareFm.card.details;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.details.internal.CollectionItemDetailAdder;
import org.softwareFm.card.details.internal.CollectionsDetailAdder;
import org.softwareFm.card.details.internal.EditorDetailAdder;
import org.softwareFm.card.details.internal.ListDetailAdder;
import org.softwareFm.display.composites.IHasControl;

public interface IDetailAdder {
	
	public static boolean useListOfCards = false;

	IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback);
	
	public static class Utils{
		public static IDetailAdder collectionItem(){
			return new CollectionItemDetailAdder();
		}
		public static IDetailAdder collections(){
			return new CollectionsDetailAdder();
		}
		public static IDetailAdder editorDetail(){
			return new EditorDetailAdder();
		}
		public static IDetailAdder listDetail(){
			return new ListDetailAdder();
		}
	}

}
