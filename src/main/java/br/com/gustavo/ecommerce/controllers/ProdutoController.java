package br.com.gustavo.ecommerce.controllers;


import br.com.gustavo.ecommerce.dto.ProdutoDTO;
import br.com.gustavo.ecommerce.entities.Produto;
import br.com.gustavo.ecommerce.repositories.ProdutoRepository;
import br.com.gustavo.ecommerce.services.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping(value = "/{id}")
    public ProdutoDTO findById(@PathVariable Long id) {
        return produtoService.findById(id);
    }

}
