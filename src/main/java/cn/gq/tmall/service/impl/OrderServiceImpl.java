package cn.gq.tmall.service.impl;

import cn.gq.tmall.mapper.OrderMapper;
import cn.gq.tmall.pojo.Order;
import cn.gq.tmall.pojo.OrderExample;
import cn.gq.tmall.pojo.OrderItem;
import cn.gq.tmall.pojo.User;
import cn.gq.tmall.service.OrderItemService;
import cn.gq.tmall.service.OrderService;
import cn.gq.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserService userService;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public List list(int uid, String excludedStatus) {
        OrderExample example = new OrderExample();
        example.createCriteria().andUidEqualTo(uid).andStatusNotEqualTo(excludedStatus);
        example.setOrderByClause("id desc");
        return orderMapper.selectByExample(example);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = "Exception")
    public float add(Order order, List<OrderItem> ois) {
        float total = 0;
        add(order);

        //模拟增加订单时发生异常
        if(false)
            throw new RuntimeException();

        for (OrderItem oi : ois){
            oi.setOid(order.getId());
            orderItemService.update(oi);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
        }

        return total;
    }

    @Override
    public void add(Order c) {
        orderMapper.insert(c);
    }

    @Override
    public void delete(int id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Order c) {
        orderMapper.updateByPrimaryKeySelective(c);
    }

    @Override
    public Order get(int id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List list() {
        OrderExample example = new OrderExample();
        example.setOrderByClause("id desc");
        List<Order> orders = orderMapper.selectByExample(example);
        setUser(orders);
        return orders;
    }

    public void setUser(List<Order> orders){
        for(Order order : orders){
            setUser(order);
        }
    }

    public void setUser(Order order){
        int uid = order.getUid();
        User user = userService.get(uid);
        order.setUser(user);
    }
}
