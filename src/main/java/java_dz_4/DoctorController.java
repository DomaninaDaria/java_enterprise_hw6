package java_dz_4;


import java_dz_4.dto.DoctorDtoConverter;
import java_dz_4.dto.DoctorInputOutputDto;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class DoctorController {
    private final SpecializationsList specializationsList;
    private final DoctorService doctorService;
    private final DoctorDtoConverter doctorDtoConverter = Mappers.getMapper(DoctorDtoConverter.class);
    private final UriComponentsBuilder uriComponentsBuilder;

    public DoctorController(DoctorService doctorService,
                            @Value("${doctors.host-name:localhost}") String hostName,
                            SpecializationsList specializationsList) {
        this.doctorService = doctorService;

        this.specializationsList = specializationsList;
        //System.out.println(specializationsList.toString());

        uriComponentsBuilder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(hostName)
                .path("/doctors/{id}");
    }

    @GetMapping("/doctors/{id}")
    public DoctorInputOutputDto findById(@PathVariable Integer id) {
        return doctorService.findById(id)
                .map(doctorDtoConverter::toDto)
                .orElseThrow(DoctorNotFoundException::new);
    }


    @GetMapping("/doctors")
    public List<DoctorInputOutputDto> findAll(@RequestParam Optional<List<String>> specializations,
                                              @RequestParam Optional<String> name) {
        List<DoctorInputOutputDto> doctors = doctorService.findAll(specializations, name)
                .stream()
                .map(doctorDtoConverter::toDto)
                .collect(Collectors.toList());
        return doctors;

    }


    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctor(@RequestBody DoctorInputOutputDto dto) {
        Doctor doctor = doctorDtoConverter.toModel(dto);
        boolean checkSp = specializationsList.getSpecializations().stream()
                .anyMatch(sp -> sp.equals(doctor.getSpecialization()));
        if (checkSp) {
            doctorService.createDoctor(doctor);
            URI uri = uriComponentsBuilder.build(doctor.getId());
            return ResponseEntity.created(uri).build();
        } else throw new SpecializationNotFoundException(specializationsList.getSpecializations().toString());
    }


    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctor(@RequestBody DoctorInputOutputDto dto,
                                          @PathVariable Integer id) {

        Doctor doctor = doctorDtoConverter.toModel(dto, id);
        boolean checkSp = specializationsList.getSpecializations().stream()
                .anyMatch(sp -> sp.equals(doctor.getSpecialization()));
        if (checkSp) {
            doctorService.updateDoctor(doctor);
            return ResponseEntity.noContent().build();
        } else throw new SpecializationNotFoundException(specializationsList.getSpecializations().toString());
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer id) {

        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noSuchDoctorOrSpecialization(DoctorNotFoundException e1, SpecializationNotFoundException e2) {

    }
}