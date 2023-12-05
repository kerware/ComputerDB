package com.myapp.domain;

import static com.myapp.domain.CompanyTestSamples.*;
import static com.myapp.domain.ComputerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComputerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Computer.class);
        Computer computer1 = getComputerSample1();
        Computer computer2 = new Computer();
        assertThat(computer1).isNotEqualTo(computer2);

        computer2.setId(computer1.getId());
        assertThat(computer1).isEqualTo(computer2);

        computer2 = getComputerSample2();
        assertThat(computer1).isNotEqualTo(computer2);
    }

    @Test
    void companyTest() throws Exception {
        Computer computer = getComputerRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        computer.setCompany(companyBack);
        assertThat(computer.getCompany()).isEqualTo(companyBack);

        computer.company(null);
        assertThat(computer.getCompany()).isNull();
    }
}
