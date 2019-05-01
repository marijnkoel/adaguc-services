package nl.knmi.adaguc.services.esgfsearch.search.catalog;

import nl.knmi.adaguc.services.esgfsearch.search.cache.DiskCache;
import nl.knmi.adaguc.tools.*;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class CatalogChecker {

    private final int catalogCheckerTimeOutMS = 2000;
    private final int catalogCheckerTimeValiditySec = 60 * 5;
    private final int catalogContentsTimeValiditySec = 10 * 60;

    private final Map<String, URLBeingChecked> urlsBeingChecked = new ConcurrentHashMap<>();
    private final ExecutorService CatalogGETExecutor;

    private final DiskCache catalogCache;

    public CatalogChecker(ExecutorService catalogGETExecutor, DiskCache catalogCache) {
        CatalogGETExecutor = catalogGETExecutor;
        this.catalogCache = catalogCache;
    }

    private void registerNewUrl(String query, HttpServletRequest request) {
        URLBeingChecked urlBeingChecked = new URLBeingChecked(query, request);
        urlsBeingChecked.put(query, urlBeingChecked);
    }

    private void cancelUrlQuery(String query) {
        urlsBeingChecked.get(query).response.cancel(true);
        urlsBeingChecked.remove(query);
    }

    /**
     * Checks URL TODO better comment
     *
     * @param query   Query to check
     * @param request Request to check with
     *
     * @return JSONResponse with 'message' and 'ok' attributes
     */
    public JSONResponse checkURL(String query, HttpServletRequest request) {
        Predicate<Long> isTimedOut = date -> date + (catalogCheckerTimeValiditySec * 1000) < DateFunctions.getCurrentDateInMillis();
        Predicate<URLBeingChecked> isUrlTimedOut = urlBeingChecked -> isTimedOut.test(urlBeingChecked.getCreationDate());

        Function<Exception, String> getErrorResponseMessage = exception -> {
            if (exception instanceof WebRequestBadStatusException) {
                return "Error, code: " + ((WebRequestBadStatusException) exception).getStatusCode();
            }
            Debug.println("Exception: " + exception.getMessage());

            return exception.getClass().getSimpleName();
        };
        Function<String, JSONResponse> refireQuery = queryToRefire -> {
            Debug.println("Refiring " + queryToRefire);

            cancelUrlQuery(queryToRefire);
            return checkURL(queryToRefire, request);
        };
        BiFunction<String, String, JSONResponse> createResponse = (message, ok) -> {
            JSONObject responseMessage = new JSONObject();
            JSONResponse r = new JSONResponse(request);

            responseMessage.put("message", message);
            responseMessage.put("ok", ok);

            r.setMessage(responseMessage);
            return r;
        };

        synchronized (urlsBeingChecked) {
            if (!urlsBeingChecked.containsKey(query)) {
                registerNewUrl(query, request);

                return createResponse.apply("start checking", "busy");
            }

            URLBeingChecked urlBeingChecked = urlsBeingChecked.get(query);

            if (isUrlTimedOut.test(urlBeingChecked)) {
                return refireQuery.apply(query);
            }

            if (!urlBeingChecked.response.isDone()) {
                return createResponse.apply("still checking", "busy");
            }
            String message;
            try {
                AsyncGetCatalogResponse response = urlBeingChecked.response.get();
                Exception e = response.getException();
                if (e == null) {
                    return createResponse.apply("status ok", "ok");
                }

                message = getErrorResponseMessage.apply(e);
            } catch (Exception e) {
                Debug.printStackTrace(e);

                message = e.getCause().getMessage();
            }

            urlsBeingChecked.remove(query);

            return createResponse.apply(message, "false");
        }
    }

    /**
     * @param catalogURL Url of the catalog-to-get
     *
     * @return Response-message
     *
     * @throws Exception
     */
    public String getCatalog(String catalogURL) throws Exception {
        String identifier = "dataset_" + catalogURL;
        String response = catalogCache.get(identifier);

        if (response != null) return response;

        try {
            catalogURL = catalogURL.contains("#") ? catalogURL.split("#")[0] : catalogURL;

            response = HTTPTools.makeHTTPGetRequestWithTimeOut(catalogURL, catalogCheckerTimeOutMS);
        } catch (IOException e) {
            Debug.println("CATALOG GET IOException");

            throw new Exception("Unable to GET catalog " + catalogURL + " : " + e.getMessage());
        }

        catalogCache.set(identifier, response);

        return response;
    }

    public class URLBeingChecked {
        public Future<AsyncGetCatalogResponse> response;

        private long creationDate;

        private long getCreationDate() {
            return creationDate;
        }

        private URLBeingChecked(String query, HttpServletRequest request) {
            creationDate = DateFunctions.getCurrentDateInMillis();
            try {
                response = CatalogGETExecutor.submit(new AsyncGetCatalogRequest(query, request));
                creationDate = DateFunctions.getCurrentDateInMillis();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public class AsyncGetCatalogRequest implements Callable<AsyncGetCatalogResponse> {
        private String url;

        private AsyncGetCatalogRequest(String url, HttpServletRequest request) {
            this.url = url;
        }

        public AsyncGetCatalogResponse call() {
            Exception e = null;
            String a = null;

            try {
                a = getCatalog(url);
            } catch (Exception e2) {
                e = e2;
            }
            return new AsyncGetCatalogResponse(a, e);
        }
    }

    public class AsyncGetCatalogResponse {
        private String body;
        private Exception exception;

        private AsyncGetCatalogResponse(String string, Exception exception) {
            this.body = string;
            this.exception = exception;
        }

        public String getBody() {
            return body;
        }

        public Exception getException() {
            return exception;
        }
    }

}
