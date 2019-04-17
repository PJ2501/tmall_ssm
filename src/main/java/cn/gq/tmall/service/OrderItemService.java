package cn.gq.tmall.service;

import cn.gq.tmall.pojo.Order;
import cn.gq.tmall.pojo.OrderItem;

import java.util.List;

public interface OrderItemService {
    void add(OrderItem orderItem);
    void delete(int id);
    void update(OrderItem orderItem);
    OrderItem get(int id);
    List list();

    void fill(List<Order> orders);
    void fill(Order order);
    int getSaleCount(int pid);

    List<OrderItem> listByUser(int uid);
}
