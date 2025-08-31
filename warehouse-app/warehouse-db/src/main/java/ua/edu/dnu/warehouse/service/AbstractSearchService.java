package ua.edu.dnu.warehouse.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public class AbstractSearchService<T> implements SearchService<T> {
    private final JpaSpecificationExecutor<T> repository;

    protected AbstractSearchService(JpaSpecificationExecutor<T> repository) {
        this.repository = repository;
    }
    @Override
    public Page<T> search(Specification<T> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }
}
