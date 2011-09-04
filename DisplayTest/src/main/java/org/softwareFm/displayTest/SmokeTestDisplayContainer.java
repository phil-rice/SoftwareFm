package org.softwareFm.displayTest;


public class SmokeTestDisplayContainer {
	public static void main(String[] args) {
		// final IDisplayContainerFactoryBuilder managerFactory = IDisplayContainerFactoryBuilder.Utils.factoryBuilder();
		// managerFactory.registerDisplayer(new TextDisplayer());
		// managerFactory.registerForEntity("entity", "text_one", "One", "Help1");
		// managerFactory.registerForEntity("entity", "text_two", "Two", "Help2");
		// managerFactory.registerForEntity("entity", "text_three", "Three", "Help3");
		// final IDisplayContainerFactory factory = managerFactory.build();
		// final ISoftwareFmRepository repository = ISoftwareFmRepository.Utils.repository();
		//
		// Swts.display("DisplayContainer1", new IFunction1<Composite, Composite>() {
		// @Override
		// public Composite apply(final Composite from) throws Exception {
		// DisplayerContext displayerContext = new DisplayerContext(IImageFactory.Utils.imageFactory(), ISelectedBindingManager.Utils.noSelectedBindingManager(), repository);
		// final IDisplayContainer displayContainer = factory.create(displayerContext, from, "entity");
		//
		// final BindingContext bindingContext1 = makeBindingContext(from, "entity", 1, "text_one", "value1");
		// displayContainer.setValues(bindingContext1);
		// System.out.println(Swts.layoutAsString(from));
		// System.out.println();
		// from.getDisplay().asyncExec(new Runnable() {
		// @Override
		// public void run() {
		// BindingContext bindingContext2 = makeBindingContext(from, "entity", 2, "text_two", "value2");
		// displayContainer.setValues(bindingContext2);
		// System.out.println(Swts.layoutAsString(from));
		// }
		// });
		// return displayContainer.getComposite();
		// }
		//
		// private BindingContext makeBindingContext(Composite parent, String entity, int count, Object... dataAsObject) {
		// return new BindingContext(repository, new Images(parent.getDisplay()), "url", //
		// Maps.<String, Object> makeMap(dataAsObject), //
		// Maps.<String, Object> makeMap(RepositoryConstants.entity, entity, "context", "c" + count));
		// }
		// });
		// repository.shutdown();
	}
}
