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
    private String nome;

    @Size(min = 10, message = "Descrição precisa ter no mínimo 10 caracteres")
    @NotBlank(message = "Campo requerido")
    private String descricao;

    @NotNull(message = "Campo requerido")
    @Positive(message = "O preço precisa ser positivo")
    private Double preco;

    private String imgUrl;

    // criando a relação para mostrar as categorias dos produtos
    // categoria não deve ser vazia
    @NotEmpty(message = "Deve ter pelo menos uma categoria")
    private List<CategoriaDTO> categorias = new ArrayList<>();

    public ProdutoDTO() {
    }

    public ProdutoDTO(Long id, String nome, String descricao, Double preco, String imgUrl) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.imgUrl = imgUrl;
    }

    public ProdutoDTO(Product entity) {
        id = entity.getId();
        nome = entity.getName();
        descricao = entity.getDescricao();
        preco = entity.getPreco();
        imgUrl = entity.getImgUrl();

        // inserindo categorias nos produtos
        for (Categoria categoria : entity.getCategorias()) {
            categorias.add(new CategoriaDTO(categoria));
        }
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public List<CategoriaDTO> getCategorias() {
        return categorias;
    }
}
