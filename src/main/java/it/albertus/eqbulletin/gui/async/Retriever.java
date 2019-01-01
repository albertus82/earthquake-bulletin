package it.albertus.eqbulletin.gui.async;

import org.eclipse.swt.widgets.Shell;

@FunctionalInterface
public interface Retriever<A, O> {

	O retrieve(A arg, Shell shell);

}
