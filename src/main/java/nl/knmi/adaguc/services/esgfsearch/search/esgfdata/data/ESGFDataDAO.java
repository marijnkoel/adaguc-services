package nl.knmi.adaguc.services.esgfsearch.search.esgfdata.data;

import nl.knmi.adaguc.services.esgfsearch.search.esgfdata.model.Facet;

import java.util.Collection;

public class ESGFDataDAO {
    private Collection<Facet> facets;

    public Collection<Facet> getFacets() {
        return facets;
    }
}
