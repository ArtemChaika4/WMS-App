package ua.edu.dnu.warehouse.filter;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build();
}
