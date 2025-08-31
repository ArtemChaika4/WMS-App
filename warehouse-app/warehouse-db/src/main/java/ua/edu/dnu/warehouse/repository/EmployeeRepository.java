package ua.edu.dnu.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.edu.dnu.warehouse.model.Customer;
import ua.edu.dnu.warehouse.model.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByEmail(String email);
    @Query("SELECT e.post, COUNT(e) FROM Employee e GROUP BY e.post")
    List<Object[]> getEmployeeCountByPosition();
}