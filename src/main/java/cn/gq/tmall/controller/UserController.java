package cn.gq.tmall.controller;

import cn.gq.tmall.pojo.User;
import cn.gq.tmall.service.UserService;
import cn.gq.tmall.util.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("admin_user_list")
    public String list(Model model, Page page){
        PageHelper.offsetPage(page.getStart(),page.getCount());

        List<User> userList = userService.list();

        int total = (int) new PageInfo<>(userList).getTotal();
        page.setTotal(total);

        model.addAttribute("us",userList);
        model.addAttribute("page",page);

        return "admin/listUser";
    }
}
