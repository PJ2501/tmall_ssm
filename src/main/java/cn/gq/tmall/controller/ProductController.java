package cn.gq.tmall.controller;

import cn.gq.tmall.pojo.Category;
import cn.gq.tmall.pojo.Product;
import cn.gq.tmall.service.CategoryService;
import cn.gq.tmall.service.ProductService;
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
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;

    @RequestMapping("admin_product_delete")
    public String delete(int id){
        Product product = productService.get(id);
        productService.delete(id);
        return "redirect:admin_product_list?cid=" + product.getCid();
    }

    @RequestMapping("admin_product_update")
    public String update(Product product){
        productService.update(product);
        return "redirect:admin_product_list?cid=" + product.getCid();
    }

    @RequestMapping("admin_product_edit")
    public String edit(Model model,int id){
        Product product = productService.get(id);
        model.addAttribute("p",product);
        return "admin/editProduct";
    }

    @RequestMapping("admin_product_add")
    public String add(Model model,Product product){
        product.setCreateData(new Date());
        productService.add(product);
        return "redirect:admin_product_list?cid=" + product.getCid();
    }

    @RequestMapping("admin_product_list")
    public String list(Model model, int cid, Page page){
        Category category = categoryService.get(cid);

        PageHelper.offsetPage(page.getStart(),page.getCount());
        List<Product> ps = productService.list(cid);

        int total = (int)new PageInfo<>(ps).getTotal();
        page.setTotal(total);
        page.setParam("&cid=" + category.getId());

        model.addAttribute("ps",ps);
        model.addAttribute("c",category);
        model.addAttribute("page",page);

        return "admin/listProduct";
    }

}
