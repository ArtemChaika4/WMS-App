package ua.edu.dnu.warehouse.user;

import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.model.Employee;

@Component
public class SessionStorage {
    private Employee loggedInUser;

    public void setUser(Employee user) {
        this.loggedInUser = user;
    }

    public Employee getUser() {
        return loggedInUser;
    }

    public void clear() {
        this.loggedInUser = null;
    }
}
