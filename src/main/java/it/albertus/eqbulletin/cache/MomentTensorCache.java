package it.albertus.eqbulletin.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import it.albertus.eqbulletin.model.MomentTensor;

public class MomentTensorCache {

	private static final byte CACHE_SIZE = Byte.MAX_VALUE;

	private static MomentTensorCache instance;

	public static synchronized MomentTensorCache getInstance() {
		if (instance == null) {
			instance = new MomentTensorCache();
		}
		return instance;
	}

	private MomentTensorCache() {}

	private final Map<String, MomentTensor> cache = new LinkedHashMap<>(); // TODO compress

	public void put(final String guid, final MomentTensor mt) {
		cache.put(guid, mt);
		while (cache.size() > 0 && cache.size() > CACHE_SIZE) {
			final String eldestGuid = cache.keySet().iterator().next();
			cache.remove(eldestGuid);
		}
	}

	public MomentTensor get(final String guid) {
		return cache.get(guid);
	}

	public boolean contains(final String guid) {
		return cache.containsKey(guid);
	}

	public int size() {
		return cache.size();
	}

	@Override
	public String toString() {
		return "MomentTensorCache [size=" + size() + "]";
	}

}
