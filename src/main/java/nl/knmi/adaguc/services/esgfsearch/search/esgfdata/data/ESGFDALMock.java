package nl.knmi.adaguc.services.esgfsearch.search.esgfdata.data;

public class ESGFDALMock implements IESGFDAL {

    public ESGFDataDTO fetch() {
        return new ESGFDataDTO();
    }
}
