package com.util.board.service;

import com.alarm.kafka.UtilProducer;
import com.util.board.BoardSpecification;
import com.util.board.entity.Board;
import com.util.board.repository.BoardRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.AuthFeignClient;
import com.util.feign.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final AuthFeignClient authFeignClient;
    private final UtilProducer utilProducer;

    public BoardService(BoardRepository boardRepository,
                        AuthFeignClient authFeignClient, UtilProducer utilProducer) {
        this.boardRepository = boardRepository;
        this.authFeignClient = authFeignClient;
        this.utilProducer = utilProducer;
    }

    public Board createBoard(Board board, long employeeId) throws IllegalArgumentException {
        UserResponse employeeDto = authFeignClient.getEmployeeById(employeeId);

        if (employeeDto != null && employeeDto.getData().getEmployeeId() != null) {
            Long fetchEmployeeId = employeeDto.getData().getEmployeeId();
            String employeeName = employeeDto.getData().getName();

            board.setEmployeeId(fetchEmployeeId);
            board.setEmployeeName(employeeName);
            Board savedBoard = boardRepository.save(board);

            //공지사항이 등록되었을 때 알림 발송 기능 (category = notice) > 전체 직원에게 발송...
            if(savedBoard.getCategory().equalsIgnoreCase("공지")) {
                sendNoticeEmployees(savedBoard);
            }

            return savedBoard;
        }
        else {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

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

        Board saveBoard =  boardRepository.save(findBoard);


        return saveBoard;
    }

    public Board findBoard(long boardId) {
        Board findBoard = findVerifiedBoard(boardId);
        findBoard.setViews(findBoard.getViews() + 1);
        return findBoard;
    }

   public Page<Board> searchBoards (String category, String keyword, String searchOption, Pageable pageable) {
        Specification<Board> spec = Specification.where(BoardSpecification.hasCategory(category));

        // 검색 옵션에 따른 동적 쿼리 조합
        if (keyword != null && !keyword.isEmpty()) {
            switch (searchOption) {
                case "title":
                    spec = spec.and(BoardSpecification.hasTitleKeyword(keyword));
                    break;
                case "content":
                    spec = spec.and(BoardSpecification.hasContentKeyword(keyword));
                    break;
                case "title_content":
                    spec = spec.and(BoardSpecification.hasTitleOrContentKeyword(keyword));
                    break;
                case "employeeName":
                    spec = spec.and(BoardSpecification.hasEmployeeName(keyword));
                    break;
            }
        }

        return boardRepository.findAll(spec, pageable);
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

    private void sendNoticeEmployees (Board savedBoard) {


        List<Long> ids = authFeignClient.getEmployeeIds();

        ids.forEach(id -> {
            utilProducer.sendNoticeNotification(
                    id,
                    String.format("공지사항[%s]이 등록되었습니다.",savedBoard.getTitle()),
                    savedBoard.getBoardId()
            );
        });

    }
}
