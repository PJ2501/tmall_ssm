package cn.gq.tmall.service;

import cn.gq.tmall.pojo.Product;
import cn.gq.tmall.pojo.PropertyValue;

import java.util.List;

public interface PropertyValueService {
    void init(Product product);
    void update(PropertyValue propertyValue);

    PropertyValue get(int ptid,int pid);
    List<PropertyValue> list(int pid);
}
