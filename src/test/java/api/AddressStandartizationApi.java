package api;

import dto.AddressStandartizationData;
import dto.AddressStandartizationMinData;
import dto.FioData;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import util.ApiEndpoints;
import util.Specification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class AddressStandartizationApi {
    private final String baseUrl;
    private final String apiKey;
    private final String secretKey;

    public AddressStandartizationApi(String apiKey, String secretKey){
        this.baseUrl = ApiEndpoints.ADDRESS_STANDARTIZATION_URL.getPath();
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public AddressStandartizationData getStandardizedAddress(String address){
        return given()
                .spec(Specification.requestSpec(baseUrl, apiKey))
                .header("X-Secret", secretKey)
                .body(List.of(address))
                .when()
                .accept(ContentType.JSON)
                .post()
                .then()
                .spec(Specification.responseSpecOK())
                .log().all()
                .extract().body().jsonPath()
                .getObject("[0]", AddressStandartizationData.class);
    }

    public AddressStandartizationMinData getStandardizedAddressMin(String address){
        return given()
                .spec(Specification.requestSpec(baseUrl, apiKey))
                .header("X-Secret", secretKey) // Нативно добавил сюда секретку ибо запрос платный и требует секретного ключа
                .body(List.of(address))
                .when()
                .post()
                .then()
                .spec(Specification.responseSpecOK())
                .log().status().and().log().body()
                .extract().body()
                .jsonPath().getObject("[0]", AddressStandartizationMinData.class);
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

    public ValidatableResponse sendRequestWithoutAuth(String address) {
        return given()
                .contentType(ContentType.JSON)
                .body(List.of(address))
                .when()
                .accept(ContentType.JSON)
                .post(baseUrl)
                .then();
    }
}
