package com.util.board.controller;

import com.util.board.dto.BoardDto;
import com.util.board.entity.Board;
import com.util.board.mapper.BoardMapper;
import com.util.board.service.BoardService;
import com.util.dto.MultiResponseDto;
import com.util.dto.SingleResponseDto;
import com.util.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/boards")
public class BoardController {
    private final static String BOARD_DEFAULT_URL = "/boards";
    private final BoardService boardService;
    private final BoardMapper mapper;

    public BoardController(BoardService boardService,
                           BoardMapper mapper) {
        this.boardService = boardService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postBoard(@Valid @RequestBody BoardDto.Post requestBody,
                                    @RequestParam("employeeId") @Positive long employeeId) {
        Board board = mapper.boardPostDtoToPost(requestBody);
        Board createBoard = boardService.createBoard(board, employeeId);
        URI location = UriCreator.createUri(BOARD_DEFAULT_URL, createBoard.getBoardId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{board-id}")
    public ResponseEntity patchBoard(@PathVariable("board-id") @Positive long boardId,
                                     @Valid @RequestBody BoardDto.Patch requestBody,
                                     @RequestParam("employeeId") @Positive long employeeId) {
        Board board = mapper.boardPatchDtoToBoard(requestBody);

        List<String> imagesToDelete = requestBody.getImagesToDelete();

        Board updateBoard = boardService.updateBoard(board, boardId, employeeId, imagesToDelete);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.boardToBoardResponseDto(updateBoard)), HttpStatus.OK);
    }

    @GetMapping("/{board-id}")
    public ResponseEntity getBoard(@PathVariable("board-id") @Positive long boardId) {
        Board board = boardService.findBoard(boardId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.boardToBoardResponseDto(board)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getBoards(@RequestParam("keyword") String keyword,
                                    @RequestParam("searchOption") String searchOption,
                                    @RequestParam(required = false) String sort,
                                    @PageableDefault(sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {
        if (sort != null) {
            Sort sortOrder = Sort.by(sort.split("_")[0]).ascending();
            if (sort.split("_")[1].equalsIgnoreCase("desc")) {
                sortOrder = sortOrder.descending();
            }
            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sortOrder);
        }

        Page<Board> searchList;

        switch (searchOption) {
            case "title":
                searchList = boardService.searchBoardTitle(pageable, keyword);
                break;
            case "content":
                searchList = boardService.searchBoardContent(pageable, keyword);
                break;
            case "title_content":
                searchList = boardService.searchBoardTitleOrContent(pageable, keyword);
                break;
            case "employeeName":
                searchList = boardService.searchBoardEmployeeName(pageable, keyword);
                break;
            default:
                throw new IllegalArgumentException("유효한 검색 옵션이 아닙니다.");
        }

        List<BoardDto.Response> responseList = searchList.stream()
                .map(mapper::boardToBoardResponseDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                new MultiResponseDto<>(responseList, searchList), HttpStatus.OK);
    }

    @DeleteMapping("/{board-id}")
    public ResponseEntity deleteBoard(@PathVariable("board-id") @Positive long boardId,
                                      @RequestParam("employeeId") @Positive long employeeId) {
        boardService.deleteBoard(boardId, employeeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
