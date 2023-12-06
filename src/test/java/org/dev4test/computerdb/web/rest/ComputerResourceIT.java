package org.dev4test.computerdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.dev4test.computerdb.IntegrationTest;
import org.dev4test.computerdb.domain.Company;
import org.dev4test.computerdb.domain.Computer;
import org.dev4test.computerdb.repository.ComputerRepository;
import org.dev4test.computerdb.service.ComputerService;
import org.dev4test.computerdb.service.dto.ComputerDTO;
import org.dev4test.computerdb.service.mapper.ComputerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ComputerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ComputerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_INTRODUCED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_INTRODUCED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_INTRODUCED = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_REMOVED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REMOVED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_REMOVED = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_HARDWARE = 0;
    private static final Integer UPDATED_HARDWARE = 1;
    private static final Integer SMALLER_HARDWARE = 0 - 1;

    private static final Integer DEFAULT_SOFTWARE = 0;
    private static final Integer UPDATED_SOFTWARE = 1;
    private static final Integer SMALLER_SOFTWARE = 0 - 1;

    private static final String ENTITY_API_URL = "/api/computers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ComputerRepository computerRepository;

    @Mock
    private ComputerRepository computerRepositoryMock;

    @Autowired
    private ComputerMapper computerMapper;

    @Mock
    private ComputerService computerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restComputerMockMvc;

    private Computer computer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Computer createEntity(EntityManager em) {
        Computer computer = new Computer()
            .name(DEFAULT_NAME)
            .introduced(DEFAULT_INTRODUCED)
            .removed(DEFAULT_REMOVED)
            .hardware(DEFAULT_HARDWARE)
            .software(DEFAULT_SOFTWARE);
        return computer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Computer createUpdatedEntity(EntityManager em) {
        Computer computer = new Computer()
            .name(UPDATED_NAME)
            .introduced(UPDATED_INTRODUCED)
            .removed(UPDATED_REMOVED)
            .hardware(UPDATED_HARDWARE)
            .software(UPDATED_SOFTWARE);
        return computer;
    }

    @BeforeEach
    public void initTest() {
        computer = createEntity(em);
    }

    @Test
    @Transactional
    void createComputer() throws Exception {
        int databaseSizeBeforeCreate = computerRepository.findAll().size();
        // Create the Computer
        ComputerDTO computerDTO = computerMapper.toDto(computer);
        restComputerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(computerDTO)))
            .andExpect(status().isCreated());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeCreate + 1);
        Computer testComputer = computerList.get(computerList.size() - 1);
        assertThat(testComputer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testComputer.getIntroduced()).isEqualTo(DEFAULT_INTRODUCED);
        assertThat(testComputer.getRemoved()).isEqualTo(DEFAULT_REMOVED);
        assertThat(testComputer.getHardware()).isEqualTo(DEFAULT_HARDWARE);
        assertThat(testComputer.getSoftware()).isEqualTo(DEFAULT_SOFTWARE);
    }

    @Test
    @Transactional
    void createComputerWithExistingId() throws Exception {
        // Create the Computer with an existing ID
        computer.setId(1L);
        ComputerDTO computerDTO = computerMapper.toDto(computer);

        int databaseSizeBeforeCreate = computerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restComputerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(computerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = computerRepository.findAll().size();
        // set the field null
        computer.setName(null);

        // Create the Computer, which fails.
        ComputerDTO computerDTO = computerMapper.toDto(computer);

        restComputerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(computerDTO)))
            .andExpect(status().isBadRequest());

        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllComputers() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList
        restComputerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(computer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].introduced").value(hasItem(DEFAULT_INTRODUCED.toString())))
            .andExpect(jsonPath("$.[*].removed").value(hasItem(DEFAULT_REMOVED.toString())))
            .andExpect(jsonPath("$.[*].hardware").value(hasItem(DEFAULT_HARDWARE)))
            .andExpect(jsonPath("$.[*].software").value(hasItem(DEFAULT_SOFTWARE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllComputersWithEagerRelationshipsIsEnabled() throws Exception {
        when(computerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restComputerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(computerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllComputersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(computerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restComputerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(computerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getComputer() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get the computer
        restComputerMockMvc
            .perform(get(ENTITY_API_URL_ID, computer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(computer.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.introduced").value(DEFAULT_INTRODUCED.toString()))
            .andExpect(jsonPath("$.removed").value(DEFAULT_REMOVED.toString()))
            .andExpect(jsonPath("$.hardware").value(DEFAULT_HARDWARE))
            .andExpect(jsonPath("$.software").value(DEFAULT_SOFTWARE));
    }

    @Test
    @Transactional
    void getComputersByIdFiltering() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        Long id = computer.getId();

        defaultComputerShouldBeFound("id.equals=" + id);
        defaultComputerShouldNotBeFound("id.notEquals=" + id);

        defaultComputerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultComputerShouldNotBeFound("id.greaterThan=" + id);

        defaultComputerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultComputerShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllComputersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where name equals to DEFAULT_NAME
        defaultComputerShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the computerList where name equals to UPDATED_NAME
        defaultComputerShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllComputersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where name in DEFAULT_NAME or UPDATED_NAME
        defaultComputerShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the computerList where name equals to UPDATED_NAME
        defaultComputerShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllComputersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where name is not null
        defaultComputerShouldBeFound("name.specified=true");

        // Get all the computerList where name is null
        defaultComputerShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllComputersByNameContainsSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where name contains DEFAULT_NAME
        defaultComputerShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the computerList where name contains UPDATED_NAME
        defaultComputerShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllComputersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where name does not contain DEFAULT_NAME
        defaultComputerShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the computerList where name does not contain UPDATED_NAME
        defaultComputerShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllComputersByIntroducedIsEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where introduced equals to DEFAULT_INTRODUCED
        defaultComputerShouldBeFound("introduced.equals=" + DEFAULT_INTRODUCED);

        // Get all the computerList where introduced equals to UPDATED_INTRODUCED
        defaultComputerShouldNotBeFound("introduced.equals=" + UPDATED_INTRODUCED);
    }

    @Test
    @Transactional
    void getAllComputersByIntroducedIsInShouldWork() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where introduced in DEFAULT_INTRODUCED or UPDATED_INTRODUCED
        defaultComputerShouldBeFound("introduced.in=" + DEFAULT_INTRODUCED + "," + UPDATED_INTRODUCED);

        // Get all the computerList where introduced equals to UPDATED_INTRODUCED
        defaultComputerShouldNotBeFound("introduced.in=" + UPDATED_INTRODUCED);
    }

    @Test
    @Transactional
    void getAllComputersByIntroducedIsNullOrNotNull() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where introduced is not null
        defaultComputerShouldBeFound("introduced.specified=true");

        // Get all the computerList where introduced is null
        defaultComputerShouldNotBeFound("introduced.specified=false");
    }

    @Test
    @Transactional
    void getAllComputersByIntroducedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where introduced is greater than or equal to DEFAULT_INTRODUCED
        defaultComputerShouldBeFound("introduced.greaterThanOrEqual=" + DEFAULT_INTRODUCED);

        // Get all the computerList where introduced is greater than or equal to UPDATED_INTRODUCED
        defaultComputerShouldNotBeFound("introduced.greaterThanOrEqual=" + UPDATED_INTRODUCED);
    }

    @Test
    @Transactional
    void getAllComputersByIntroducedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where introduced is less than or equal to DEFAULT_INTRODUCED
        defaultComputerShouldBeFound("introduced.lessThanOrEqual=" + DEFAULT_INTRODUCED);

        // Get all the computerList where introduced is less than or equal to SMALLER_INTRODUCED
        defaultComputerShouldNotBeFound("introduced.lessThanOrEqual=" + SMALLER_INTRODUCED);
    }

    @Test
    @Transactional
    void getAllComputersByIntroducedIsLessThanSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where introduced is less than DEFAULT_INTRODUCED
        defaultComputerShouldNotBeFound("introduced.lessThan=" + DEFAULT_INTRODUCED);

        // Get all the computerList where introduced is less than UPDATED_INTRODUCED
        defaultComputerShouldBeFound("introduced.lessThan=" + UPDATED_INTRODUCED);
    }

    @Test
    @Transactional
    void getAllComputersByIntroducedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where introduced is greater than DEFAULT_INTRODUCED
        defaultComputerShouldNotBeFound("introduced.greaterThan=" + DEFAULT_INTRODUCED);

        // Get all the computerList where introduced is greater than SMALLER_INTRODUCED
        defaultComputerShouldBeFound("introduced.greaterThan=" + SMALLER_INTRODUCED);
    }

    @Test
    @Transactional
    void getAllComputersByRemovedIsEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where removed equals to DEFAULT_REMOVED
        defaultComputerShouldBeFound("removed.equals=" + DEFAULT_REMOVED);

        // Get all the computerList where removed equals to UPDATED_REMOVED
        defaultComputerShouldNotBeFound("removed.equals=" + UPDATED_REMOVED);
    }

    @Test
    @Transactional
    void getAllComputersByRemovedIsInShouldWork() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where removed in DEFAULT_REMOVED or UPDATED_REMOVED
        defaultComputerShouldBeFound("removed.in=" + DEFAULT_REMOVED + "," + UPDATED_REMOVED);

        // Get all the computerList where removed equals to UPDATED_REMOVED
        defaultComputerShouldNotBeFound("removed.in=" + UPDATED_REMOVED);
    }

    @Test
    @Transactional
    void getAllComputersByRemovedIsNullOrNotNull() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where removed is not null
        defaultComputerShouldBeFound("removed.specified=true");

        // Get all the computerList where removed is null
        defaultComputerShouldNotBeFound("removed.specified=false");
    }

    @Test
    @Transactional
    void getAllComputersByRemovedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where removed is greater than or equal to DEFAULT_REMOVED
        defaultComputerShouldBeFound("removed.greaterThanOrEqual=" + DEFAULT_REMOVED);

        // Get all the computerList where removed is greater than or equal to UPDATED_REMOVED
        defaultComputerShouldNotBeFound("removed.greaterThanOrEqual=" + UPDATED_REMOVED);
    }

    @Test
    @Transactional
    void getAllComputersByRemovedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where removed is less than or equal to DEFAULT_REMOVED
        defaultComputerShouldBeFound("removed.lessThanOrEqual=" + DEFAULT_REMOVED);

        // Get all the computerList where removed is less than or equal to SMALLER_REMOVED
        defaultComputerShouldNotBeFound("removed.lessThanOrEqual=" + SMALLER_REMOVED);
    }

    @Test
    @Transactional
    void getAllComputersByRemovedIsLessThanSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where removed is less than DEFAULT_REMOVED
        defaultComputerShouldNotBeFound("removed.lessThan=" + DEFAULT_REMOVED);

        // Get all the computerList where removed is less than UPDATED_REMOVED
        defaultComputerShouldBeFound("removed.lessThan=" + UPDATED_REMOVED);
    }

    @Test
    @Transactional
    void getAllComputersByRemovedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where removed is greater than DEFAULT_REMOVED
        defaultComputerShouldNotBeFound("removed.greaterThan=" + DEFAULT_REMOVED);

        // Get all the computerList where removed is greater than SMALLER_REMOVED
        defaultComputerShouldBeFound("removed.greaterThan=" + SMALLER_REMOVED);
    }

    @Test
    @Transactional
    void getAllComputersByHardwareIsEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where hardware equals to DEFAULT_HARDWARE
        defaultComputerShouldBeFound("hardware.equals=" + DEFAULT_HARDWARE);

        // Get all the computerList where hardware equals to UPDATED_HARDWARE
        defaultComputerShouldNotBeFound("hardware.equals=" + UPDATED_HARDWARE);
    }

    @Test
    @Transactional
    void getAllComputersByHardwareIsInShouldWork() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where hardware in DEFAULT_HARDWARE or UPDATED_HARDWARE
        defaultComputerShouldBeFound("hardware.in=" + DEFAULT_HARDWARE + "," + UPDATED_HARDWARE);

        // Get all the computerList where hardware equals to UPDATED_HARDWARE
        defaultComputerShouldNotBeFound("hardware.in=" + UPDATED_HARDWARE);
    }

    @Test
    @Transactional
    void getAllComputersByHardwareIsNullOrNotNull() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where hardware is not null
        defaultComputerShouldBeFound("hardware.specified=true");

        // Get all the computerList where hardware is null
        defaultComputerShouldNotBeFound("hardware.specified=false");
    }

    @Test
    @Transactional
    void getAllComputersByHardwareIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where hardware is greater than or equal to DEFAULT_HARDWARE
        defaultComputerShouldBeFound("hardware.greaterThanOrEqual=" + DEFAULT_HARDWARE);

        // Get all the computerList where hardware is greater than or equal to (DEFAULT_HARDWARE + 1)
        defaultComputerShouldNotBeFound("hardware.greaterThanOrEqual=" + (DEFAULT_HARDWARE + 1));
    }

    @Test
    @Transactional
    void getAllComputersByHardwareIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where hardware is less than or equal to DEFAULT_HARDWARE
        defaultComputerShouldBeFound("hardware.lessThanOrEqual=" + DEFAULT_HARDWARE);

        // Get all the computerList where hardware is less than or equal to SMALLER_HARDWARE
        defaultComputerShouldNotBeFound("hardware.lessThanOrEqual=" + SMALLER_HARDWARE);
    }

    @Test
    @Transactional
    void getAllComputersByHardwareIsLessThanSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where hardware is less than DEFAULT_HARDWARE
        defaultComputerShouldNotBeFound("hardware.lessThan=" + DEFAULT_HARDWARE);

        // Get all the computerList where hardware is less than (DEFAULT_HARDWARE + 1)
        defaultComputerShouldBeFound("hardware.lessThan=" + (DEFAULT_HARDWARE + 1));
    }

    @Test
    @Transactional
    void getAllComputersByHardwareIsGreaterThanSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where hardware is greater than DEFAULT_HARDWARE
        defaultComputerShouldNotBeFound("hardware.greaterThan=" + DEFAULT_HARDWARE);

        // Get all the computerList where hardware is greater than SMALLER_HARDWARE
        defaultComputerShouldBeFound("hardware.greaterThan=" + SMALLER_HARDWARE);
    }

    @Test
    @Transactional
    void getAllComputersBySoftwareIsEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where software equals to DEFAULT_SOFTWARE
        defaultComputerShouldBeFound("software.equals=" + DEFAULT_SOFTWARE);

        // Get all the computerList where software equals to UPDATED_SOFTWARE
        defaultComputerShouldNotBeFound("software.equals=" + UPDATED_SOFTWARE);
    }

    @Test
    @Transactional
    void getAllComputersBySoftwareIsInShouldWork() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where software in DEFAULT_SOFTWARE or UPDATED_SOFTWARE
        defaultComputerShouldBeFound("software.in=" + DEFAULT_SOFTWARE + "," + UPDATED_SOFTWARE);

        // Get all the computerList where software equals to UPDATED_SOFTWARE
        defaultComputerShouldNotBeFound("software.in=" + UPDATED_SOFTWARE);
    }

    @Test
    @Transactional
    void getAllComputersBySoftwareIsNullOrNotNull() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where software is not null
        defaultComputerShouldBeFound("software.specified=true");

        // Get all the computerList where software is null
        defaultComputerShouldNotBeFound("software.specified=false");
    }

    @Test
    @Transactional
    void getAllComputersBySoftwareIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where software is greater than or equal to DEFAULT_SOFTWARE
        defaultComputerShouldBeFound("software.greaterThanOrEqual=" + DEFAULT_SOFTWARE);

        // Get all the computerList where software is greater than or equal to (DEFAULT_SOFTWARE + 1)
        defaultComputerShouldNotBeFound("software.greaterThanOrEqual=" + (DEFAULT_SOFTWARE + 1));
    }

    @Test
    @Transactional
    void getAllComputersBySoftwareIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where software is less than or equal to DEFAULT_SOFTWARE
        defaultComputerShouldBeFound("software.lessThanOrEqual=" + DEFAULT_SOFTWARE);

        // Get all the computerList where software is less than or equal to SMALLER_SOFTWARE
        defaultComputerShouldNotBeFound("software.lessThanOrEqual=" + SMALLER_SOFTWARE);
    }

    @Test
    @Transactional
    void getAllComputersBySoftwareIsLessThanSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where software is less than DEFAULT_SOFTWARE
        defaultComputerShouldNotBeFound("software.lessThan=" + DEFAULT_SOFTWARE);

        // Get all the computerList where software is less than (DEFAULT_SOFTWARE + 1)
        defaultComputerShouldBeFound("software.lessThan=" + (DEFAULT_SOFTWARE + 1));
    }

    @Test
    @Transactional
    void getAllComputersBySoftwareIsGreaterThanSomething() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        // Get all the computerList where software is greater than DEFAULT_SOFTWARE
        defaultComputerShouldNotBeFound("software.greaterThan=" + DEFAULT_SOFTWARE);

        // Get all the computerList where software is greater than SMALLER_SOFTWARE
        defaultComputerShouldBeFound("software.greaterThan=" + SMALLER_SOFTWARE);
    }

    @Test
    @Transactional
    void getAllComputersByCompanyIsEqualToSomething() throws Exception {
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            computerRepository.saveAndFlush(computer);
            company = CompanyResourceIT.createEntity(em);
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(company);
        em.flush();
        computer.setCompany(company);
        computerRepository.saveAndFlush(computer);
        Long companyId = company.getId();
        // Get all the computerList where company equals to companyId
        defaultComputerShouldBeFound("companyId.equals=" + companyId);

        // Get all the computerList where company equals to (companyId + 1)
        defaultComputerShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultComputerShouldBeFound(String filter) throws Exception {
        restComputerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(computer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].introduced").value(hasItem(DEFAULT_INTRODUCED.toString())))
            .andExpect(jsonPath("$.[*].removed").value(hasItem(DEFAULT_REMOVED.toString())))
            .andExpect(jsonPath("$.[*].hardware").value(hasItem(DEFAULT_HARDWARE)))
            .andExpect(jsonPath("$.[*].software").value(hasItem(DEFAULT_SOFTWARE)));

        // Check, that the count call also returns 1
        restComputerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultComputerShouldNotBeFound(String filter) throws Exception {
        restComputerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restComputerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingComputer() throws Exception {
        // Get the computer
        restComputerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingComputer() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        int databaseSizeBeforeUpdate = computerRepository.findAll().size();

        // Update the computer
        Computer updatedComputer = computerRepository.findById(computer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedComputer are not directly saved in db
        em.detach(updatedComputer);
        updatedComputer
            .name(UPDATED_NAME)
            .introduced(UPDATED_INTRODUCED)
            .removed(UPDATED_REMOVED)
            .hardware(UPDATED_HARDWARE)
            .software(UPDATED_SOFTWARE);
        ComputerDTO computerDTO = computerMapper.toDto(updatedComputer);

        restComputerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, computerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(computerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeUpdate);
        Computer testComputer = computerList.get(computerList.size() - 1);
        assertThat(testComputer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testComputer.getIntroduced()).isEqualTo(UPDATED_INTRODUCED);
        assertThat(testComputer.getRemoved()).isEqualTo(UPDATED_REMOVED);
        assertThat(testComputer.getHardware()).isEqualTo(UPDATED_HARDWARE);
        assertThat(testComputer.getSoftware()).isEqualTo(UPDATED_SOFTWARE);
    }

    @Test
    @Transactional
    void putNonExistingComputer() throws Exception {
        int databaseSizeBeforeUpdate = computerRepository.findAll().size();
        computer.setId(longCount.incrementAndGet());

        // Create the Computer
        ComputerDTO computerDTO = computerMapper.toDto(computer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, computerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(computerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComputer() throws Exception {
        int databaseSizeBeforeUpdate = computerRepository.findAll().size();
        computer.setId(longCount.incrementAndGet());

        // Create the Computer
        ComputerDTO computerDTO = computerMapper.toDto(computer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(computerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComputer() throws Exception {
        int databaseSizeBeforeUpdate = computerRepository.findAll().size();
        computer.setId(longCount.incrementAndGet());

        // Create the Computer
        ComputerDTO computerDTO = computerMapper.toDto(computer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(computerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateComputerWithPatch() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        int databaseSizeBeforeUpdate = computerRepository.findAll().size();

        // Update the computer using partial update
        Computer partialUpdatedComputer = new Computer();
        partialUpdatedComputer.setId(computer.getId());

        partialUpdatedComputer
            .name(UPDATED_NAME)
            .introduced(UPDATED_INTRODUCED)
            .removed(UPDATED_REMOVED)
            .hardware(UPDATED_HARDWARE)
            .software(UPDATED_SOFTWARE);

        restComputerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComputer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComputer))
            )
            .andExpect(status().isOk());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeUpdate);
        Computer testComputer = computerList.get(computerList.size() - 1);
        assertThat(testComputer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testComputer.getIntroduced()).isEqualTo(UPDATED_INTRODUCED);
        assertThat(testComputer.getRemoved()).isEqualTo(UPDATED_REMOVED);
        assertThat(testComputer.getHardware()).isEqualTo(UPDATED_HARDWARE);
        assertThat(testComputer.getSoftware()).isEqualTo(UPDATED_SOFTWARE);
    }

    @Test
    @Transactional
    void fullUpdateComputerWithPatch() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        int databaseSizeBeforeUpdate = computerRepository.findAll().size();

        // Update the computer using partial update
        Computer partialUpdatedComputer = new Computer();
        partialUpdatedComputer.setId(computer.getId());

        partialUpdatedComputer
            .name(UPDATED_NAME)
            .introduced(UPDATED_INTRODUCED)
            .removed(UPDATED_REMOVED)
            .hardware(UPDATED_HARDWARE)
            .software(UPDATED_SOFTWARE);

        restComputerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComputer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComputer))
            )
            .andExpect(status().isOk());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeUpdate);
        Computer testComputer = computerList.get(computerList.size() - 1);
        assertThat(testComputer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testComputer.getIntroduced()).isEqualTo(UPDATED_INTRODUCED);
        assertThat(testComputer.getRemoved()).isEqualTo(UPDATED_REMOVED);
        assertThat(testComputer.getHardware()).isEqualTo(UPDATED_HARDWARE);
        assertThat(testComputer.getSoftware()).isEqualTo(UPDATED_SOFTWARE);
    }

    @Test
    @Transactional
    void patchNonExistingComputer() throws Exception {
        int databaseSizeBeforeUpdate = computerRepository.findAll().size();
        computer.setId(longCount.incrementAndGet());

        // Create the Computer
        ComputerDTO computerDTO = computerMapper.toDto(computer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, computerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(computerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComputer() throws Exception {
        int databaseSizeBeforeUpdate = computerRepository.findAll().size();
        computer.setId(longCount.incrementAndGet());

        // Create the Computer
        ComputerDTO computerDTO = computerMapper.toDto(computer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(computerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComputer() throws Exception {
        int databaseSizeBeforeUpdate = computerRepository.findAll().size();
        computer.setId(longCount.incrementAndGet());

        // Create the Computer
        ComputerDTO computerDTO = computerMapper.toDto(computer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(computerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Computer in the database
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComputer() throws Exception {
        // Initialize the database
        computerRepository.saveAndFlush(computer);

        int databaseSizeBeforeDelete = computerRepository.findAll().size();

        // Delete the computer
        restComputerMockMvc
            .perform(delete(ENTITY_API_URL_ID, computer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Computer> computerList = computerRepository.findAll();
        assertThat(computerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
