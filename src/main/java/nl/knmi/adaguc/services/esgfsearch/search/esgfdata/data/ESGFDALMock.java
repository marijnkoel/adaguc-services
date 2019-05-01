package nl.knmi.adaguc.services.esgfsearch.search.esgfdata.data;

import java.util.ArrayList;

public class ESGFDALMock implements IESGFDAL {

    public ESGFDataDAO fetch() {
        return new ESGFDataDAO();
    }
}
