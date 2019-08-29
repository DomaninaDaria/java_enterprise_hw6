package java_dz_4;


import java_dz_4.dto.DoctorDtoConverter;
import java_dz_4.dto.DoctorInputOutputDto;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorDtoConverter doctorDtoConverter= Mappers.getMapper(DoctorDtoConverter.class);


    @GetMapping("/doctors/{id}")
    public DoctorInputOutputDto findById(@PathVariable Integer id) {
        return doctorService.findById(id)
                .map(doctorDtoConverter::toDto)
                .orElseThrow(DoctorNotFoundException::new);
    }


    @GetMapping("/doctors")
    public List<DoctorInputOutputDto> findAll(@RequestParam Optional<String> specialization,
                                              @RequestParam Optional<String> name) {
        Optional<Predicate<Doctor>> mayBeSpecPredicate = specialization.map(this::filterBySpecialization);
        Optional<Predicate<Doctor>> mayBeNamePredicate = name.map(this::filterByFirstLetterOfName);
        Predicate<Doctor> predicate = Stream.of(mayBeNamePredicate, mayBeSpecPredicate)
                .flatMap(Optional::stream)
                .reduce(Predicate::and)
                .orElse(doctor -> true);
        List<DoctorInputOutputDto> doctors = doctorService.findAll(predicate)
                .stream()
                .map(doctorDtoConverter::toDto)
                .collect(Collectors.toList());
        return doctors;

    }

    private Predicate<Doctor> filterBySpecialization(String spec) {
        return doctor -> doctor.getSpecialization().equals(spec);
    }

    private Predicate<Doctor> filterByFirstLetterOfName(String letter) {
        return doctor -> doctor.getName().substring(0, 1).equals(letter);
    }


    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctor(@RequestBody DoctorInputOutputDto dto) {
        Doctor doctor = doctorService.createDoctor(doctorDtoConverter.toModel(dto));
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/doctors/{id}")
                .build(doctor.getId());
        return ResponseEntity.created(uri).build();
    }


    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctor(@RequestBody DoctorInputOutputDto dto,
                                          @PathVariable Integer id) {

        Doctor doctor = doctorDtoConverter.toModel(dto, id);
        doctorService.updateDoctor(doctor);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer id) {

        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noSuchDoctor(DoctorNotFoundException e) {

    }
}