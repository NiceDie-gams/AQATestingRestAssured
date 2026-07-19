package tests;

import api.FioHintApi;
import dto.FioData;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Specification;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
@DisplayName("Проверка API для стандартизации ФИО")
public class FioHintApiTest {

    private static FioHintApi fioHintApi;

    @BeforeAll
    static void setUp(){
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("API_KEY");
        String secretKey = dotenv.get("SECRET_KEY");
        fioHintApi = new FioHintApi(apiKey, secretKey);
    }

    @ParameterizedTest
    @MethodSource("util.TestDataLoader#getFioAndExpectedFio")
    @DisplayName("Проверка корректного преобразования имен")
    public void FioStadartisationTest(String fio, String correct_fio){

        FioData result = fioHintApi.getStandardizedFio(fio);

        assertEquals(correct_fio, result.getResult(), "Ошибка, результат преобразования не совпадает с ожидаемым");
    }

    @ParameterizedTest
    @MethodSource("util.TestDataLoader#getFioAndExpectedGender")
    @DisplayName("Проверка корректного определения пола")
    public void correctGenderRecognitionTest(String fio, String expected_gender){

        FioData result = fioHintApi.getStandardizedFio(fio);

        assertEquals(expected_gender, result.getGender(), "Ошибка, пол не совпадает с ожидаемым");
    }

    @DisplayName("Проверка пустого значения для поля ввода")
    @Test
    public void queryImportanceTest(){
        /*
        Тест
        Проверка на необходимость поля query. Ответ должен прийти со статусом 400
        */

        fioHintApi.sendRequestWithoutBody().spec(Specification.responseSpec400());
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

        fioHintApi.sendRequestWithoutAuth(fio).spec(Specification.responseSpec401());

    }
}

