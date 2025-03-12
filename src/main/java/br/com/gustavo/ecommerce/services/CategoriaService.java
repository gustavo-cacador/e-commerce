package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.CategoriaDTO;
import br.com.gustavo.ecommerce.entities.Categoria;
import br.com.gustavo.ecommerce.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() {
        List<Categoria> result = categoriaRepository.findAll();
        return result.stream().map(x -> new CategoriaDTO(x)).toList();
    }
}
