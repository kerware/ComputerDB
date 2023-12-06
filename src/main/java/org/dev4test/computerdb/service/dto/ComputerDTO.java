package org.dev4test.computerdb.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link org.dev4test.computerdb.domain.Computer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComputerDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private LocalDate introduced;

    private LocalDate removed;

    @Min(value = 0)
    @Max(value = 40)
    private Integer hardware;

    @Min(value = 0)
    @Max(value = 60)
    private Integer software;

    private CompanyDTO company;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getIntroduced() {
        return introduced;
    }

    public void setIntroduced(LocalDate introduced) {
        this.introduced = introduced;
    }

    public LocalDate getRemoved() {
        return removed;
    }

    public void setRemoved(LocalDate removed) {
        this.removed = removed;
    }

    public Integer getHardware() {
        return hardware;
    }

    public void setHardware(Integer hardware) {
        this.hardware = hardware;
    }

    public Integer getSoftware() {
        return software;
    }

    public void setSoftware(Integer software) {
        this.software = software;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ComputerDTO)) {
            return false;
        }

        ComputerDTO computerDTO = (ComputerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, computerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ComputerDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", introduced='" + getIntroduced() + "'" +
            ", removed='" + getRemoved() + "'" +
            ", hardware=" + getHardware() +
            ", software=" + getSoftware() +
            ", company=" + getCompany() +
            "}";
    }
}
