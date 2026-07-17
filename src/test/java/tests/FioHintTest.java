package tests;

import dto.FioData;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.ApiEndpoints;
import util.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
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

        assertEquals(correct_fio, result.getFirst().getResult(), "Ошибка, результат преобразования не совпадает с ожидаемым");
    }

    @ParameterizedTest
    @MethodSource("util.TestDataLoader#getFioAndExpectedGender")
    @DisplayName("Проверка корректного определения пола")
    public void correctGenderRecognitionTest(String fio, String expected_gender){
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

        assertEquals(expected_gender, result.getFirst().getGender(), "Ошибка, пол не совпадает с ожидаемым");
    }

    @DisplayName("Проверка пустого значения для поля ввода")
    @Test
    public void queryImportanceTest(){
        /*
        Тест
        Проверка на необходимость поля query. Ответ должен прийти со статусом 400
        */

        Dotenv dotenv = Dotenv.load(); //Загружаем .env

        ValidatableResponse response =
                given()
                        .header("X-Secret", dotenv.get("SECRET_KEY"))
                        .spec(Specification.requestSpec(ApiEndpoints.FIO_HINT_URL.getPath(), dotenv.get("API_KEY")))
                        .when()
                        .accept(ContentType.JSON)
                        .post()
                        .then()
                        .spec(Specification.responseSpec400())
                        .log().status().and().log().body();
    }

    @ParameterizedTest
    @MethodSource("util.TestDataLoader#getOneFioForTest")
    @DisplayName("Проверка доступности API без ключа для авторизации")
    public void authorizationKeyImportanceTest(String fio) {
        /*
        Тест
        Проверка на необходимость авторизации. Запуск без отправки заголовка авторизации.
        Ожидаеймый статус ответа 401.
        */

        Dotenv dotenv = Dotenv.load(); //Загружаем .env

        List<String> fioAsArray = Arrays.asList(fio);

        ValidatableResponse response =
                given()
                        .contentType(ContentType.JSON)
                        .body(fioAsArray)
                        .when()
                        .accept(ContentType.JSON)
                        .post(ApiEndpoints.FIO_HINT_URL.getPath())
                        .then()
                        .spec(Specification.responseSpec401());

    }
}

