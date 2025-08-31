package ua.edu.dnu.warehouse.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.dnu.warehouse.aspect.LogAction;
import ua.edu.dnu.warehouse.aspect.LogCategory;
import ua.edu.dnu.warehouse.model.Customer;
import org.springframework.stereotype.Service;
import ua.edu.dnu.warehouse.model.Action;
import ua.edu.dnu.warehouse.model.Category;
import ua.edu.dnu.warehouse.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

@LogCategory(Category.CUSTOMERS)
@Transactional
@Service
public class CustomerService extends AbstractSearchService<Customer>{
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        super(customerRepository);
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }
    public Optional<Customer> getCustomerByPhone(String phone){
        return customerRepository.findByPhone(phone);
    }
    public Optional<Customer> getCustomerById(Long id){
        return customerRepository.findById(id);
    }

    @LogAction(value = Action.CREATE, message = "Створено нового замовника")
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @LogAction(value = Action.UPDATE, message = "Оновлено дані замовника")
    public Customer updateCustomer(Customer customer){
        Customer saved = findCustomerByIdOrElseThrow(customer.getId());
        saved.setName(customer.getName());
        saved.setSurname(customer.getSurname());
        saved.setPatronymic(customer.getPatronymic());
        saved.setPhone(customer.getPhone());
        saved.setAddress(customer.getAddress());
        return customerRepository.save(saved);
    }

    private Customer findCustomerByIdOrElseThrow(Long id){
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Замовника не знайдено"));
    }

    @LogAction(value = Action.DELETE, message = "Видалено замовника")
    public Customer deleteCustomer(Long id) {
        Customer customer = findCustomerByIdOrElseThrow(id);
        customerRepository.deleteById(id);
        return customer;
    }

    public long getCustomersCount(){
        return customerRepository.count();
    }
}
