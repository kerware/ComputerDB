package org.dev4test.computerdb.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class ComputerMapperTest {

    private ComputerMapper computerMapper;

    @BeforeEach
    public void setUp() {
        computerMapper = new ComputerMapperImpl();
    }
}
