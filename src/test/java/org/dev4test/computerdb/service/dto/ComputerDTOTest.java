package org.dev4test.computerdb.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.dev4test.computerdb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComputerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComputerDTO.class);
        ComputerDTO computerDTO1 = new ComputerDTO();
        computerDTO1.setId(1L);
        ComputerDTO computerDTO2 = new ComputerDTO();
        assertThat(computerDTO1).isNotEqualTo(computerDTO2);
        computerDTO2.setId(computerDTO1.getId());
        assertThat(computerDTO1).isEqualTo(computerDTO2);
        computerDTO2.setId(2L);
        assertThat(computerDTO1).isNotEqualTo(computerDTO2);
        computerDTO1.setId(null);
        assertThat(computerDTO1).isNotEqualTo(computerDTO2);
    }
}
