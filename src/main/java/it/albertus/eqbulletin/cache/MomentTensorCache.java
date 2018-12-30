package it.albertus.eqbulletin.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import it.albertus.eqbulletin.model.MomentTensor;

public class MomentTensorCache implements Cache<String, MomentTensor> {

	private static final byte CACHE_SIZE = Byte.MAX_VALUE;

	private static MomentTensorCache instance;

	public static synchronized MomentTensorCache getInstance() {
		if (instance == null) {
			instance = new MomentTensorCache();
		}
		return instance;
	}

	private MomentTensorCache() {}

	private final Map<String, PackedMomentTensor> cache = new LinkedHashMap<>(16, 0.75f, true);

	@Override
	public void put(final String guid, final MomentTensor mt) {
		cache.put(guid, PackedMomentTensor.pack(mt));
		while (cache.size() > 0 && cache.size() > CACHE_SIZE) {
			final String firstKey = cache.keySet().iterator().next();
			cache.remove(firstKey);
		}
	}

	@Override
	public MomentTensor get(final String guid) {
		return contains(guid) ? cache.get(guid).unpack() : null;
	}

	@Override
	public boolean contains(final String guid) {
		return cache.containsKey(guid);
	}

	@Override
	public int getSize() {
		return cache.size();
	}

	@Override
	public String toString() {
		return "MomentTensorCache [size=" + getSize() + "]";
	}

}
