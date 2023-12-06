package org.dev4test.computerdb.service;

import java.util.Optional;
import org.dev4test.computerdb.service.dto.ComputerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.dev4test.computerdb.domain.Computer}.
 */
public interface ComputerService {
    /**
     * Save a computer.
     *
     * @param computerDTO the entity to save.
     * @return the persisted entity.
     */
    ComputerDTO save(ComputerDTO computerDTO);

    /**
     * Updates a computer.
     *
     * @param computerDTO the entity to update.
     * @return the persisted entity.
     */
    ComputerDTO update(ComputerDTO computerDTO);

    /**
     * Partially updates a computer.
     *
     * @param computerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ComputerDTO> partialUpdate(ComputerDTO computerDTO);

    /**
     * Get all the computers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ComputerDTO> findAll(Pageable pageable);

    /**
     * Get all the computers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ComputerDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" computer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ComputerDTO> findOne(Long id);

    /**
     * Delete the "id" computer.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
