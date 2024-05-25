package com.bcb.vetra.controllers;

import com.bcb.vetra.daos.PrescriptionDao;
import com.bcb.vetra.daos.PatientDao;
import com.bcb.vetra.daos.UserDao;
import com.bcb.vetra.services.AccessControl;
import com.bcb.vetra.viewmodels.PrescriptionWithMedication;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * <strong>Controller for prescriptions.</strong>
 * <br><br>
 * This class is responsible for handling all HTTP requests related to prescriptions.
 */
@PreAuthorize("isAuthenticated()")
@RestController
public class PrescriptionController {
    private PrescriptionDao prescriptionDao;
    private UserDao userDao;
    private PatientDao patientDao;
    private AccessControl accessControl;

    public PrescriptionController(PrescriptionDao prescriptionDao, UserDao userDao, PatientDao patientDao) {
        this.prescriptionDao = prescriptionDao;
        this.userDao = userDao;
        this.patientDao = patientDao;
        this.accessControl = new AccessControl(patientDao, userDao);
    }

    //TODO: Auth starting here
    @GetMapping("/patients/{patientId}/prescriptions")
    public List<PrescriptionWithMedication> getAll(@PathVariable int patientId, Principal principal) {
        if (!accessControl.canAccessPatient(patientId, principal.getName())) {
            return null;
        }
        return prescriptionDao.getPrescriptionsByPatientId(patientId);
    }
    @GetMapping("/patients/{patientId}/prescriptions/{prescriptionId}")
    public PrescriptionWithMedication get(@PathVariable int patientId, @PathVariable int prescriptionId, Principal principal) {
        if (!accessControl.canAccessPatient(patientId, principal.getName())) {
            return null;
        }
        return prescriptionDao.getPrescriptionById(prescriptionId);
    }

    @PreAuthorize("hasAnyAuthority('DOCTOR', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/patients/{patientId}/prescriptions")
    public PrescriptionWithMedication create(@PathVariable int patientId, @Valid @RequestBody PrescriptionWithMedication prescription) {
        prescription.setPrescriptionId(patientId);
        return prescriptionDao.create(prescription);
    }

    @PreAuthorize("hasAnyAuthority('DOCTOR', 'ADMIN')")
    @PutMapping("/patients/{patientId}/prescriptions/{prescriptionId}")
    public PrescriptionWithMedication update(@PathVariable int patientId, @PathVariable int prescriptionId, @Valid @RequestBody PrescriptionWithMedication prescription) {
        prescription.setPrescriptionId(prescriptionId);
        return prescriptionDao.update(prescription);
    }

    @PreAuthorize("hasAnyAuthority('DOCTOR', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/patients/{patientId}/prescriptions/{prescriptionId}")
    public void delete(@PathVariable int patientId, @PathVariable int prescriptionId) {
        prescriptionDao.deletePrescriptionOfPet(prescriptionId, patientId);
    }
}
