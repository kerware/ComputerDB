package org.dev4test.computerdb.service.mapper;

import org.dev4test.computerdb.domain.Company;
import org.dev4test.computerdb.domain.Computer;
import org.dev4test.computerdb.service.dto.CompanyDTO;
import org.dev4test.computerdb.service.dto.ComputerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Computer} and its DTO {@link ComputerDTO}.
 */
@Mapper(componentModel = "spring")
public interface ComputerMapper extends EntityMapper<ComputerDTO, Computer> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyName")
    ComputerDTO toDto(Computer s);

    @Named("companyName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CompanyDTO toDtoCompanyName(Company company);
}
