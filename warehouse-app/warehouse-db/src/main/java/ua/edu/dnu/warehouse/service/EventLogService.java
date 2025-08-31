package ua.edu.dnu.warehouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.dnu.warehouse.user.SessionStorage;
import ua.edu.dnu.warehouse.model.*;
import ua.edu.dnu.warehouse.repository.EventLogRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventLogService extends AbstractSearchService<EventLog> {
    private final EventLogRepository eventLogRepository;
    private final SessionStorage sessionStorage;


    public EventLogService(EventLogRepository eventLogRepository, SessionStorage sessionStorage) {
        super(eventLogRepository);
        this.eventLogRepository = eventLogRepository;
        this.sessionStorage = sessionStorage;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createEventLog(Action action, Category category, String result){
        Employee employee = sessionStorage.getUser();
        LocalDateTime timestamp = LocalDateTime.now();
        EventLog eventLog = new EventLog(result, category, action, timestamp, employee);
        System.out.println(eventLog);
        eventLogRepository.save(eventLog);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createEventLog(Action action, Category category, String result, Employee employee){
        LocalDateTime timestamp = LocalDateTime.now();
        EventLog eventLog = new EventLog(result, category, action, timestamp, employee);
        System.out.println(eventLog);
        eventLogRepository.save(eventLog);
    }

    public List<EventLog> getAllEventLogs(){
        return eventLogRepository.findAll();
    }
}
