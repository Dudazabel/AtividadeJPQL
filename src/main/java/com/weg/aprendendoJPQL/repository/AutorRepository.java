package com.weg.aprendendoJPQL.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.aprendendoJPQL.model.Autor;

public interface AutorRepository extends JpaRepository<Autor, Long>{

    List<Autor> findByNomeContainingIgnoreCase(String nome);
}
