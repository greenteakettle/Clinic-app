package com.example.clinic.model;

public class BookedAppointmentList {

    private String Date;
    private String Time;
    private String Doctor_ID;
    private String PatientID;
    private String Shift;
    private String Status; // Добавлено поле Status
    private String Experience; // Добавлено поле Experience
    private String Specialization; // Добавлено поле Specialization
    private String Email; // Добавлено поле Email
    private String Address; // Добавлено поле Address
    private String Education; // Добавлено поле Education
    private String Contact_N0; // Добавлено поле Contact_N0
    private String Gender; // Добавлено поле Gender
    private String Age; // Добавлено поле Age
    private String Name; // Добавлено поле Name

    public BookedAppointmentList() {
        // Пустое тело конструктора
    }

    public BookedAppointmentList(String date, String time, String doctor_ID, String patientID, String shift, String status,
                                 String experience, String specialization, String email, String address, String education,
                                 String contact_N0, String gender, String age, String name) {
        this.Date = date;
        this.Time = time;
        this.Doctor_ID = doctor_ID;
        this.PatientID = patientID;
        this.Shift = shift;
        this.Status = status;
        this.Experience = experience;
        this.Specialization = specialization;
        this.Email = email;
        this.Address = address;
        this.Education = education;
        this.Contact_N0 = contact_N0;
        this.Gender = gender;
        this.Age = age;
        this.Name = name;
    }

    // Геттеры и сеттеры для всех полей

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getDoctor_ID() {
        return Doctor_ID;
    }

    public void setDoctor_ID(String doctor_ID) {
        this.Doctor_ID = doctor_ID;
    }

    public String getPatientID() {
        return PatientID;
    }

    public void setPatientID(String patientID) {
        this.PatientID = patientID;
    }

    public String getShift() {
        return Shift;
    }

    public void setShift(String shift) {
        this.Shift = shift;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getExperience() {
        return Experience;
    }

    public void setExperience(String experience) {
        this.Experience = experience;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public void setSpecialization(String specialization) {
        this.Specialization = specialization;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getEducation() {
        return Education;
    }

    public void setEducation(String education) {
        this.Education = education;
    }

    public String getContact_N0() {
        return Contact_N0;
    }

    public void setContact_N0(String contact_N0) {
        this.Contact_N0 = contact_N0;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        this.Age = age;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }
}
