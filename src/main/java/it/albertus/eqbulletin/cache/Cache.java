package it.albertus.eqbulletin.cache;

import java.io.Serializable;

public interface Cache<K, V> extends Serializable {

	void put(K key, V value);

	V get(K key);

	boolean contains(K key);

	int getSize();

}
