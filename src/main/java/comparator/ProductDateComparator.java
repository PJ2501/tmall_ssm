package comparator;

import cn.gq.tmall.pojo.Product;

import java.util.Comparator;

public class ProductDateComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o2.getCreateData().compareTo(o1.getCreateData());
    }
}
