package ua.edu.dnu.warehouse.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.dnu.warehouse.aspect.LogAction;
import ua.edu.dnu.warehouse.aspect.LogCategory;
import ua.edu.dnu.warehouse.model.*;
import ua.edu.dnu.warehouse.repository.GoodsRepository;
import ua.edu.dnu.warehouse.repository.GoodsTypeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@LogCategory(Category.GOODS)
@Transactional
@Service
public class GoodsService extends AbstractSearchService<Goods>{
    private final GoodsRepository goodsRepository;
    private final GoodsTypeRepository goodsTypeRepository;

    public GoodsService(GoodsRepository goodsRepository, GoodsTypeRepository goodsTypeRepository) {
        super(goodsRepository);
        this.goodsRepository = goodsRepository;
        this.goodsTypeRepository = goodsTypeRepository;
    }

    public List<Goods> getAllGoods() {
        return goodsRepository.findAll();
    }

    public Optional<Goods > getGoodsById(Long id) {
        return goodsRepository.findById(id);
    }

    @LogAction(value = Action.CREATE, message = "Додано новий товар")
    public Goods createGoods(Goods goods) {
        checkGoodsType(goods.getType());
        return goodsRepository.save(goods);
    }

    private void checkGoodsType(GoodsType type){
        if(type != null && !goodsTypeRepository.existsById(type.getId())){
            throw new EntityNotFoundException("Не знайдено вид товару");
        }
    }

    @LogAction(value = Action.UPDATE, message = "Оновлено дані про товар")
    public Goods updateGoods(Goods goods) {
        Goods saved = goodsRepository.findById(goods.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не знайдено товар"));
        checkGoodsType(goods.getType());
        saved.setName(goods.getName());
        saved.setDescription(goods.getDescription());
        saved.setAmount(goods.getAmount());
        saved.setPrice(goods.getPrice());
        saved.setStatus(goods.getStatus());
        saved.setType(goods.getType());
        return goodsRepository.save(saved);
    }

    @LogAction(value = Action.DELETE, message = "Видалено товар")
    public Goods deleteGoods(Long id) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не знайдено товар"));
        goodsRepository.deleteById(id);
        return goods;
    }

    public long getGoodsCount(){
        return goodsRepository.count();
    }

    public Double getGoodsAveragePrice() {
        return Optional.ofNullable(goodsRepository.getAveragePrice()).orElse(0.0);
    }

    public Map<GoodsStatus, Long> getGoodsCountByStatus() {
        List<Object[]> results = goodsRepository.getGoodsCountByStatus();
        return results.stream()
                .collect(Collectors.toMap(row -> (GoodsStatus) row[0], row -> (Long) row[1]));
    }

    public Map<GoodsType, Long> getGoodsCountGroupedByType() {
        List<Object[]> results = goodsRepository.getGoodsCountByType();
        return results.stream()
                .collect(Collectors.toMap(row -> (GoodsType) row[0], row -> (Long) row[1]));
    }

}
