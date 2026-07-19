package tests;

import api.DeliveryCodeApi;
import dto.DeliveryData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Specification;
import org.junit.jupiter.api.Test;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import io.github.cdimascio.dotenv.Dotenv;

@Execution(ExecutionMode.CONCURRENT)
@DisplayName("Проверка работы API с КЛАДР-кодами городов")
public class DeliveryCodeTest {
    private static DeliveryCodeApi deliveryCodeApi;

    @BeforeAll
    static void setUp(){
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("API_KEY");
        String secretKey = dotenv.get("SECRET_KEY");
        deliveryCodeApi = new DeliveryCodeApi(apiKey, secretKey);
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

        List<DeliveryData> deliveries = deliveryCodeApi.sendKladrCode(kladrCode);

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

        List<DeliveryData> deliveries = deliveryCodeApi.sendKladrCode(kladr);
        assertTrue(deliveries.isEmpty(), "Для запроса " + kladr + " ответ не пуст, а должен быть пустым");
    }

    @DisplayName("Проверка пустого значения для поля ввода(query)")
    @Test
    public void queryImportanceTest(){
        /*
        Тест
        Проверка на необходимость поля query. Ответ должен прийти со статусом 400
        */

        deliveryCodeApi.sendRequestWithoutBody().spec(Specification.responseSpec400());
    }

    @ParameterizedTest
    @MethodSource("util.TestDataLoader#getCorrectKladrArg")
    @DisplayName("Проверка доступности API без ключа для авторизации")
    public void authorizationKeyImportanceTest(String kladrCode) {
        /*
        Тест
        Проверка на необходимость авторизации. Запуск без оправки заголовка авторизации.
        Ожидаеймый статус ответа 401.
        */
        deliveryCodeApi.sendRequestWithoutAuth(kladrCode).spec(Specification.responseSpec401());
    }
}


