package cn.gq.tmall.service;

import cn.gq.tmall.pojo.User;

import java.util.List;


public interface UserService {
    void add(User user);
    void delete(int id);
    void update(User user);
    User get(int id);
    List list();
    boolean isExist(String name);

    User get(String name,String password);
}
