package core;

import api.DeliveryData;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;

import io.github.cdimascio.dotenv.Dotenv;

public class DeliveryCodeTest {

    private final static String URL = "https://suggestions.dadata.ru/suggestions/api/";

    @Test
    public void checkDeliveryCode(){
        /*
        Тест
        Проверка корректного ответа для различных query (в данном случае query - это КЛАДР-код).
        В данном API КЛАДР-коды только городов. В качестве тестовых занчений я выбрал список со
        значениями как реальных КЛАДР-кодов городов, так и ложных, и пустых значений.
        */
        Dotenv dotenv = Dotenv.load();
        List<String> queries = List.of("3100000100000", "3100400100000", "3600000100000", "6100000100000", "12345678", "3900000100000", "");
        Map<Integer, String> expectedCdekByIndex = Map.of(
                0, "337",
                1, "344",
                2, "506",
                3, "438",
                5, "152"
        );
        JSONObject data = new JSONObject(); //Так как принимает он оказывается только JSON
        //Вся проверка происходит в цикле ниже с использованием RestAssured и JUnit
        for(int i =0; i < queries.size(); i++) {
            data.put("query", queries.get(i));
            List<DeliveryData> deliveries =
                    given()
                    .header("Authorization", "Token " + dotenv.get("API_KEY"))
                    .body(data.toString())
                    .contentType(ContentType.JSON)
                    .when()
                    .accept(ContentType.JSON)
                    .post(URL + "4_1/rs/findById/delivery")
                    .then().log().status().and().log().body()
                    .extract().body()
                    .jsonPath().getList("suggestions.data", DeliveryData.class);

            if (expectedCdekByIndex.containsKey(i)) {
                /*
                Если условие выполнено значит, потенциально данный запрос должен вернуть
                данные, далее идет проверка, что ответ не пуст в assertFalse, а после уже
                идет проверка на то коректыный ли ответ в assertEquals
                */
                String expected = expectedCdekByIndex.get(i);
                assertFalse("Для запроса " + queries.get(i) + " ответ пуст, а должен содержать данные", deliveries.isEmpty());
                String actual = deliveries.get(0).getCdek_id();
                assertEquals("Неверный cdek_id для запроса " + queries.get(i), expected, actual);
            } else {
                /*
                Если условие ложно то тело ответа должно быть пустым. Этот пункт проверяется в assertTrue
                */
                assertTrue("Для запроса " + queries.get(i) + " ответ не пуст, а должен быть пустым", deliveries.isEmpty());
            }
        }

    }
}
