package nl.knmi.adaguc.services.esgfsearch.search.esgfdata;

import nl.knmi.adaguc.services.esgfsearch.search.cache.ICache;
import nl.knmi.adaguc.services.esgfsearch.search.esgfdata.data.ESGFDataDTO;
import nl.knmi.adaguc.services.esgfsearch.search.esgfdata.data.IESGFDAL;
import nl.knmi.adaguc.services.esgfsearch.search.esgfdata.model.Facet;

import java.util.Collection;

public class ESGFDataProvider {
    private IESGFDAL dataAccessLayer;
    private ICache<String> cache;

    private String facetCacheKey = "facets";
    private String resultsCacheKey = "results";

    public ESGFDataProvider(IESGFDAL dataAccessLayer, ICache<String> cache) {
        this.dataAccessLayer = dataAccessLayer;
        this.cache = cache;
    }

    private void refetch() {
        ESGFDataDTO fetchedData = dataAccessLayer.fetch();

//        cache.set(facetCacheKey, fetchedData.getFacets());
    }

    public Collection<Facet> provideFacets() {
        final String cacheKey = "facets";

        String cachedFacets = cache.get(cacheKey);

        if (cachedFacets == null) {

//            cache.set(cacheKey, fetchedData.getFacets().toString());
        }



        return dataAccessLayer.fetch().getFacets();
    }
}
