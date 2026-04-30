package com.weg.aprendendoJPQL.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.weg.aprendendoJPQL.dto.EstatisticaEditoraDTO;
import com.weg.aprendendoJPQL.model.Livro;
import com.weg.aprendendoJPQL.projection.LivroMinimoProjection;

public interface LivroRepository extends JpaRepository<Livro, Long>{

    //Query JPA personalizadas
    Optional<Livro> findByTitulo(String titulo);
    List<Livro> findByCategoriaAndPrecoLessThan(String categoria, BigDecimal preco);
    List<Livro> findByPrecoBetween(BigDecimal precoMin, BigDecimal precoMax);
    List<Livro> findByCategoriaIn(List<String> categorias);
    List<Livro> findByIsbnIsNull();
    List<Livro> findByEditoraOrderByTituloAsc(String editora);
    Optional<Integer> countByAutorNacionalidade(String nacionalidade);

    //Query JPQL

    @Query("""
            SELECT l.titulo 
            FROM Livro l 
            WHERE l.categoria = :categoria       
            """)
    List<String> buscarTitulosPorCategoria(@Param("categoria") String categoria); 

    @Query("""
            SELECT l
            FROM Livro l
            JOIN l.autores a
            WHERE a.nome = :nome
            """)
    List<Livro> buscarLivrosPorAutor(@Param("nome") String nome);

    @Query("""
            SELECT l
            FROM Livro l
            JOIN FETCH l.autores
            """)
    List<Livro> buscarLivrosComAutores();

    @Query("""
            SELECT AVG(l.preco)
            FROM Livro l
            WHERE l.editora = :editora
            """)
    Double mediaPrecoPorEditora(@Param("editora") String editora);

    @Query("""
            SELECT l
            FROM Livro l
            WHERE l.preco > 
            (SELECT AVG(l2.preco) 
             FROM Livro l2)
            """)
    List<Livro> livrosPrecoAcimaMedia();

    // Native Query

    @Query(value = """
            SELECT titulo,
                   isbn,
                   preco,
                   data_publicacao,
                   categoria
            FROM Livro 
            WHERE YEAR(data_publicacao) = :ano
            """, nativeQuery = true)
    List<Livro> buscarPorAno(@Param("ano") int ano);

    @Query(value = """
            SELECT l.titulo,
                   l.isbn,
                   l.preco,
                   l.data_publicacao,
                   l.categoria
            FROM Livro l 
            JOIN livro_autores la ON l.id = la.livro_id
            JOIN autor a ON la.autor_id = a.id
            WHERE a.nacionalidade = 'Brasileira'
            """, nativeQuery = true)
    List<Livro> livrosAutoresBrasileiros();

    @Query(value = """
            SELECT titulo,
                   isbn,
                   preco,
                   data_publicacao,
                   categoria
            FROM Livro 
            WHERE LOWER(categoria) = LOWER(:categoria)
            """, nativeQuery = true)
    List<Livro> buscarCategoriaIgnoreCase(@Param("categoria") String categoria);

    //Projections e DTOs

    List<LivroMinimoProjection> findByCategoria(String categoria);

    @Query("""
            SELECT new package com.weg.aprendendoJPQL.dto(
                l.editora,
                COUNT(1)
            )
            FROM Livro l
            GROUP BY l.editora
            """)
    List<EstatisticaEditoraDTO> contarLivrosPorEditora();

    @Query(value = """
            SELECT titulo AS titulo,
                   preco AS preco
            FROM Livro
            """, nativeQuery = true)
    List<LivroMinimoProjection> buscarResumo();

    <T> List<T> findByCategoria(String categoria, Class<T> type);
}
