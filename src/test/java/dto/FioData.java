package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FioData {
    private String result;
    private String name;
    private String surname;
    private String patronymic;
    private String gender;

    public FioData(String result, String gender, String patronymic, String surname, String name) {
        this.result = result;
        this.gender = gender;
        this.patronymic = patronymic;
        this.surname = surname;
        this.name = name;
    }

    public FioData(){}

    public String getResult() {
        return result;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getGender() {
        return gender;
    }
}
