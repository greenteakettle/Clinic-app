package com.example.clinic.model;

public class Medcard {
    private String cardId;
    private String patientName;
    private String bloodGroup;
    private String dateOfBirth; // Добавляем поле для даты рождения
    private String gender;
    private String information;

    // Конструктор без параметров (необходим для Firebase)
    public Medcard() {
    }

    // Конструктор с параметрами
    public Medcard(String cardId, String patientName, String bloodGroup, String dateOfBirth, String gender, String information) {
        this.cardId = cardId;
        this.patientName = patientName;
        this.bloodGroup = bloodGroup;
        this.dateOfBirth = dateOfBirth; // Добавляем дату рождения
        this.gender = gender;
        this.information = information;
    }

    // Геттеры и сеттеры
    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
