package api;

import dto.FioData;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import util.ApiEndpoints;
import util.Specification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class FioHintApi {
    private final String baseUrl;
    private final String apiKey;
    private final String secretKey;

    public FioHintApi(String apiKey, String secretKey){
        this.baseUrl = ApiEndpoints.FIO_HINT_URL.getPath();
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }



    public FioData getStandardizedFio(String fio){
        return given()
                .spec(Specification.requestSpec(baseUrl, apiKey))
                .header("X-Secret", secretKey)
                .body(List.of(fio))
                .when()
                .accept(ContentType.JSON)
                .post()
                .then()
                .spec(Specification.responseSpecOK())
                .log().all()
                .extract().body().jsonPath()
                .getObject("[0]", FioData.class);
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

    public ValidatableResponse sendRequestWithoutAuth(String fio) {
        return given()
                .contentType(ContentType.JSON)
                .body(List.of(fio))
                .when()
                .accept(ContentType.JSON)
                .post(baseUrl)
                .then();
    }
}
