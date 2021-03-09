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

	private final Map<Widget, ISupplier<String>> wrapped;

	/** @see HashMap#HashMap() */
	public LocalizedWidgets() {
		wrapped = new HashMap<Widget, ISupplier<String>>();
	}

	/** @see HashMap#HashMap(int) */
	public LocalizedWidgets(final int initialCapacity) {
		wrapped = new HashMap<Widget, ISupplier<String>>(initialCapacity);
	}

	public void updateTexts() {
		for (final Entry<Widget, ISupplier<String>> entry : wrapped.entrySet()) {
			resetText(entry.getKey(), entry.getValue());
		}
	}

	/** @see Map#put(Object, Object) */
	@Override
	public ISupplier<String> put(final Widget widget, final ISupplier<String> supplier) {
		final Class<? extends Widget> widgetClass = widget.getClass();
		Method method = setTextMethods.get(widgetClass);
		if (method == null) {
			try {
				method = widgetClass.getMethod("setText", String.class);
				setTextMethods.put(widgetClass, method);
			}
			catch (final NoSuchMethodException e) {
				throw new IllegalArgumentException(String.valueOf(widget), e);
			}
		}
		resetText(widget, supplier);
		return wrapped.put(widget, supplier);
	}

	@Override
	public void putAll(Map<? extends Widget, ? extends ISupplier<String>> map) {
		for (final Entry<? extends Widget, ? extends ISupplier<String>> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public int size() {
		return wrapped.size();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Override
	public boolean containsKey(final Object key) {
		return wrapped.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return wrapped.containsValue(value);
	}

	@Override
	public ISupplier<String> get(final Object key) {
		return wrapped.get(key);
	}

	@Override
	public ISupplier<String> remove(final Object key) {
		return wrapped.remove(key);
	}

	@Override
	public void clear() {
		wrapped.clear();
	}

	@Override
	public Set<Widget> keySet() {
		return wrapped.keySet();
	}

	@Override
	public Collection<ISupplier<String>> values() {
		return wrapped.values();
	}

	@Override
	public Set<Entry<Widget, ISupplier<String>>> entrySet() {
		return wrapped.entrySet();
	}

	public <T extends Widget> Entry<T, ISupplier<String>> putAndReturn(final T widget, final ISupplier<String> supplier) {
		put(widget, supplier);
		return new SimpleEntry<T, ISupplier<String>>(widget, supplier);
	}

	private static void resetText(final Widget widget, final ISupplier<String> supplier) {
		if (widget != null && !widget.isDisposed()) {
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
