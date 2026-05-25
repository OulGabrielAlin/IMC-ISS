package org.example.domain.datatypes;

import jakarta.persistence.Embeddable;

@Embeddable
public class Adress {
    private String street;
    private String number;
    private String city;
    private String county;
    private String country;
    private String postalCode;

    public Adress() {
    }

    public Adress(String street, String number, String city) {
        this.street = street;
        this.number = number;
        this.city = city;
    }

    public Adress(String street, String number, String city, String county, String country, String postalCode) {
        this.street = street;
        this.number = number;
        this.city = city;
        this.county = county;
        this.country = country;
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
