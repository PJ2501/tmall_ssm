package cn.gq.tmall.service;

import cn.gq.tmall.pojo.Category;
import cn.gq.tmall.pojo.Product;

import java.util.List;

public interface ProductService {

    void add(Product product);
    void delete(int id);
    void update(Product product);
    Product get(int id);
    List list(int cid);
    void setFirstProductImage(Product p);

    void fill(List<Category> categories);

    void fill(Category category);

    void fillByRow(List<Category> categories);

    void setSaleAndReviewNumber(Product product);

    void setSaleAndReviewNumber(List<Product> products);

    List<Product> search(String keyword);
}
