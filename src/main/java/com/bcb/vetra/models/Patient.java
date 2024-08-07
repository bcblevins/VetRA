package com.bcb.vetra.models;

import com.bcb.vetra.services.deserializers.EpochToLocalDateDeserializer;
import com.bcb.vetra.services.deserializers.SexIdToSexDeserializer;
import com.bcb.vetra.services.deserializers.SpeciesIdToSpeciesDeserializer;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class for patient record.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {
    private int patientId;
    @JsonAlias({"name", "firstName"})
    @NotBlank(message = "First name is required.")
    private String name;
    @JsonAlias({"date_of_birth", "birthday"})
    @JsonDeserialize(using = EpochToLocalDateDeserializer.class)
    private LocalDate birthday;
    @JsonAlias({"species", "species_id"})
    @JsonDeserialize(using = SpeciesIdToSpeciesDeserializer.class)
    private String species;
    @JsonAlias({"sex_id", "sex"})
    @JsonDeserialize(using = SexIdToSexDeserializer.class)
    private String sex;
    @NotBlank(message = "Owner username is required.")
    @JsonAlias({"contact_id", "ownerUsername"})
    private String ownerUsername;

    private Map<String, String> vmsIds;

    public Patient(int patientId, String name, LocalDate birthday, String species, String sex, String ownerUsername) {
        this.patientId = patientId;
        this.name = name;
        this.birthday = birthday;
        this.species = species;
        this.sex = sex;
        this.ownerUsername = ownerUsername;
    }
    public void addVmsId(String vmsName, String vmsId) {
        if (vmsIds == null) {
            vmsIds = new HashMap<>();
        }
        vmsIds.put(vmsName, vmsId);
    }

    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", species='" + species + '\'' +
                ", sex='" + sex + '\'' +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", vmsIds='" + vmsIds.toString() + '\'';
    }

}
