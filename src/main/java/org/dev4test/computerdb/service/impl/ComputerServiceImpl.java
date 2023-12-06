package org.dev4test.computerdb.service.impl;

import java.util.Optional;
import org.dev4test.computerdb.domain.Computer;
import org.dev4test.computerdb.repository.ComputerRepository;
import org.dev4test.computerdb.service.ComputerService;
import org.dev4test.computerdb.service.dto.ComputerDTO;
import org.dev4test.computerdb.service.mapper.ComputerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.dev4test.computerdb.domain.Computer}.
 */
@Service
@Transactional
public class ComputerServiceImpl implements ComputerService {

    private final Logger log = LoggerFactory.getLogger(ComputerServiceImpl.class);

    private final ComputerRepository computerRepository;

    private final ComputerMapper computerMapper;

    public ComputerServiceImpl(ComputerRepository computerRepository, ComputerMapper computerMapper) {
        this.computerRepository = computerRepository;
        this.computerMapper = computerMapper;
    }

    @Override
    public ComputerDTO save(ComputerDTO computerDTO) {
        log.debug("Request to save Computer : {}", computerDTO);
        Computer computer = computerMapper.toEntity(computerDTO);
        computer = computerRepository.save(computer);
        return computerMapper.toDto(computer);
    }

    @Override
    public ComputerDTO update(ComputerDTO computerDTO) {
        log.debug("Request to update Computer : {}", computerDTO);
        Computer computer = computerMapper.toEntity(computerDTO);
        computer = computerRepository.save(computer);
        return computerMapper.toDto(computer);
    }

    @Override
    public Optional<ComputerDTO> partialUpdate(ComputerDTO computerDTO) {
        log.debug("Request to partially update Computer : {}", computerDTO);

        return computerRepository
            .findById(computerDTO.getId())
            .map(existingComputer -> {
                computerMapper.partialUpdate(existingComputer, computerDTO);

                return existingComputer;
            })
            .map(computerRepository::save)
            .map(computerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComputerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Computers");
        return computerRepository.findAll(pageable).map(computerMapper::toDto);
    }

    public Page<ComputerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return computerRepository.findAllWithEagerRelationships(pageable).map(computerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComputerDTO> findOne(Long id) {
        log.debug("Request to get Computer : {}", id);
        return computerRepository.findOneWithEagerRelationships(id).map(computerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Computer : {}", id);
        computerRepository.deleteById(id);
    }
}
