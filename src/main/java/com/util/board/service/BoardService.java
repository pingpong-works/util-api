package com.util.board.service;

import com.util.board.entity.Board;
import com.util.board.repository.BoardRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.EmployeeFeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final EmployeeFeignClient employeeFeignClient;

    public BoardService(BoardRepository boardRepository,
                        EmployeeFeignClient employeeFeignClient) {
        this.boardRepository = boardRepository;
        this.employeeFeignClient = employeeFeignClient;
    }

    public Board createBoard(Board board, long employeeId) throws IllegalArgumentException {
        // employee 호출할 경우 사용하는 코드
//        Map<String, Object> employee = employeeFeignClient.getEmployeeById(employeeId);
//
//        if (employee.containsKey("employeeId")) {
//            Long fetchEmployeeId = (Long) employee.get("employeeId");
//            String employeeName = (String) employee.get("username");
//
//            board.setEmployeeId(fetchEmployeeId);
//            board.setEmployeeName(employeeName);
//
//            return boardRepository.save(board);
//        }
//        else {
//            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
//        }

        // employee없이 사용하는 일반 코드, 현재 board에는 username이 null로 저장됨
        board.setEmployeeId(employeeId);
        return boardRepository.save(board);
    }

    public Board updateBoard(Board board, long boardId, long employeeId, List<String> imagesToDelete) throws IllegalArgumentException {
        Board findBoard = findVerifiedBoard(boardId);

        if (findBoard.getEmployeeId() != employeeId) {
            throw new BusinessLogicException(ExceptionCode.BOARD_UNAUTHORIZED_ACTION);
        }

        Optional.ofNullable(board.getTitle())
                .ifPresent(title -> findBoard.setTitle(title));
        Optional.ofNullable(board.getContent())
                .ifPresent(content -> findBoard.setContent(content));
        Optional.ofNullable(board.getCategory())
                .ifPresent(category -> findBoard.setCategory(category));

        Optional.ofNullable(board.getImageUrls())
                .ifPresent(images -> findBoard.getImageUrls().addAll(images));
        Optional.ofNullable(imagesToDelete)
                .ifPresent(toDelete -> toDelete.forEach(url -> findBoard.getImageUrls().remove(url)));

        return boardRepository.save(findBoard);
    }

    public Board findBoard(long boardId) {
        Board findBoard = findVerifiedBoard(boardId);
        findBoard.setViews(findBoard.getViews() + 1);
        return findBoard;
    }

    @Transactional(readOnly = true)
    public Page<Board> searchBoardTitle(Pageable pageable, String keyword) {
        return boardRepository.searchByTitle(pageable, keyword);
    }

    @Transactional(readOnly = true)
    public Page<Board> searchBoardContent(Pageable pageable, String keyword) {
        return boardRepository.searchByContent(pageable, keyword);
    }

    @Transactional(readOnly = true)
    public Page<Board> searchBoardTitleOrContent(Pageable pageable, String keyword) {
        return boardRepository.searchByTitleOrContent(pageable, keyword);
    }

    @Transactional(readOnly = true)
    public Page<Board> searchBoardEmployeeName(Pageable pageable, String keyword) {
        return boardRepository.searchByEmployeeName(pageable, keyword);
    }

    public void deleteBoard(long boardId, long employeeId) {
        Board findBoard = findVerifiedBoard(boardId);

        if (findBoard.getEmployeeId() == employeeId) {
           boardRepository.delete(findBoard);
        }else {
            throw new BusinessLogicException(ExceptionCode.BOARD_UNAUTHORIZED_ACTION);
        }
    }

    public Board findVerifiedBoard(long boardId) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        return optionalBoard.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
    }
}
