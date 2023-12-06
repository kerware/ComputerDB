package org.dev4test.computerdb.repository;

import java.util.List;
import java.util.Optional;
import org.dev4test.computerdb.domain.Computer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Computer entity.
 */
@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long>, JpaSpecificationExecutor<Computer> {
    default Optional<Computer> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Computer> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Computer> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select computer from Computer computer left join fetch computer.company",
        countQuery = "select count(computer) from Computer computer"
    )
    Page<Computer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select computer from Computer computer left join fetch computer.company")
    List<Computer> findAllWithToOneRelationships();

    @Query("select computer from Computer computer left join fetch computer.company where computer.id =:id")
    Optional<Computer> findOneWithToOneRelationships(@Param("id") Long id);
}
