package nl.knmi.adaguc.services.esgfsearch.search.esgfdata.data;

import nl.knmi.adaguc.services.esgfsearch.search.esgfdata.model.Facet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.Collection;

public class ESGFDataDTO {
    private Collection<Facet> facets;

    @XmlElements(
            @XmlElement(name = "facet_counts")
    )
    public Collection<Facet> getFacets() {
        return facets;
    }
}
