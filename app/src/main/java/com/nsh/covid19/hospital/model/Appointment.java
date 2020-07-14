package com.nsh.covid19.hospital.model;

public class Appointment {

    String time;
    String phone;
    String doctor_id;
    String patient_id;
    String message;
    String patient_name;
    String doctor_name;
    String appointment_id;

    public Appointment(String time, String doctor_id, String patient_id, String message, String patient_name, String doctor_name, String phone, String appointment_id) {
        this.time = time;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.message = message;
        this.patient_name = patient_name;
        this.doctor_name = doctor_name;
        this.phone = phone;
        this.appointment_id = appointment_id;
    }

    public String getappointment_id() {
        return appointment_id;
    }

    public String getPhone() {
        return phone;
    }

    public String getTime() {
        return time;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public String getMessage() {
        return message;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public String getDoctor_name() {
        return doctor_name;
    }
}
