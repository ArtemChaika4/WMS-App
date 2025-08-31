package ua.edu.dnu.warehouse.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.dnu.warehouse.aspect.LogAction;
import ua.edu.dnu.warehouse.aspect.LogCategory;
import ua.edu.dnu.warehouse.model.Action;
import ua.edu.dnu.warehouse.model.Category;
import ua.edu.dnu.warehouse.model.Employee;
import ua.edu.dnu.warehouse.model.Post;
import ua.edu.dnu.warehouse.repository.EmployeeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@LogCategory(Category.EMPLOYEES)
@Transactional
@Service
public class EmployeeService extends AbstractSearchService<Employee>{
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        super(employeeRepository);
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @LogAction(value = Action.CREATE, message = "Додано нового працівника")
    public Employee createEmployee(Employee employee) {
        String hashedPassword = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(hashedPassword);
        return employeeRepository.save(employee);
    }

    @LogAction(value = Action.LOGIN, message = "Здійснено вхід в систему")
    public Employee loginEmployee(String email, String rawPassword) {
        return employeeRepository.findByEmail(email)
                .filter(employee -> passwordEncoder.matches(rawPassword, employee.getPassword()))
                .orElse(null);
    }

    public boolean checkEmployeePassword(String email, String rawPassword) {
        return employeeRepository.findByEmail(email)
                .map(employee -> passwordEncoder.matches(rawPassword, employee.getPassword()))
                .orElse(false);
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    private Employee findEmployeeOrElseThrow(String email, String rawPassword){
        return employeeRepository.findByEmail(email)
                .filter(employee -> passwordEncoder.matches(rawPassword, employee.getPassword()))
                .orElseThrow(() -> new EntityNotFoundException("Не знайдено працівника"));
    }

    @LogAction(value = Action.UPDATE, message = "Змінено пароль працівника")
    public Employee changePassword(String email, String currentPassword, String newPassword) {
        Employee employee = findEmployeeOrElseThrow(email, currentPassword);
        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);
        return employee;
    }

    @LogAction(value = Action.UPDATE, message = "Скинуто пароль працівника")
    public void adminChangePassword(String email, String  newPassword){
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Не знайдено працівника"));
        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);
    }

    @LogAction(value = Action.UPDATE, message = "Оновлено дані працівника")
    public Employee updateEmployee(Employee employee) {
        Employee saved = employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не знайдено працівника"));
        saved.setName(employee.getName());
        saved.setSurname(employee.getSurname());
        saved.setPatronymic(employee.getPatronymic());
        saved.setEmail(employee.getEmail());
        saved.setPost(employee.getPost());
        return employeeRepository.save(saved);
    }

    @LogAction(value = Action.DELETE, message = "Видалено працівника")
    public Employee deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не знайдено працівника"));
        employeeRepository.deleteById(id);
        return employee;
    }

    public long getEmployeesCount(){
        return employeeRepository.count();
    }

    public Map<Post, Long> getEmployeesCountByPosition() {
        List<Object[]> results = employeeRepository.getEmployeeCountByPosition();
        return results.stream()
                .collect(Collectors.toMap(row -> (Post) row[0], row -> (Long) row[1]));
    }
}
