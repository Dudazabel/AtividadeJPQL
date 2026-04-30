package com.weg.aprendendoJPQL.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.aprendendoJPQL.model.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long>{

    Optional<Livro> findByTitulo(String titulo);
    Optional<Livro>
}
