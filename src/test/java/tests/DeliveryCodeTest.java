package tests;

import dto.DeliveryData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.ApiEndpoints;
import util.Specification;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.JSONObject;

import util.TestDataLoader;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import io.github.cdimascio.dotenv.Dotenv;

@DisplayName("Проверка работы API с КЛАДР-кодами городов")
public class DeliveryCodeTest { //Инденпотентность разобраться!!!

    @BeforeEach
    public void setUp() {
        RestAssured.reset();
    }

    @ParameterizedTest(name = "Проверка соответствия КЛАДР-кода {0} и СДЭК {1}")
    @MethodSource("util.TestDataLoader#getAllCorrectKladrAndCdek")
    @DisplayName("Проверка соответствия КЛАДР-кода СДЭК-id")
    public void checkDeliveryCode(String kladrCode, String cdekCode){
        /*
        Тест
        Проверка корректного ответа для различных query (в данном случае query - это КЛАДР-код).
        В данном API КЛАДР-коды только городов. В качестве тестовых занчений я выбрал список со
        значениями как реальных КЛАДР-кодов городов.
        */

        Dotenv dotenv = Dotenv.load(); //Загружаем .env

        JSONObject data = new JSONObject(); //Так как принимает он оказывается только JSON

        data.put("query", kladrCode);
        List<DeliveryData> deliveries =
                    given()
                    .spec(Specification.requestSpec(ApiEndpoints.DELIVERY_ID_URL.getPath(), dotenv.get("API_KEY")))
                    .body(data.toString())
                    .when()
                    .accept(ContentType.JSON)
                    .post()
                    .then()
                    .spec(Specification.responseSpecOK())
                    .log().status().and().log().body()
                    .extract().body()
                    .jsonPath().getList("suggestions.data", DeliveryData.class);

                /*
                Если условие выполнено значит, потенциально данный запрос должен вернуть
                данные, далее идет проверка, что ответ не пуст в assertFalse, а после уже
                идет проверка на то коректыный ли ответ в assertEquals
                */
                assertFalse(deliveries.isEmpty(), "Для запроса " + kladrCode + " ответ не пуст, а должен быть пустым");
                String actual = deliveries.get(0).getCdek_id();
                assertEquals (cdekCode, actual, "Неверный cdek_id для запроса " + kladrCode);
        }

    @ParameterizedTest(name="Проверка соответствия КЛАДР-кода {0} и СДЭК {1}")
    @MethodSource("util.TestDataLoader#getAllIncorrectKladrAndCdek")
    @DisplayName("Проверка обработки не корректных КЛАДР")
    public void checkDeliveryCodeForIncorrectKladr(String kladr, String cdek) {
        /*
        Тест
        Проверка корректного ответа для различных query (в данном случае query - это КЛАДР-код).
        В данном API КЛАДР-коды только городов. В качестве тестовых занчений я выбрал список со
        значениями как реальных КЛАДР-кодов городов, так и ложных, и пустых значений.
        */

        Dotenv dotenv = Dotenv.load(); //Загружаем .env

        JSONObject data = new JSONObject(); //Так как принимает он оказывается только JSON

        data.put("query", kladr);
        List<DeliveryData> deliveries =
                given()
                        .spec(Specification.requestSpec(ApiEndpoints.DELIVERY_ID_URL.getPath(), dotenv.get("API_KEY")))
                        .body(data.toString())
                        .when()
                        .accept(ContentType.JSON)
                        .post()
                        .then()
                        .spec(Specification.responseSpecOK())
                        .log().status().and().log().body()
                        .extract().body()
                        .jsonPath().getList("suggestions.data", DeliveryData.class);
        assertTrue(deliveries.isEmpty(), "Для запроса " + kladr + " ответ не пуст, а должен быть пустым");
    }

    @DisplayName("Проверка пустого значения для поля ввода(query)")
    @Test
    public void queryImportanceTest(){
        /*
        Тест
        Проверка на необходимость поля query. Ответ должен прийти со статусом 400
        */

        Dotenv dotenv = Dotenv.load(); //Загружаем .env

        ValidatableResponse responce =
                    given()
                            .spec(Specification.requestSpec(ApiEndpoints.DELIVERY_ID_URL.getPath(), dotenv.get("API_KEY")))
                            .when()
                            .accept(ContentType.JSON)
                            .post()
                            .then()
                            .spec(Specification.responseSpec400())
                            .log().status().and().log().body();
    }

    @ParameterizedTest
    @MethodSource("util.TestDataLoader#getCorrectKladrArg")
    @DisplayName("Проверка доступности API без ключа для авторизации")
    public void authorizationKeyImportanceTest(String kladrCode){
        /*
        Тест
        Проверка на необходимость авторизации. Запуск без оправки заголовка авторизации.
        Ожидаеймый статус ответа 401.
        */

        Dotenv dotenv = Dotenv.load(); //Загружаем .env

        JSONObject data = new JSONObject();

        data.put("query", kladrCode);

            Response response =
                    given()
                            .contentType(ContentType.JSON)
                            .body(data.toString())
                            .when()
                            .accept(ContentType.JSON)
                            .post(ApiEndpoints.DELIVERY_ID_URL.getPath());
            assertTrue( response.getStatusCode() == 401, "Ожидался 401 ответ, а пришел " + response.getStatusCode());
        }
    }


