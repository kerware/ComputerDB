package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.myapp.IntegrationTest;
import com.myapp.domain.Computer;
import com.myapp.repository.ComputerRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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

    private static final LocalDate DEFAULT_REMOVED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REMOVED = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_HARDWARE = 0;
    private static final Integer UPDATED_HARDWARE = 1;

    private static final Integer DEFAULT_SOFTWARE = 0;
    private static final Integer UPDATED_SOFTWARE = 1;

    private static final String ENTITY_API_URL = "/api/computers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ComputerRepository computerRepository;

    @Mock
    private ComputerRepository computerRepositoryMock;

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
        restComputerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(computer)))
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

        int databaseSizeBeforeCreate = computerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restComputerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(computer)))
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

        restComputerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(computer)))
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
        when(computerRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restComputerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(computerRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllComputersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(computerRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

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

        restComputerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedComputer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedComputer))
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

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, computer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(computer))
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

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(computer))
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

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(computer)))
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

        partialUpdatedComputer.name(UPDATED_NAME).software(UPDATED_SOFTWARE);

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
        assertThat(testComputer.getIntroduced()).isEqualTo(DEFAULT_INTRODUCED);
        assertThat(testComputer.getRemoved()).isEqualTo(DEFAULT_REMOVED);
        assertThat(testComputer.getHardware()).isEqualTo(DEFAULT_HARDWARE);
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

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, computer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(computer))
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

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(computer))
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

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComputerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(computer)))
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
