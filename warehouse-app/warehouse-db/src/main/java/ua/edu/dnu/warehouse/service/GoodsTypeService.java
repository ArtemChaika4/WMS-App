package ua.edu.dnu.warehouse.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.dnu.warehouse.aspect.LogAction;
import ua.edu.dnu.warehouse.aspect.LogCategory;
import ua.edu.dnu.warehouse.model.Action;
import ua.edu.dnu.warehouse.model.Category;
import ua.edu.dnu.warehouse.model.GoodsType;
import ua.edu.dnu.warehouse.repository.GoodsTypeRepository;

import java.util.List;
import java.util.Optional;

@LogCategory(Category.GOODS_TYPES)
@Transactional
@Service
public class GoodsTypeService extends AbstractSearchService<GoodsType>{
    private final GoodsTypeRepository goodsTypeRepository;

    public GoodsTypeService(GoodsTypeRepository goodsTypeRepository) {
        super(goodsTypeRepository);
        this.goodsTypeRepository = goodsTypeRepository;
    }

    public List<GoodsType> getAllGoodsTypes() {
        return goodsTypeRepository.findAll();
    }

    public Optional<GoodsType> getGoodsTypeByById(Long id) {
        return goodsTypeRepository.findById(id);
    }

    public GoodsType getByName(String name){
        return goodsTypeRepository.findByName(name);
    }
    @LogAction(value = Action.CREATE, message = "Додано новий вид товару")
    public GoodsType createGoodsType(GoodsType type) {
        return goodsTypeRepository.save(type);
    }

    private GoodsType findGoodsTypeByIdOrElseThrow(Long id){
        return goodsTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не знайдено вид товару"));
    }

    @LogAction(value = Action.UPDATE, message = "Оновлено дані про вид товару")
    public GoodsType updateGoodsType(GoodsType type) {
        GoodsType saved = findGoodsTypeByIdOrElseThrow(type.getId());
        saved.setName(type.getName());
        saved.setDescription(type.getDescription());
        return goodsTypeRepository.save(saved);
    }

    @LogAction(value = Action.DELETE, message = "Видалено вид товару")
    public GoodsType deleteGoodsType(Long id) {
        GoodsType type = findGoodsTypeByIdOrElseThrow(id);
        goodsTypeRepository.deleteById(id);
        return type;
    }

    public long getGoodsTypeCount(){
        return goodsTypeRepository.count();
    }
}
