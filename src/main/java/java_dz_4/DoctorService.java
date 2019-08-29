package java_dz_4;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DoctorService {
    private DoctorRepo doctorRepo;

    public List<Doctor> findAll(Predicate<Doctor> predicate) {
        return doctorRepo.findAll()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public Optional<Doctor> findById(Integer id) {
        return doctorRepo.findById(id);
    }

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepo.createDoctor(doctor);
    }

    public void updateDoctor(Doctor doctor) {
        doctorRepo.updateDoctor(doctor);
    }

    public void deleteDoctor(Integer id) {
        doctorRepo.deleteDoctor(id);
    }
}