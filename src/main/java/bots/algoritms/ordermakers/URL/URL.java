package bots.algoritms.ordermakers.URL;

public class URL {
  private String protocol = "";
  private String net = "";
  private String baseUrl = "";
  private String apiPath = "";
  private String queryData = "";
  private String resourcePath = "";
  
  public String getQueryData() {
    return queryData;
  }

  public void setQueryData(String queryData) {
    this.queryData = queryData;
  }
  
  public String getResourcePath() {
    return resourcePath;
  }

  public void setResourcePath(String resourcePath) {
    this.resourcePath = resourcePath;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getNet() {
    return net;
  }

  public void setNet(String net) {
    this.net = net;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getApiPath() {
    return apiPath;
  }

  public void setApiPath(String apiPath) {
    this.apiPath = apiPath;
  }

  @Override
  public String toString() {
    return protocol+net+baseUrl+apiPath+resourcePath+queryData;
  }
}
