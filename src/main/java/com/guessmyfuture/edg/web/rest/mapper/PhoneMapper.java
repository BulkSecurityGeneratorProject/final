package com.guessmyfuture.edg.web.rest.mapper;

import com.guessmyfuture.edg.domain.*;
import com.guessmyfuture.edg.web.rest.dto.PhoneDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Phone and its DTO PhoneDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PhoneMapper {

    @Mapping(source = "student.id", target = "studentId")
    PhoneDTO phoneToPhoneDTO(Phone phone);

    List<PhoneDTO> phonesToPhoneDTOs(List<Phone> phones);

    @Mapping(source = "studentId", target = "student")
    Phone phoneDTOToPhone(PhoneDTO phoneDTO);

    List<Phone> phoneDTOsToPhones(List<PhoneDTO> phoneDTOs);

    default Student studentFromId(Long id) {
        if (id == null) {
            return null;
        }
        Student student = new Student();
        student.setId(id);
        return student;
    }
}
