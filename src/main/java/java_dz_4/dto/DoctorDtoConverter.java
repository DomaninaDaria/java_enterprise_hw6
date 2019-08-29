package java_dz_4.dto;

import java_dz_4.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DoctorDtoConverter {
    @Mapping(target = "id", ignore = true)
    Doctor toModel(DoctorInputOutputDto doctorInputDto);

    Doctor toModel(DoctorInputOutputDto dto, Integer id);


    @Mapping(target = "specialization")
    @Mapping(target = "name")
    DoctorInputOutputDto toDto(Doctor doctor);
}
