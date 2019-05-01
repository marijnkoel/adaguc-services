package nl.knmi.adaguc.services.esgfsearch.search.cache;

import java.util.HashMap;

public class MemoryCache<TItem> implements ICache<TItem> {

    private final HashMap<String, CacheItem<TItem>> cache = new HashMap<>();

    private final int timeoutTimeSeconds;

    /**
     * @param timeoutTimeSeconds the time to timeout. Zero to immediately time-out
     */
    public MemoryCache(int timeoutTimeSeconds) {
        if (timeoutTimeSeconds < 0) throw new IllegalArgumentException("Timeout-time cannot be lower than zero");

        this.timeoutTimeSeconds = timeoutTimeSeconds;
    }

    /**
     * @param key  Key by which to identify Item
     * @param item Item itself
     */
    public void set(String key, TItem item) {
        CacheItem<TItem> tuple = CacheItem.createItem(timeoutTimeSeconds, item);

        this.cache.put(key, tuple);
    }

    /**
     * @param key Key by which to identify Item
     *
     * @return Tuple: Boolean if item is timed out; TItem the item itself
     */
    public TItem get(String key) {
        return cache.get(key).getItem();
    }
}
