package org.dev4test.computerdb.service.mapper;

import org.dev4test.computerdb.domain.Company;
import org.dev4test.computerdb.service.dto.CompanyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Company} and its DTO {@link CompanyDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper extends EntityMapper<CompanyDTO, Company> {}
