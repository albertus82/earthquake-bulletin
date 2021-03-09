package it.albertus.eqbulletin.gui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Widget;

import it.albertus.util.ISupplier;
import it.albertus.util.logging.LoggerFactory;

public class LocalizedWidgets implements Iterable<Widget> {

	private static final String METHOD_KEY = LocalizedWidgets.class.getName() + ".method";
	private static final String SUPPLIER_KEY = LocalizedWidgets.class.getName() + ".supplier";

	private static final Logger log = LoggerFactory.getLogger(LocalizedWidgets.class);

	private static final Map<Class<? extends Widget>, Method> setTextMethods = new HashMap<Class<? extends Widget>, Method>();

	private final Collection<Widget> widgets;

	/** @see ArrayList#ArrayList() */
	public LocalizedWidgets() {
		widgets = new ArrayList<Widget>();
	}

	/** @see ArrayList#ArrayList(int) */
	public LocalizedWidgets(final int initialCapacity) {
		widgets = new ArrayList<Widget>(initialCapacity);
	}

	public void updateTexts() {
		for (final Widget widget : widgets) {
			resetText(widget);
		}
	}

	/** @see Collection#add(Object) */
	public boolean add(final Widget widget, final ISupplier<String> supplier) {
		final Class<? extends Widget> widgetClass = widget.getClass();
		Method method = setTextMethods.get(widgetClass);
		if (method == null) {
			try {
				method = widgetClass.getMethod("setText", String.class);
				setTextMethods.put(widgetClass, method);
				log.log(Level.FINE, "{0}", method);
			}
			catch (final NoSuchMethodException e) {
				throw new IllegalArgumentException(String.valueOf(widget), e);
			}
		}
		widget.setData(METHOD_KEY, method);
		widget.setData(SUPPLIER_KEY, supplier);
		resetText(widget);
		return widgets.add(widget);
	}

	/** @see Collection#remove(Object) */
	public boolean remove(final Widget widget) {
		return widgets.remove(widget);
	}

	/** @see Collection#isEmpty() */
	public boolean isEmpty() {
		return widgets.isEmpty();
	}

	/** @see Collection#size() */
	public int size() {
		return widgets.size();
	}

	/** @see Collection#contains(Object) */
	public boolean contains(final Widget widget) {
		return widgets.contains(widget);
	}

	/** @see Collection#containsAll(Collection) */
	public boolean containsAll(final Collection<? extends Widget> widgets) {
		return this.widgets.containsAll(widgets);
	}

	/** @see Collection#removeAll(Collection) */
	public boolean removeAll(final Collection<? extends Widget> widgets) {
		return this.widgets.removeAll(widgets);
	}

	/** @see Collection#retainAll(Collection) */
	public boolean retainAll(final Collection<? extends Widget> widgets) {
		return this.widgets.retainAll(widgets);
	}

	/** @see Collection#clear() */
	public void clear() {
		widgets.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return widgets.iterator();
	}

	private static void resetText(final Widget widget) {
		if (widget != null && !widget.isDisposed()) {
			final Object method = widget.getData(METHOD_KEY);
			final Object supplier = widget.getData(SUPPLIER_KEY);
			if (supplier instanceof ISupplier && method instanceof Method) {
				try {
					((Method) method).invoke(widget, String.valueOf(((ISupplier<?>) supplier).get()));
				}
				catch (final IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
				catch (final InvocationTargetException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

}
