package com.example.clinic.model;

public class DoctorList {

    private String Doctor_ID;
    private String Name;
    private String Email;
    private String Address;
    private String Education;
    private String Experience;
    private String Specialization;
    private String Contact_N0;
    private String Shift;
    private String Age;
    private String Gender;
    private static String Status;

    public DoctorList() {
    }

    public DoctorList(String doctor_ID, String name, String email, String address, String education, String specialization, String contact_N0, String experience, String shift, String age, String gender, String status) {
        this.Doctor_ID = doctor_ID;
        this.Name = name;
        this.Email = email;
        this.Address = address;
        this.Education = education;
        this.Specialization = specialization;
        this.Contact_N0 = contact_N0;
        this.Experience = experience;
        this.Shift = shift;
        this.Age = age;
        this.Gender = gender;
        this.Status = status;
    }

    public String getDoctor_ID() {
        return Doctor_ID;
    }

    public void setDoctor_ID(String doctor_ID) {
        this.Doctor_ID = doctor_ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
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

    public String getSpecialization() {
        return Specialization;
    }

    public void setSpecialization(String specialization) {
        this.Specialization = specialization;
    }

    public String getExperience() {
        return Experience;
    }

    public void setExperience(String experience) {
        this.Experience = experience;
    }

    public String getContact_N0() {
        return Contact_N0;
    }

    public void setContact_N0(String contact_N0) {
        this.Contact_N0 = contact_N0;
    }

    public String getShift() {
        return Shift;
    }

    public void setShift(String shift) {
        this.Shift = shift;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        this.Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public static String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }
}
