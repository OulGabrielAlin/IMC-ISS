package org.example.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.example.domain.datatypes.AdminAccesLevel;

import java.util.Objects;

@Entity
public class Administrator extends AngajatIMC {
    @Enumerated(EnumType.STRING)
    private AdminAccesLevel adminAccesLevel;

    public Administrator() {
    }

    public Administrator(AdminAccesLevel adminAccesLevel) {
        this.adminAccesLevel = adminAccesLevel;
    }

    public AdminAccesLevel getAdminAccesLevel() {
        return adminAccesLevel;
    }

    public void setAdminAccesLevel(AdminAccesLevel adminAccesLevel) {
        this.adminAccesLevel = adminAccesLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Administrator that = (Administrator) o;
        return adminAccesLevel == that.adminAccesLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(adminAccesLevel);
    }
}
