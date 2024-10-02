package com.util.board.service;

import com.util.board.entity.Board;
import com.util.board.entity.BoardComment;
import com.util.board.repository.BoardCommentRepository;
import com.util.board.repository.BoardRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.EmployeeFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
public class BoardCommentService {
    private final BoardService boardService;
    private final EmployeeFeignClient employeeFeignClient;
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;

    public BoardCommentService(BoardService boardService,
                               EmployeeFeignClient employeeFeignClient,
                               BoardRepository boardRepository,
                               BoardCommentRepository boardCommentRepository) {
        this.boardService = boardService;
        this.employeeFeignClient = employeeFeignClient;
        this.boardRepository = boardRepository;
        this.boardCommentRepository = boardCommentRepository;
    }

    public BoardComment createBoardComment(BoardComment boardComment, long employeeId) throws IllegalArgumentException {
//        Map<String, Object> employee = employeeFeignClient.getEmployeeById(employeeId);

//        if (employee.containsKey("employeeId")) {
//            Long fetchEmployeeId = (Long) employee.get("employeeId");
//            String employeeName = (String) employee.get("username");

            Board board = boardService.findVerifiedBoard(boardComment.getBoard().getBoardId());

            boardComment.setBoard(board);
//            boardComment.setEmployeeId(fetchEmployeeId);
//            boardComment.setEmployeeName(employeeName);

            if (boardComment.getParentComment() != null) {
                BoardComment parentComment = boardCommentRepository.findById(boardComment.getParentComment().getBoardCommentId())
                        .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));

                if (parentComment.getBoard().getBoardId() != board.getBoardId()) {
                    throw new IllegalArgumentException("부모 댓글이 현재 게시물에 속하지 않습니다.");
                }
                boardComment.setParentComment(parentComment);
            } else {
                boardComment.setParentComment(null);
            }

            boardComment.setEmployeeId(employeeId); // feign 사용시 지워야됨
            board.setCommentCount(board.getCommentCount() + 1);
            boardRepository.save(board);
            return boardCommentRepository.save(boardComment);
//        }
//        else {
//            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
//        }
    }

    public BoardComment updateBoardComment(long boardCommentId, BoardComment boardComment, long employeeId) {
        BoardComment findBoardComment = findVerifiedBoardComment(boardCommentId);

        if (findBoardComment.getEmployeeId() != employeeId) {
            throw new BusinessLogicException(ExceptionCode.BOARD_COMMENT_NOT_FOUND);
        }

        Optional.ofNullable(boardComment.getContent())
                .ifPresent(content -> findBoardComment.setContent(content));
        return boardCommentRepository.save(findBoardComment);
    }

    @Transactional(readOnly = true)
    public List<BoardComment> findBoardCommentsByBoardId(long boardId) {
        Board board = boardService.findVerifiedBoard(boardId);
        return boardCommentRepository.findByBoard(board);
    }

    public void deleteBoardComment(long boardCommentId, long employeeId) {
        BoardComment findBoardComment = findVerifiedBoardComment(boardCommentId);

        if (findBoardComment.getEmployeeId() != employeeId) {
            throw new BusinessLogicException(ExceptionCode.BOARD_COMMENT_NOT_FOUND);
        }

        Board board = findBoardComment.getBoard();
        int commentCountToRemove = countComments(findBoardComment, new HashSet<>());
        boardCommentRepository.delete(findBoardComment);
        int newCommentCount = board.getCommentCount() - commentCountToRemove;
        board.setCommentCount(newCommentCount);
        boardRepository.save(board);
    }

    public BoardComment findVerifiedBoardComment(long boardCommentId) {
        Optional<BoardComment> optionalBoardComment = boardCommentRepository.findById(boardCommentId);
        BoardComment findBoardComment = optionalBoardComment.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.BOARD_COMMENT_NOT_FOUND));
        return findBoardComment;
    }

    private int countComments(BoardComment boardComment, Set<Long> visited) {
        if (visited.contains(boardComment.getBoardCommentId())) {
            return 0;
        }
        visited.add(boardComment.getBoardCommentId());

        int count = 1;
        for (BoardComment reply : boardComment.getReplies()) {
            count += countComments(reply, visited);
        }
        return count;
    }
}
