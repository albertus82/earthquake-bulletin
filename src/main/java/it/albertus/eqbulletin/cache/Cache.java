package it.albertus.eqbulletin.cache;

public interface Cache<K, V> {

	void put(K key, V value);

	V get(K key);

	boolean contains(K key);

	int getSize();

}
