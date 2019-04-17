package cn.gq.tmall.controller;


import cn.gq.tmall.pojo.Order;
import cn.gq.tmall.service.OrderItemService;
import cn.gq.tmall.service.OrderService;
import cn.gq.tmall.util.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    @RequestMapping("admin_order_delivery")
    public String delivery(Order order){
        order.setDeliveryDate(new Date());
        order.setStatus(OrderService.waitConfirm);
        orderService.update(order);

        return "redirect:admin_order_list";
    }

    @RequestMapping("admin_order_list")
    public String list(Model model, Page page){
        PageHelper.offsetPage(page.getStart(),page.getCount());

        List<Order> orders = orderService.list();

        int total = (int) new PageInfo<>(orders).getTotal();
        page.setTotal(total);

        orderItemService.fill(orders);

        model.addAttribute("os",orders);
        model.addAttribute("page",page);

        return "admin/listOrder";
    }
}
