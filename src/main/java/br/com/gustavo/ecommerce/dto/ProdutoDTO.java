package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.Categoria;
import br.com.gustavo.ecommerce.entities.Produto;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

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

    @Positive(message = "O preço precisa ser positivo")
    private Double preco;

    private String imgUrl;

    // criando a relação para mostrar as categorias dos produtos
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

    public ProdutoDTO(Produto entity) {
        id = entity.getId();
        nome = entity.getNome();
        descricao = entity.getDescricao();
        preco = entity.getPreco();
        imgUrl = entity.getImgUrl();
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
