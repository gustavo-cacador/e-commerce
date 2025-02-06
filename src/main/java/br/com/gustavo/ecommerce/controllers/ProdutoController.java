package br.com.gustavo.ecommerce.controllers;


import br.com.gustavo.ecommerce.dto.ProdutoDTO;
import br.com.gustavo.ecommerce.services.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping(value = "/{id}")
    public ProdutoDTO findById(@PathVariable Long id) {
        return produtoService.findById(id);
    }

    // Para buscar todos os produtos da lista
    /*
    @GetMapping
    public List<ProdutoDTO> buscarTodos() {
        return produtoService.buscarTodos();
    }
     */

    @GetMapping
    public Page<ProdutoDTO> findAll(Pageable pageable) {
        return produtoService.findAll(pageable);
    }

    @PostMapping
    public ProdutoDTO insert(@RequestBody ProdutoDTO dto) {
        return produtoService.insert(dto);
    }

}
