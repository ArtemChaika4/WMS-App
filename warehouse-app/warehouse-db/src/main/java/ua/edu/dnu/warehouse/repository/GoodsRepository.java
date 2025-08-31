package ua.edu.dnu.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.edu.dnu.warehouse.model.Customer;
import ua.edu.dnu.warehouse.model.Goods;

import java.util.List;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long>, JpaSpecificationExecutor<Goods> {
    @Query("SELECT AVG(g.price) FROM Goods g")
    Double getAveragePrice();

    @Query("SELECT g.status, COUNT(g) FROM Goods g GROUP BY g.status")
    List<Object[]> getGoodsCountByStatus();

    @Query("SELECT g.type, COUNT(g) FROM Goods g GROUP BY g.type")
    List<Object[]> getGoodsCountByType();

}
