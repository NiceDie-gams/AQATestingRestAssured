package tests;

import dto.FioData;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.ApiEndpoints;
import util.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Проверка API для стандартизации ФИО")
public class FioHintTest {

    @ParameterizedTest
    @MethodSource("util.TestDataLoader#getFioAndExpectedFio")
    @DisplayName("Проверка корректного преобразования имен")
    public void FioStadartisationTest(String fio, String correct_fio){
        Dotenv dotenv = Dotenv.load();

        List<FioData> result = new ArrayList<>();
        List<String> fioAsArray = Arrays.asList(fio);

        result.addLast(given()
                .spec(Specification.requestSpec(ApiEndpoints.FIO_HINT_URL.getPath(), dotenv.get("API_KEY")))
                .header("X-Secret", dotenv.get("SECRET_KEY"))
                .body(fioAsArray)
                .when()
                .accept(ContentType.JSON)
                .post()
                .then()
                .spec(Specification.responseSpecOK())
                .log().all()
                .extract().body().jsonPath()
                .getObject("[0]", FioData.class)
        );

        assertEquals(correct_fio, result.getFirst().getResult(), "Error");
    }

}
