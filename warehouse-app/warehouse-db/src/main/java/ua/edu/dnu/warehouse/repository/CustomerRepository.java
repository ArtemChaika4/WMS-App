package ua.edu.dnu.warehouse.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ua.edu.dnu.warehouse.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
