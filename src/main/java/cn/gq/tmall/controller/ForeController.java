package cn.gq.tmall.controller;


import cn.gq.tmall.pojo.*;
import cn.gq.tmall.service.*;
import com.github.pagehelper.PageHelper;
import comparator.*;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("")
public class ForeController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @RequestMapping("foredoreview")
    public String doreview(Model model,HttpSession session,@RequestParam("oid") int oid,@RequestParam("pid") int pid,String content){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setPid(pid);
        review.setCreateDate(new Date());
        review.setUid(user.getId());
        reviewService.add(review);

        return "redirect:forereview?oid=" + oid + "&showonly=true";
    }

    @RequestMapping("forereview")
    public String review(Model model,int oid){
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        Product product = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(product.getId());
        productService.setSaleAndReviewNumber(product);
        model.addAttribute("p",product);
        model.addAttribute("o",order);
        model.addAttribute("reviews",reviews);
        return "fore/review";
    }

    @RequestMapping("foredeleteOrder")
    @ResponseBody
    public String deleteOrder(Model model,int oid){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.delete);
        orderService.update(order);
        return "success";
    }

    @RequestMapping("foreorderConfirmed")
    public String orderConfirmed(Model model,int oid){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);
        return "fore/orderConfirmed";
    }

    @RequestMapping("foreconfirmPay")
    public String confirmPay(Model model,int oid){
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        model.addAttribute("o",order);
        return "fore/confirmPay";
    }

    @RequestMapping("forebought")
    public String bought(Model model,HttpSession session){
        User user = (User) session.getAttribute("user");
        List<Order> os = orderService.list(user.getId(),OrderService.delete);

        orderItemService.fill(os);

        model.addAttribute("os",os);
        return "fore/bought";
    }

    @RequestMapping("forepayed")
    public String payed(int oid,float total,Model model){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        model.addAttribute("o",order);
        return "fore/payed";
    }

    @RequestMapping("forecreateOrder")
    public String createOrder(Model model,HttpSession session,Order order){
        User user = (User) session.getAttribute("user");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUid(user.getId());
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");

        float total = orderService.add(order,ois);
        return "redirect:forealipay?oid=" + order.getId() + "&total=" + total;
    }

    @RequestMapping("foredeleteOrderItem")
    @ResponseBody
    public String deleteOrderItem(Model model,HttpSession session,int oiid){
        User user = (User) session.getAttribute("user");
        if(user == null)
            return "fail";
        orderItemService.delete(oiid);
        return "success";
    }

    @RequestMapping("forechangeOrderItem")
    @ResponseBody
    public String changeOrderItem(int num,int pid,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        if(user == null)
            return "fail";

        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for(OrderItem oi : ois){
            if(oi.getProduct().getId().intValue() == pid){
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return "success";
    }

    @RequestMapping("forecart")
    public String cart(Model model,HttpSession session){
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        model.addAttribute("ois",ois);
        return "fore/cart";
    }

    @RequestMapping("foreaddCart")
    @ResponseBody
    public String addCart(int pid,int num,HttpSession session,Model model){
        Product p = productService.get(pid);
        User user = (User) session.getAttribute("user");
        boolean found = false;

        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for(OrderItem oi : ois){
            if(oi.getProduct().getId().intValue() == p.getId().intValue()){
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                break;
            }
        }
        if(!found){
            OrderItem oi = new OrderItem();
            oi.setNumber(num);
            oi.setUid(user.getId());
            oi.setPid(pid);
            orderItemService.add(oi);
        }
        return "success";
    }

    @RequestMapping("forebuy")
    public String buy(Model model,HttpSession session,String[] oiid){
        List<OrderItem> ois = new ArrayList<>();
        float total = 0;

        for(String strid : oiid){
            int id = Integer.parseInt(strid);
            OrderItem oi = orderItemService.get(id);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            ois.add(oi);
        }

        session.setAttribute("ois",ois);
        model.addAttribute("total",total);
        return "fore/buy";
    }

    @RequestMapping("forebuyone")
    public String buyone(int pid,int num,HttpSession session){
        Product product = productService.get(pid);
        int oiid = 0;

        User user = (User)session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for(OrderItem oi : ois){
            if(oi.getProduct().getId().intValue() == product.getId().intValue()){
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }
        if(!found) {
            OrderItem oi = new OrderItem();
            oi.setUid(user.getId());
            oi.setNumber(num);
            oi.setPid(pid);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return "redirect:forebuy?oiid=" + oiid;
    }

    @RequestMapping("foresearch")
    public String search(String keyword,Model model){

        PageHelper.offsetPage(0,20);
        List<Product> products = productService.search(keyword);
        productService.setSaleAndReviewNumber(products);
        model.addAttribute("ps",products);
        return "fore/searchResult";
    }

    @RequestMapping("forecategory")
    public String category(Model model,int cid,String sort){
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());

        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        model.addAttribute("c",category);
        return "fore/category";
    }

    @RequestMapping("forecheckLogin")
    @ResponseBody
    public String checkLogin(HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user != null)
            return "success";
        return "fail";
    }

    @RequestMapping("foreloginAjax")
    @ResponseBody
    public String loginAjax(@RequestParam("name") String name,@RequestParam("password") String password,HttpSession session){
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name,password);

        if(user == null)
            return "fail";
        session.setAttribute("user",user);
        return "success";
    }

    @RequestMapping("foreproduct")
    public String product(int pid,Model model){
        Product product = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.list(product.getId(),productImageService.type_single);
        List<ProductImage> productDetailImages = productImageService.list(product.getId(),productImageService.type_detail);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        List<PropertyValue> propertyValues = propertyValueService.list(product.getId());
        List<Review> reviews = reviewService.list(product.getId());
        productService.setSaleAndReviewNumber(product);
        model.addAttribute("reviews",reviews);
        model.addAttribute("p",product);
        model.addAttribute("pvs",propertyValues);
        return "fore/product";
    }

    @RequestMapping("forelogout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:forehome";
    }

    @RequestMapping("forelogin")
    public String login(Model model, HttpSession session, @RequestParam("name") String name, @RequestParam("password") String password) {
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name,password);

        if(user == null){
            model.addAttribute("msg","账号密码错误");
            return "fore/login";
        }

        session.setAttribute("user",user);
        return "redirect:forehome";
    }

    @RequestMapping("foreregister")
    public String register(Model model, User user) {
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if (exist) {
            String message = "用户名已被使用，请重新输入";
            model.addAttribute("msg", message);
            model.addAttribute("user", null);
            return "fore/register";
        }
        userService.add(user);

        return "redirect:registerSuccessPage";
    }

    @RequestMapping("forehome")
    public String home(Model model) {
        List<Category> categories = categoryService.list();
        productService.fill(categories);
        productService.fillByRow(categories);
        model.addAttribute("cs", categories);
        return "fore/home";
    }
}
