package org.dev4test.computerdb.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class CompanyMapperTest {

    private CompanyMapper companyMapper;

    @BeforeEach
    public void setUp() {
        companyMapper = new CompanyMapperImpl();
    }
}