package cn.gq.tmall.controller;

import cn.gq.tmall.pojo.Product;
import cn.gq.tmall.pojo.PropertyValue;
import cn.gq.tmall.service.ProductService;
import cn.gq.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("")
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;

    @RequestMapping("admin_propertyValue_update")
    @ResponseBody
    public String update(PropertyValue propertyValue){
        propertyValueService.update(propertyValue);
        return "success";
    }

    @RequestMapping("admin_propertyValue_edit")
    public String edit(Model model,int pid){
        Product product = productService.get(pid);
        propertyValueService.init(product);
        List<PropertyValue> propertyValues = propertyValueService.list(product.getId());

        model.addAttribute("p",product);
        model.addAttribute("pvs",propertyValues);
        return "admin/editPropertyValue";
    }
}
