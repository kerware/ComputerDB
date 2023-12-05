package org.dev4test.computerdb.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.dev4test.computerdb.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
