package dto;

public class BadResponseData {
    private String HttpResponse;
    private String Reason;

    public BadResponseData(String httpResponse, String reason) {
        HttpResponse = httpResponse;
        Reason = reason;
    }

    public BadResponseData(){};

    public String getHttpResponse() {
        return HttpResponse;
    }

    public String getReason() {
        return Reason;
    }
}
