package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.Categoria;
import br.com.gustavo.ecommerce.entities.Product;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

public class ProdutoDTO {

    private Long id;

    @Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres")
    @NotBlank(message = "Campo requerido")
    private String name;

    @Size(min = 10, message = "Descrição precisa ter no mínimo 10 caracteres")
    @NotBlank(message = "Campo requerido")
    private String description;

    @NotNull(message = "Campo requerido")
    @Positive(message = "O preço precisa ser positivo")
    private Double price;

    private String imgUrl;

    // criando a relação para mostrar as categorias dos produtos
    // categoria não deve ser vazia
    @NotEmpty(message = "Deve ter pelo menos uma categoria")
    private List<CategoriaDTO> categories = new ArrayList<>();

    public ProdutoDTO() {
    }

    public ProdutoDTO(Long id, String name, String description, Double price, String imgUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public ProdutoDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();

        // inserindo categorias nos produtos
        for (Categoria categoria : entity.getCategories()) {
            categories.add(new CategoriaDTO(categoria));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public List<CategoriaDTO> getCategories() {
        return categories;
    }
}
