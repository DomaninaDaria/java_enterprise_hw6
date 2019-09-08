package java_dz_4;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DoctorRepo extends JpaRepository<Doctor, Integer> {

    List<Doctor> findBySpecializationInAndNameIgnoreCase(List<String> specializations, String name);

    List<Doctor> findBySpecializationIn(List<String> specializations);

    List<Doctor> findByNameIgnoreCase(String name);
}