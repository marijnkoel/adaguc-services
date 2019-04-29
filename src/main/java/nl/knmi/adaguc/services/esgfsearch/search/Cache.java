package nl.knmi.adaguc.services.esgfsearch.search;

import nl.knmi.adaguc.tools.Tuple;

import java.util.Date;
import java.util.HashMap;

public class Cache<String, TItem> {

    private final HashMap<String, CacheItem> cache = new HashMap<>();

    private final int TimeoutTimeSeconds;

    /**
     * @param timeoutTimeSeconds the time to timeout. Zero to immediately time-out
     */
    public Cache(int timeoutTimeSeconds) {
        if (timeoutTimeSeconds < 0) throw new IllegalArgumentException("Timeout-time cannot be lower than zero");

        TimeoutTimeSeconds = timeoutTimeSeconds;
    }

    /**
     * @param item Item to be saved
     *
     * @return Cache-item bound to current time
     */
    private CacheItem createItem(TItem item) {
        return new CacheItem((int) new Date().getTime(), item);
    }

    /**
     * @param key  Key by which to identify Item
     * @param item Item itself
     */
    private void set(String key, TItem item) {
        CacheItem tuple = createItem(item);

        this.cache.put(key, tuple);
    }

    /**
     * @param key Key by which to identify Item
     *
     * @return Tuple: Boolean if item is timed out; TItem the item itself
     */
    private CacheItem get(String key) {
        return cache.get(key);
    }

    public class CacheItem {

        private final Tuple<Integer, TItem> store;

        /**
         * @param timestamp Timestamp in seconds
         * @param item      Item to be cached
         */
        private CacheItem(int timestamp, TItem item) {

            if (timestamp < 0) throw new IllegalArgumentException("Timestamp cannot be less than zero");

            this.store = new Tuple<>(timestamp, item);
        }

        /**
         * @return Whether item is timed out or not
         */
        public boolean isTimedOut() {
            // Time of registry plus timeout-time is less than current time
            return store.getFirst() + TimeoutTimeSeconds > new Date().getTime();
        }

        /**
         * @return Timestamp in seconds
         */
        public int getTime() {
            return store.getFirst();
        }

        /**
         * @return Item saved in cache-item
         */
        public TItem getItem() {
            return store.getSecond();
        }
    }
}
