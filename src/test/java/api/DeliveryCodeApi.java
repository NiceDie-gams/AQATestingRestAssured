package api;

import dto.DeliveryData;
import dto.FioData;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.json.JSONObject;
import util.ApiEndpoints;
import util.Specification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class DeliveryCodeApi {
    private final String baseUrl;
    private final String apiKey;
    private final String secretKey;

    public DeliveryCodeApi(String apiKey, String secretKey){
        this.baseUrl = ApiEndpoints.DELIVERY_ID_URL.getPath();
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }



    public List<DeliveryData> sendKladrCode(String kladrCode){
        JSONObject data = new JSONObject(); //Так как принимает он оказывается только JSON
        data.put("query", kladrCode);

        return given()
                .spec(Specification.requestSpec(baseUrl, apiKey))
                .body(data.toString())
                .when()
                .accept(ContentType.JSON)
                .post()
                .then()
                .spec(Specification.responseSpecOK())
                .log().status().and().log().body()
                .extract().body()
                .jsonPath().getList("suggestions.data", DeliveryData.class);
    }

    public ValidatableResponse sendRequestWithoutBody() {
        return given()
                .header("X-Secret", secretKey)
                .spec(Specification.requestSpec(baseUrl, apiKey))
                .when()
                .accept(ContentType.JSON)
                .post()
                .then();
    }

    public ValidatableResponse sendRequestWithoutAuth(String kladrCode) {
        JSONObject data = new JSONObject();

        data.put("query", kladrCode);

        return given()
                .contentType(ContentType.JSON)
                .body(data.toString())
                .when()
                .accept(ContentType.JSON)
                .post(baseUrl)
                .then();
    }
}
