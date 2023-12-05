package com.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Computer.
 */
@Entity
@Table(name = "computer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Computer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "introduced")
    private LocalDate introduced;

    @Column(name = "removed")
    private LocalDate removed;

    @Min(value = 0)
    @Max(value = 40)
    @Column(name = "hardware")
    private Integer hardware;

    @Min(value = 0)
    @Max(value = 60)
    @Column(name = "software")
    private Integer software;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Computer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Computer name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getIntroduced() {
        return this.introduced;
    }

    public Computer introduced(LocalDate introduced) {
        this.setIntroduced(introduced);
        return this;
    }

    public void setIntroduced(LocalDate introduced) {
        this.introduced = introduced;
    }

    public LocalDate getRemoved() {
        return this.removed;
    }

    public Computer removed(LocalDate removed) {
        this.setRemoved(removed);
        return this;
    }

    public void setRemoved(LocalDate removed) {
        this.removed = removed;
    }

    public Integer getHardware() {
        return this.hardware;
    }

    public Computer hardware(Integer hardware) {
        this.setHardware(hardware);
        return this;
    }

    public void setHardware(Integer hardware) {
        this.hardware = hardware;
    }

    public Integer getSoftware() {
        return this.software;
    }

    public Computer software(Integer software) {
        this.setSoftware(software);
        return this;
    }

    public void setSoftware(Integer software) {
        this.software = software;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Computer company(Company company) {
        this.setCompany(company);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Computer)) {
            return false;
        }
        return getId() != null && getId().equals(((Computer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Computer{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", introduced='" + getIntroduced() + "'" +
            ", removed='" + getRemoved() + "'" +
            ", hardware=" + getHardware() +
            ", software=" + getSoftware() +
            "}";
    }
}
