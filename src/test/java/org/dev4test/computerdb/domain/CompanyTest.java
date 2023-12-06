package org.dev4test.computerdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dev4test.computerdb.domain.CompanyTestSamples.*;
import static org.dev4test.computerdb.domain.ComputerTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.dev4test.computerdb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompanyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Company.class);
        Company company1 = getCompanySample1();
        Company company2 = new Company();
        assertThat(company1).isNotEqualTo(company2);

        company2.setId(company1.getId());
        assertThat(company1).isEqualTo(company2);

        company2 = getCompanySample2();
        assertThat(company1).isNotEqualTo(company2);
    }

    @Test
    void computerTest() throws Exception {
        Company company = getCompanyRandomSampleGenerator();
        Computer computerBack = getComputerRandomSampleGenerator();

        company.addComputer(computerBack);
        assertThat(company.getComputers()).containsOnly(computerBack);
        assertThat(computerBack.getCompany()).isEqualTo(company);

        company.removeComputer(computerBack);
        assertThat(company.getComputers()).doesNotContain(computerBack);
        assertThat(computerBack.getCompany()).isNull();

        company.computers(new HashSet<>(Set.of(computerBack)));
        assertThat(company.getComputers()).containsOnly(computerBack);
        assertThat(computerBack.getCompany()).isEqualTo(company);

        company.setComputers(new HashSet<>());
        assertThat(company.getComputers()).doesNotContain(computerBack);
        assertThat(computerBack.getCompany()).isNull();
    }
}
