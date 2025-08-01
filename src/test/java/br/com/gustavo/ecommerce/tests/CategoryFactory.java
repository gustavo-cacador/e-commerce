package br.com.gustavo.ecommerce.tests;

import br.com.gustavo.ecommerce.entities.Category;

public class CategoryFactory {

    public static Category createCategory() {
        return new Category(1L, "Games");
    }

    public static Category createCategory(Long id, String name) {
        return new Category(id, name);
    }
}
