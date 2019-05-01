package nl.knmi.adaguc.services.esgfsearch.search.cache;

/**
 * @param <TItem> Type of the cached items
 */
public interface ICache<TItem> {
    /**
     * @param key Identifier for the item
     *
     * @return item if present, null if not
     */
    TItem get(String key);

    /**
     * @param key  Identifier for the item
     * @param item Item to cache
     */
    void set(String key, TItem item);
}
