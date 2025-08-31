package ua.edu.dnu.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ua.edu.dnu.warehouse.model.EventLog;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long>, JpaSpecificationExecutor<EventLog> {
}
