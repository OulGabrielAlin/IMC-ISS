package org.example.domain;

import jakarta.persistence.*;
import org.example.domain.datatypes.Adress;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String lastName;
    private String firstName;

    @Column(length = 13)
    private String cnp;
    private LocalDate dateOfBirth;
    private Double salary;

    @Embedded
    private Adress adress;

    public Client() {
    }

    public Client(String lastName, String firstName, String cnp, LocalDate dateOfBirth, Double salary, Adress adress) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.cnp = cnp;
        this.dateOfBirth = dateOfBirth;
        this.salary = salary;
        this.adress = adress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(cnp, client.cnp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastName, firstName, cnp, dateOfBirth, salary, adress);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", cnp='" + cnp + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", salary=" + salary +
                ", adress=" + adress +
                '}';
    }
}
