package com.util.board.repository;

import com.util.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT p FROM Board p WHERE (p.title LIKE %:keyword%)")
    Page<Board> searchByTitle(Pageable pageable, @Param("keyword") String keyword);

    @Query("SELECT p FROM Board p WHERE (p.content LIKE %:keyword%)")
    Page<Board> searchByContent(Pageable pageable, @Param("keyword") String keyword);

    @Query("SELECT p FROM Board p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Board> searchByTitleOrContent(Pageable pageable, @Param("keyword") String keyword);

    @Query("SELECT p FROM Board p WHERE (p.employeeName LIKE %:keyword%)")
    Page<Board> searchByEmployeeName(Pageable pageable, @Param("keyword") String keyword);
}
