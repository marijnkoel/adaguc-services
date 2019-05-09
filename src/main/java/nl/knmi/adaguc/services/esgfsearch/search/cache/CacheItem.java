package nl.knmi.adaguc.services.esgfsearch.search.cache;

import nl.knmi.adaguc.tools.Tuple;

import java.util.Date;

public class CacheItem<TItem> {

    private final Tuple<Long, TItem> store;
    private final int timeoutTimeSeconds;

    /**
     * @param timestamp Timestamp in seconds
     * @param item      Item to be cached
     */
    private CacheItem(long timestamp, int timeoutTimeSeconds, TItem item) {
        this.timeoutTimeSeconds = timeoutTimeSeconds;

        if (timestamp < 0) throw new IllegalArgumentException("Timestamp cannot be less than zero");

        this.store = new Tuple<>(timestamp, item);
    }

    /**
     * @param item Item to be saved
     *
     * @return MemoryCache-item bound to current time
     */
    static <TItem> CacheItem<TItem> createItem(int timeoutTimeSeconds, TItem item) {
        return new CacheItem<>((new Date()).getTime(), timeoutTimeSeconds, item);
    }

    /**
     * @return Whether item is timed out or not
     */
    public boolean isTimedOut() {
        // Time of registry plus timeout-time is less than current time
        return store.getFirst() + timeoutTimeSeconds > new Date().getTime();
    }

    /**
     * @return Timestamp in seconds
     */
    public long getTime() {
        return store.getFirst();
    }

    /**
     * @return Item saved in cache-item
     */
    public TItem getItem() {
        return store.getSecond();
    }
}
