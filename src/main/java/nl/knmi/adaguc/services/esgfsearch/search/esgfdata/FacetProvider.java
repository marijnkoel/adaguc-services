package nl.knmi.adaguc.services.esgfsearch.search.esgfdata;

import nl.knmi.adaguc.services.esgfsearch.search.cache.CacheItem;
import nl.knmi.adaguc.services.esgfsearch.search.cache.ICache;
import nl.knmi.adaguc.services.esgfsearch.search.esgfdata.data.IESGFDAL;
import nl.knmi.adaguc.services.esgfsearch.search.esgfdata.model.Facet;
import org.json.JSONObject;

import java.util.Collection;

public class FacetProvider {
    private IESGFDAL dataAccessLayer;
    private ICache<String> cache;

    public FacetProvider(IESGFDAL dataAccessLayer, ICache<String> cache) {
        this.dataAccessLayer = dataAccessLayer;
        this.cache = cache;
    }

    public Collection<Facet> provide() {

        String cachedFacets = cache.get("facets");

//        if (cachedFacets.isTimedOut())

        return dataAccessLayer.fetch().getFacets();
    }
}
