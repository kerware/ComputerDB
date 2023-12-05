package org.dev4tests.computerdb.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.dev4tests.computerdb.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
