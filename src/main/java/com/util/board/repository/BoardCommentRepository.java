package com.util.board.repository;

import com.util.board.entity.Board;
import com.util.board.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    List<BoardComment> findByBoard(Board board);
}
