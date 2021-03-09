package it.albertus.eqbulletin.gui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Widget;

import it.albertus.util.ISupplier;

public class LocalizedWidgets implements Map<Widget, ISupplier<String>> {

	private static final Map<Class<? extends Widget>, Method> setTextMethods = new HashMap<Class<? extends Widget>, Method>();

	private final Map<Widget, ISupplier<String>> wrappedMap;

	/** @see HashMap#HashMap() */
	public LocalizedWidgets() {
		wrappedMap = new HashMap<Widget, ISupplier<String>>();
	}

	/** @see HashMap#HashMap(int) */
	public LocalizedWidgets(final int initialCapacity) {
		wrappedMap = new HashMap<Widget, ISupplier<String>>(initialCapacity);
	}

	public void updateTexts() {
		for (final Entry<Widget, ISupplier<String>> entry : wrappedMap.entrySet()) {
			resetText(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public ISupplier<String> put(final Widget widget, final ISupplier<String> supplier) {
		final Class<? extends Widget> widgetClass = widget.getClass();
		if (!setTextMethods.containsKey(widgetClass)) {
			try {
				setTextMethods.put(widgetClass, widgetClass.getMethod("setText", String.class));
			}
			catch (final NoSuchMethodException e) {
				throw new IllegalArgumentException(String.valueOf(widget), e);
			}
		}
		resetText(widget, supplier);
		return wrappedMap.put(widget, supplier);
	}

	@Override
	public void putAll(Map<? extends Widget, ? extends ISupplier<String>> map) {
		for (final Entry<? extends Widget, ? extends ISupplier<String>> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public int size() {
		return wrappedMap.size();
	}

	@Override
	public boolean isEmpty() {
		return wrappedMap.isEmpty();
	}

	@Override
	public boolean containsKey(final Object key) {
		return wrappedMap.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return wrappedMap.containsValue(value);
	}

	@Override
	public ISupplier<String> get(final Object key) {
		return wrappedMap.get(key);
	}

	@Override
	public ISupplier<String> remove(final Object key) {
		return wrappedMap.remove(key);
	}

	@Override
	public void clear() {
		wrappedMap.clear();
	}

	@Override
	public Set<Widget> keySet() {
		return wrappedMap.keySet();
	}

	@Override
	public Collection<ISupplier<String>> values() {
		return wrappedMap.values();
	}

	@Override
	public Set<Entry<Widget, ISupplier<String>>> entrySet() {
		return wrappedMap.entrySet();
	}

	public <T extends Widget> Entry<T, ISupplier<String>> putAndReturn(final T widget, final ISupplier<String> supplier) {
		put(widget, supplier);
		return new SimpleEntry<T, ISupplier<String>>(widget, supplier);
	}

	private static void resetText(final Widget widget, final ISupplier<String> supplier) {
		if (supplier != null && widget != null && !widget.isDisposed()) {
			try {
				setTextMethods.get(widget.getClass()).invoke(widget, String.valueOf(supplier.get()));
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
