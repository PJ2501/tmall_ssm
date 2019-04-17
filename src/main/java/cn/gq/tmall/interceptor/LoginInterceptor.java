package cn.gq.tmall.interceptor;

import cn.gq.tmall.pojo.User;
import cn.gq.tmall.service.CategoryService;
import cn.gq.tmall.service.OrderItemService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String contextPath = session.getServletContext().getContextPath();
        String[] noNeedAuthPage = new String[]{
                "home",
                "checkLogin",
                "register",
                "loginAjax",
                "login",
                "product",
                "category",
                "search"
        };

        String uri = request.getRequestURI();
        uri = StringUtils.remove(uri, contextPath);
        if (uri.startsWith("/fore")) {
            String method = StringUtils.substringAfterLast(uri, "/fore");
            if (!Arrays.asList(noNeedAuthPage).contains(method)) {
                User user = (User) session.getAttribute("user");
                if (user == null) {
                    response.sendRedirect("loginPage");
                    return false;//从当前拦截器往回执行所有拦截器的afterCompletion(),然后退出拦截器链。
                }
            }
        }
        return true;//执行下一个拦截器,直到所有的拦截器都执行完毕再执行被拦截的Controller,然后进入拦截器链, 从最后一个拦截器往回执行所有的postHandle(),接着再从最后一个拦截器往回执行所有的afterCompletion()
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
    }

    public void afterCompletion(HttpServletResponse response, HttpServletRequest request, Object handler, Exception ex) throws Exception {

    }
}
