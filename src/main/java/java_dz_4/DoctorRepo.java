package java_dz_4;


import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class DoctorRepo {
    private final Map<Integer, Doctor> idToDoctor = new ConcurrentHashMap<>();

    private final AtomicInteger counter = new AtomicInteger(0);

    public List<Doctor> findAll() {

        return new ArrayList<>(idToDoctor.values());
    }

    public Optional<Doctor> findById(Integer id) {
        return Optional.ofNullable(idToDoctor.get(id));
    }


    public Doctor createDoctor(Doctor doctor) {
        int id = counter.incrementAndGet();
        Doctor created = new Doctor(id, doctor.getName(), doctor.getSpecialization());
        idToDoctor.put(created.getId(), created);
        return created;
    }


    public void updateDoctor(Doctor doctor) {
        if (idToDoctor.containsKey(doctor.getId())) {
            idToDoctor.replace(doctor.getId(), doctor);
        }else{
            throw new DoctorNotFoundException();
        }
    }

    public void deleteDoctor(Integer id) {
        if (idToDoctor.containsKey(id)) {
            idToDoctor.remove(id);
        }else{
            throw new DoctorNotFoundException();
        }
    }


    public void cleanAll() {
        counter.set(0);
        idToDoctor.clear();
    }
}