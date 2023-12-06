package org.dev4test.computerdb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.dev4test.computerdb.repository.ComputerRepository;
import org.dev4test.computerdb.service.ComputerQueryService;
import org.dev4test.computerdb.service.ComputerService;
import org.dev4test.computerdb.service.criteria.ComputerCriteria;
import org.dev4test.computerdb.service.dto.ComputerDTO;
import org.dev4test.computerdb.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.dev4test.computerdb.domain.Computer}.
 */
@RestController
@RequestMapping("/api/computers")
public class ComputerResource {

    private final Logger log = LoggerFactory.getLogger(ComputerResource.class);

    private static final String ENTITY_NAME = "computer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ComputerService computerService;

    private final ComputerRepository computerRepository;

    private final ComputerQueryService computerQueryService;

    public ComputerResource(
        ComputerService computerService,
        ComputerRepository computerRepository,
        ComputerQueryService computerQueryService
    ) {
        this.computerService = computerService;
        this.computerRepository = computerRepository;
        this.computerQueryService = computerQueryService;
    }

    /**
     * {@code POST  /computers} : Create a new computer.
     *
     * @param computerDTO the computerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new computerDTO, or with status {@code 400 (Bad Request)} if the computer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ComputerDTO> createComputer(@Valid @RequestBody ComputerDTO computerDTO) throws URISyntaxException {
        log.debug("REST request to save Computer : {}", computerDTO);
        if (computerDTO.getId() != null) {
            throw new BadRequestAlertException("A new computer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ComputerDTO result = computerService.save(computerDTO);
        return ResponseEntity
            .created(new URI("/api/computers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /computers/:id} : Updates an existing computer.
     *
     * @param id the id of the computerDTO to save.
     * @param computerDTO the computerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated computerDTO,
     * or with status {@code 400 (Bad Request)} if the computerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the computerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ComputerDTO> updateComputer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ComputerDTO computerDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Computer : {}, {}", id, computerDTO);
        if (computerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, computerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!computerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ComputerDTO result = computerService.update(computerDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, computerDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /computers/:id} : Partial updates given fields of an existing computer, field will ignore if it is null
     *
     * @param id the id of the computerDTO to save.
     * @param computerDTO the computerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated computerDTO,
     * or with status {@code 400 (Bad Request)} if the computerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the computerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the computerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ComputerDTO> partialUpdateComputer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ComputerDTO computerDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Computer partially : {}, {}", id, computerDTO);
        if (computerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, computerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!computerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ComputerDTO> result = computerService.partialUpdate(computerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, computerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /computers} : get all the computers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of computers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ComputerDTO>> getAllComputers(
        ComputerCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Computers by criteria: {}", criteria);

        Page<ComputerDTO> page = computerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /computers/count} : count all the computers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countComputers(ComputerCriteria criteria) {
        log.debug("REST request to count Computers by criteria: {}", criteria);
        return ResponseEntity.ok().body(computerQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /computers/:id} : get the "id" computer.
     *
     * @param id the id of the computerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the computerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComputerDTO> getComputer(@PathVariable Long id) {
        log.debug("REST request to get Computer : {}", id);
        Optional<ComputerDTO> computerDTO = computerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(computerDTO);
    }

    /**
     * {@code DELETE  /computers/:id} : delete the "id" computer.
     *
     * @param id the id of the computerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComputer(@PathVariable Long id) {
        log.debug("REST request to delete Computer : {}", id);
        computerService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
