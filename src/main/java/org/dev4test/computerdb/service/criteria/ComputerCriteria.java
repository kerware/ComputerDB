package org.dev4test.computerdb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.dev4test.computerdb.domain.Computer} entity. This class is used
 * in {@link org.dev4test.computerdb.web.rest.ComputerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /computers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComputerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LocalDateFilter introduced;

    private LocalDateFilter removed;

    private IntegerFilter hardware;

    private IntegerFilter software;

    private LongFilter companyId;

    private Boolean distinct;

    public ComputerCriteria() {}

    public ComputerCriteria(ComputerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.introduced = other.introduced == null ? null : other.introduced.copy();
        this.removed = other.removed == null ? null : other.removed.copy();
        this.hardware = other.hardware == null ? null : other.hardware.copy();
        this.software = other.software == null ? null : other.software.copy();
        this.companyId = other.companyId == null ? null : other.companyId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ComputerCriteria copy() {
        return new ComputerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LocalDateFilter getIntroduced() {
        return introduced;
    }

    public LocalDateFilter introduced() {
        if (introduced == null) {
            introduced = new LocalDateFilter();
        }
        return introduced;
    }

    public void setIntroduced(LocalDateFilter introduced) {
        this.introduced = introduced;
    }

    public LocalDateFilter getRemoved() {
        return removed;
    }

    public LocalDateFilter removed() {
        if (removed == null) {
            removed = new LocalDateFilter();
        }
        return removed;
    }

    public void setRemoved(LocalDateFilter removed) {
        this.removed = removed;
    }

    public IntegerFilter getHardware() {
        return hardware;
    }

    public IntegerFilter hardware() {
        if (hardware == null) {
            hardware = new IntegerFilter();
        }
        return hardware;
    }

    public void setHardware(IntegerFilter hardware) {
        this.hardware = hardware;
    }

    public IntegerFilter getSoftware() {
        return software;
    }

    public IntegerFilter software() {
        if (software == null) {
            software = new IntegerFilter();
        }
        return software;
    }

    public void setSoftware(IntegerFilter software) {
        this.software = software;
    }

    public LongFilter getCompanyId() {
        return companyId;
    }

    public LongFilter companyId() {
        if (companyId == null) {
            companyId = new LongFilter();
        }
        return companyId;
    }

    public void setCompanyId(LongFilter companyId) {
        this.companyId = companyId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ComputerCriteria that = (ComputerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(introduced, that.introduced) &&
            Objects.equals(removed, that.removed) &&
            Objects.equals(hardware, that.hardware) &&
            Objects.equals(software, that.software) &&
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, introduced, removed, hardware, software, companyId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ComputerCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (introduced != null ? "introduced=" + introduced + ", " : "") +
            (removed != null ? "removed=" + removed + ", " : "") +
            (hardware != null ? "hardware=" + hardware + ", " : "") +
            (software != null ? "software=" + software + ", " : "") +
            (companyId != null ? "companyId=" + companyId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
