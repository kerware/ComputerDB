package org.dev4test.computerdb.service;

import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.dev4test.computerdb.domain.*; // for static metamodels
import org.dev4test.computerdb.domain.Computer;
import org.dev4test.computerdb.repository.ComputerRepository;
import org.dev4test.computerdb.service.criteria.ComputerCriteria;
import org.dev4test.computerdb.service.dto.ComputerDTO;
import org.dev4test.computerdb.service.mapper.ComputerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Computer} entities in the database.
 * The main input is a {@link ComputerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ComputerDTO} or a {@link Page} of {@link ComputerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ComputerQueryService extends QueryService<Computer> {

    private final Logger log = LoggerFactory.getLogger(ComputerQueryService.class);

    private final ComputerRepository computerRepository;

    private final ComputerMapper computerMapper;

    public ComputerQueryService(ComputerRepository computerRepository, ComputerMapper computerMapper) {
        this.computerRepository = computerRepository;
        this.computerMapper = computerMapper;
    }

    /**
     * Return a {@link List} of {@link ComputerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ComputerDTO> findByCriteria(ComputerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Computer> specification = createSpecification(criteria);
        return computerMapper.toDto(computerRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ComputerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ComputerDTO> findByCriteria(ComputerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Computer> specification = createSpecification(criteria);
        return computerRepository.findAll(specification, page).map(computerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ComputerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Computer> specification = createSpecification(criteria);
        return computerRepository.count(specification);
    }

    /**
     * Function to convert {@link ComputerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Computer> createSpecification(ComputerCriteria criteria) {
        Specification<Computer> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Computer_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Computer_.name));
            }
            if (criteria.getIntroduced() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIntroduced(), Computer_.introduced));
            }
            if (criteria.getRemoved() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRemoved(), Computer_.removed));
            }
            if (criteria.getHardware() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHardware(), Computer_.hardware));
            }
            if (criteria.getSoftware() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSoftware(), Computer_.software));
            }
            if (criteria.getCompanyId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCompanyId(), root -> root.join(Computer_.company, JoinType.LEFT).get(Company_.id))
                    );
            }
        }
        return specification;
    }
}
