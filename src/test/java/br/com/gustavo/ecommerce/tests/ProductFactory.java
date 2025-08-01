package br.com.gustavo.ecommerce.tests;

import br.com.gustavo.ecommerce.entities.Category;
import br.com.gustavo.ecommerce.entities.Product;

public class ProductFactory {

    public static Product createProduct() {

        Category category = CategoryFactory.createCategory();
        Product product = new Product(1L, "Play5", "muito bom", 3999.0, "url.teste");
        product.getCategories().add(category);
        return product;
    }

    public static Product createProduct(String name) {

        Product product = createProduct();
        product.setName(name);
        return product;
    }
}
