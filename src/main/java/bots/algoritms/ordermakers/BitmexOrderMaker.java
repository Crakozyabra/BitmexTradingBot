package bots.algoritms.ordermakers;

import bots.algoritms.ordermakers.URL.URL;
import bots.algoritms.ordermakers.URL.URLBuilder;
import bots.algoritms.ordermakers.URL.urlconstants.ResourceURL;
import bots.algoritms.ordermakers.URL.urlconstants.UtilURL;
import bots.algoritms.ordermakers.URL.urlconstants.Verb;
import bots.algoritms.ordermakers.jsondataparsers.BitmexJsonDataParser;
import bots.algoritms.ordermakers.jsondataparsers.JsonDataParser;
import bots.algoritms.ordermakers.orders.LimitOrder;
import bots.algoritms.ordermakers.orders.orderconstants.OrderSide;
import bots.algoritms.ordermakers.orders.orderconstants.Symbol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

public class BitmexOrderMaker implements OrderMaker {
    private final String apiKey;
    private final String apiSecret;
    private HttpClient client = HttpClient.newHttpClient();
    private HttpRequest request;
    private HttpResponse<String> response;
    private URL url;
    private String signature;
    private String getQueryURLparams = "?symbol=XBTUSD&count=100&reverse=false&endTime=";
    private JsonDataParser jsonDataParser = new BitmexJsonDataParser();
    private final static Logger logger = LogManager.getLogger();

    public BitmexOrderMaker(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }
    @Override
    public LimitOrder makeOrder(LimitOrder order) {
        url = new URLBuilder() // tested
                .protocol(UtilURL.PROTOCOL_HTTP)
                .net(UtilURL.TESTNET)
                .baseUrl(UtilURL.BASE_URL)
                .apiPath(UtilURL.API_PATH_HTTP)
                .recoursePath(ResourceURL.ORDER)
                .build();
        String orderJsonStr = new JSONObject(order).toString();
        String expires = createExpires();
        signature = createSignature(url, Verb.POST, orderJsonStr, expires);

        while(signature.length() != 64) { // tested
            expires = createExpires();
            signature = createSignature(url, Verb.POST, orderJsonStr, expires);
        }

        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(orderJsonStr))
                .header("api-signature", signature)
                .header("api-expires", expires)
                .header("api-key", apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .uri(URI.create(url.toString()))
                .build();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);  // tested
        }
        logger.debug("response:" + response.body());
        return jsonDataParser.parseLimitOrder(response.body()); // tested
    }

    @Override
    public Set<LimitOrder> getAllOrders() {
        url = new URLBuilder() // tested
                .protocol(UtilURL.PROTOCOL_HTTP)
                .net(UtilURL.TESTNET)
                .baseUrl(UtilURL.BASE_URL)
                .apiPath(UtilURL.API_PATH_HTTP)
                .recoursePath(ResourceURL.ORDER)
                .queryData(getQueryURLparams + LocalDateTime.now())
                .build();
        String expires = createExpires();
        signature = createSignature(url, Verb.GET, getQueryURLparams + LocalDateTime.now(), expires);

        while(signature.length() != 64) {
            expires = createExpires();
            signature = createSignature(url, Verb.GET, getQueryURLparams + LocalDateTime.now(), expires);
        }

        request = makeGetRequest(signature, apiKey, expires, url);

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.debug("response: " + response.body());
        return jsonDataParser.parseAllLimitOrders(response.body());
    }



    @Override
    public double getCurrentBitcoinPrice(){
        url = new URLBuilder() // tested
                .protocol(UtilURL.PROTOCOL_HTTP)
                .net(UtilURL.TESTNET)
                .baseUrl(UtilURL.BASE_URL)
                .apiPath(UtilURL.API_PATH_HTTP)
                .recoursePath(ResourceURL.QUOTE)
                .queryData(getQueryURLparams + LocalDateTime.now())
                .build();

        String expires = createExpires();
        signature = createSignature(url, Verb.GET, getQueryURLparams + LocalDateTime.now(), expires);

        while(signature.length() != 64) {
            expires = createExpires();
            signature = createSignature(url, Verb.GET, getQueryURLparams + LocalDateTime.now(), expires);
        }

        request = makeGetRequest(signature, apiKey, expires, url);

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.debug("response: " + response.body());
        return jsonDataParser.parseBitcoinPrice(response.body());
    }

    private String createSignature(URL url, Verb verb, String data, String expires) {
        Signature signature = new Signature();
        switch (verb) {
            case POST -> {
                String path = url.getApiPath()+url.getResourcePath();
                return signature.signatureToString(signature.createSignature("POST", path, data, expires, apiSecret ));
            }
            case GET -> {
                String path = url.getApiPath() + url.getResourcePath() + url.getQueryData();
                return signature.signatureToString(signature.createSignature("GET", path, "", expires, apiSecret ));
            }
            default -> {
                return null;
            }
        }
    }

    private String createExpires() {
        return (Instant.now().getEpochSecond() + 100) + "";
    }

    public boolean accountKeysIsValid() {
        url = new URLBuilder()
                .protocol(UtilURL.PROTOCOL_HTTP)
                .net(UtilURL.TESTNET)
                .baseUrl(UtilURL.BASE_URL)
                .apiPath(UtilURL.API_PATH_HTTP)
                .recoursePath(ResourceURL.ORDER)
                .queryData(getQueryURLparams + LocalDateTime.now())
                .build();
        String expires = createExpires();
        signature = createSignature(url, Verb.GET, getQueryURLparams + LocalDateTime.now(), expires);

        while(signature.length() != 64) {
            expires = createExpires();
            signature = createSignature(url, Verb.GET, getQueryURLparams + LocalDateTime.now(), expires);
        }

        request = makeGetRequest(signature, apiKey, expires, url);

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.debug("response: " + response.body());
        try {
            JSONObject jsonObject = new JSONObject(response.body());
            String errorMessage = jsonObject.getJSONObject("error").getString("message");

            if (errorMessage.equals("Invalid API Key.") || errorMessage.equals("Signature not valid.")) {
                return false;
            }
        } catch (JSONException e) {}
        return true;
    }

    private HttpRequest makeGetRequest(String signature, String apiKey, String expires, URL url) {
        return HttpRequest.newBuilder()
                .GET()
                .header("api-signature", signature)
                .header("api-expires", expires)
                .header("api-key", apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .uri(URI.create(url.toString()))
                .build();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public HttpClient getClient() {
        return client;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse<String> getResponse() {
        return response;
    }

    public void setResponse(HttpResponse<String> response) {
        this.response = response;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getGetQueryURLparams() {
        return getQueryURLparams;
    }

    public void setGetQueryURLparams(String getQueryURLparams) {
        this.getQueryURLparams = getQueryURLparams;
    }

    public JsonDataParser getJsonDataParser() {
        return jsonDataParser;
    }

    public void setJsonDataParser(JsonDataParser jsonDataParser) {
        this.jsonDataParser = jsonDataParser;
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }
}
