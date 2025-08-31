package ua.edu.dnu.warehouse.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SearchService<T> {
    Page<T> search(Specification<T> specification, Pageable pageable);
}
