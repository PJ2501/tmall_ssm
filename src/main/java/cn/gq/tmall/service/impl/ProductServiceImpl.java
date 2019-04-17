package cn.gq.tmall.service.impl;

import cn.gq.tmall.mapper.ProductMapper;
import cn.gq.tmall.pojo.Category;
import cn.gq.tmall.pojo.Product;
import cn.gq.tmall.pojo.ProductExample;
import cn.gq.tmall.pojo.ProductImage;
import cn.gq.tmall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @Override
    public List<Product> search(String keyword) {
        ProductExample example = new ProductExample();
        example.createCriteria().andNameLike("%" + keyword + "%");
        example.setOrderByClause("id desc");
        List result = productMapper.selectByExample(example);
        setFirstProductImage(result);
        setCategory(result);
        return result;
    }

    @Override
    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product.getId());
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product.getId());
        product.setReviewCount(reviewCount);
    }

    @Override
    public void setSaleAndReviewNumber(List<Product> products) {
        for(Product product : products){
            setSaleAndReviewNumber(product);
        }
    }

    @Override
    public void fill(List<Category> categories) {
        for(Category category : categories){
            fill(category);
        }
    }

    @Override
    public void fill(Category category) {
        List<Product> products = list(category.getId());
        category.setProducts(products);
    }

    @Override
    public void fillByRow(List<Category> categories) {
        int productNumberEachRow = 8;
        for(Category category : categories){
            List<Product> products = category.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for(int i = 0;i < products.size();i += productNumberEachRow){
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsOfEachRow = products.subList(i,size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    @Override
    public void setFirstProductImage(Product p) {
        List<ProductImage> productImages = productImageService.list(p.getId(),productImageService.type_single);
        if(productImages != null && !productImages.isEmpty()){
            ProductImage productImage = productImages.get(0);
            p.setFirstProductImage(productImage);
        }
    }

    public void setFirstProductImage(List<Product> products){
        for(Product product : products){
            setFirstProductImage(product);
        }
    }

    @Override
    public void add(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void delete(int id) {
        productMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Product product) {
        productMapper.updateByPrimaryKeySelective(product);
    }

    @Override
    public Product get(int id) {
        Product product = productMapper.selectByPrimaryKey(id);
        setFirstProductImage(product);
        setCategory(product);
        return product;
    }

    public void setCategory(Product product){
        int cid = product.getCid();
        Category category= categoryService.get(cid);
        product.setCategory(category);
    }

    public void setCategory(List<Product> products){
        for(Product product : products){
            setCategory(product);
        }
    }

    @Override
    public List list(int cid) {
        ProductExample example = new ProductExample();
        example.createCriteria().andCidEqualTo(cid);
        example.setOrderByClause("id desc");
        List result = productMapper.selectByExample(example);
        setCategory(result);
        setFirstProductImage(result);

        return result;
    }
}
