package cn.gq.tmall.controller;


import cn.gq.tmall.pojo.Product;
import cn.gq.tmall.pojo.ProductImage;
import cn.gq.tmall.service.ProductImageService;
import cn.gq.tmall.service.ProductService;
import cn.gq.tmall.util.ImageUtil;
import cn.gq.tmall.util.UploadedImageFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("")
public class ProductImageController {
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ProductService productService;

    @RequestMapping("admin_productImage_delete")
    public String delete(int id,HttpSession session){
        ProductImage productImage = productImageService.get(id);

        String fileName = productImage.getId() + ".jpg";
        String imageFolder;
        String imageFolder_small = null;
        String imageFolder_middle = null;

        if(productImageService.type_single.equals(productImage.getType())){
            imageFolder = session.getServletContext().getRealPath("img/productSingle");
            imageFolder_small = session.getServletContext().getRealPath("img/productSingle_small");
            imageFolder_middle = session.getServletContext().getRealPath("img/productSingle_middle");

            File imageFile = new File(imageFolder,fileName);
            File f_small = new File(imageFolder_small,fileName);
            File f_middle = new File(imageFolder_middle,fileName);
            imageFile.delete();
            f_small.delete();
            f_middle.delete();
        }else {
            imageFolder = session.getServletContext().getRealPath("img/productDetail");
            File imageFile = new File(imageFolder,fileName);
            imageFile.delete();
        }

        productImageService.delete(id);

        return "redirect:admin_productImage_list?pid=" + productImage.getPid();
    }

    @RequestMapping("admin_productImage_add")
    public String add(ProductImage productImage, HttpSession session, UploadedImageFile uploadedImageFile){
        productImageService.add(productImage);
        String fileName = productImage.getId() + ".jpg";
        String imageFolder;
        String imageFolder_small = null;
        String imageFolder_middle = null;
        if(productImageService.type_single.equals(productImage.getType())){
            imageFolder = session.getServletContext().getRealPath("img/productSingle");
            imageFolder_middle = session.getServletContext().getRealPath("img/productSingle_middle");
            imageFolder_small = session.getServletContext().getRealPath("img/productSingle_small");
        }else{
            imageFolder = session.getServletContext().getRealPath("img/productDetail");
        }

        File file = new File(imageFolder,fileName);
        file.getParentFile().mkdirs();

        try {
            uploadedImageFile.getImage().transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img,"jpg",file);

            if(productImageService.type_single.equals(productImage.getType())){
                File f_small = new File(imageFolder_small,fileName);
                File f_middle = new File(imageFolder_middle,fileName);

                ImageUtil.resizeImage(file,56,56,f_small);
                ImageUtil.resizeImage(file,217,190,f_middle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:admin_productImage_list?pid=" + productImage.getPid();
    }

    @RequestMapping("admin_productImage_list")
    public String list(int pid,Model model){
        Product product = productService.get(pid);
        List<ProductImage> productImagesSingle = productImageService.list(pid,ProductImageService.type_single);
        List<ProductImage> productImagesDetail = productImageService.list(pid,ProductImageService.type_detail);

        model.addAttribute("p",product);
        model.addAttribute("pisSingle",productImagesSingle);
        model.addAttribute("pisDetail",productImagesDetail);

        return "admin/listProductImage";
    }
}
