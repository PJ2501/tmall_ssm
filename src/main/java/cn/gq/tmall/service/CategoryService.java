package cn.gq.tmall.service;

import cn.gq.tmall.pojo.Category;
import cn.gq.tmall.util.Page;

import java.util.List;

public interface CategoryService {

    List<Category> list();

    void add(Category category);

    void delete(int id);

    Category get(int id);

    void update(Category category);

}
