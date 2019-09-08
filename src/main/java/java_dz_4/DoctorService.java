package java_dz_4;


import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DoctorService {
    private DoctorRepo doctorRepo;

    public List<Doctor> findAll(Optional<List<String>> specializations, Optional<String> name) {
        if (specializations.isPresent() && name.isPresent()) {
            return doctorRepo.findBySpecializationInAndNameIgnoreCase(specializations.get(), name.get());
        }
        if (specializations.isPresent()) {
            return doctorRepo.findBySpecializationIn(specializations.get());
        }
        if (name.isPresent()) {
            return doctorRepo.findByNameIgnoreCase(name.get());
        }
        return doctorRepo.findAll();
    }

    public Optional<Doctor> findById(Integer id) {
        return doctorRepo.findById(id);
    }

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepo.save(doctor);
    }

    public void updateDoctor(Doctor doctor) {
        doctorRepo.save(doctor);
    }

    public void deleteDoctor(Integer id) {
        try {
            doctorRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DoctorNotFoundException();
        }
    }
}