package com.bcb.vetra.viewmodels;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model class for a prescription with medication information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionWithMedication {
    private int prescriptionId;
    @NotBlank(message = "Name is required.")
    private String name;
    @NotBlank(message = "Quantity is required.")
    private int quantity;
    @NotBlank(message = "Unit is required.")
    private String unit;
    @NotBlank(message = "Instructions are required.")
    private String instructions;
    private boolean isActive;
    @NotBlank(message = "Patient ID is required.")
    private int patientId;
    @NotBlank(message = "Doctor ID is required.")
    private int doctorId;
}