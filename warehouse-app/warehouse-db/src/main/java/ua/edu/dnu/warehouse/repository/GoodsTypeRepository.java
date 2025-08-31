package ua.edu.dnu.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ua.edu.dnu.warehouse.model.Customer;
import ua.edu.dnu.warehouse.model.GoodsType;

@Repository
public interface GoodsTypeRepository extends JpaRepository<GoodsType, Long>, JpaSpecificationExecutor<GoodsType> {
    GoodsType findByName(String name);
}
