package com.util.board.controller;

import com.util.board.dto.BoardCommentDto;
import com.util.board.entity.Board;
import com.util.board.entity.BoardComment;
import com.util.board.mapper.BoardCommentMapper;
import com.util.board.service.BoardCommentService;
import com.util.board.service.BoardService;
import com.util.dto.SingleResponseDto;
import com.util.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/boards/{board-id}/comments")
public class BoardCommentController {
    private final static String BOARD_COMMENT_DEFAULT_URL = "/boards/{board-id}/comments";
    private final BoardCommentService boardCommentService;
    private final BoardService boardService;
    private final BoardCommentMapper mapper;

    public BoardCommentController(BoardCommentService boardCommentService, BoardService boardService,
                                  BoardCommentMapper mapper) {
        this.boardCommentService = boardCommentService;
        this.boardService = boardService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postBoardComment(@PathVariable("board-id") @Positive long boardId,
                                           @Valid @RequestBody BoardCommentDto.Post requestBody,
                                           @RequestParam("employeeId") @Positive long employeeId) {
        BoardComment boardComment = mapper.boardCommentPostDtoToBoardComment(requestBody);

        Board board = boardService.findVerifiedBoard(boardId);
        boardComment.setBoard(board);

        BoardComment createBoardComment = boardCommentService.createBoardComment(boardComment, employeeId);

        URI location = UriCreator.createUri(BOARD_COMMENT_DEFAULT_URL.replace("{board-id}", String.valueOf(boardId)), createBoardComment.getBoardCommentId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{comment-id}")
    public ResponseEntity patchBoardComment(@PathVariable("comment-id") @Positive long boardCommentId,
                                            @Valid @RequestBody BoardCommentDto.Patch requestBody,
                                            @RequestParam("employeeId") @Positive long employeeId) {
        BoardComment updateBoardComment = mapper.boardCommentPatchDtoToBoardComment(requestBody);
        BoardComment boardComment = boardCommentService.updateBoardComment(boardCommentId, updateBoardComment, employeeId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.boardCommentToBoardCommentResponseDto(boardComment)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BoardCommentDto.Response>> getBoardComments(@PathVariable("board-id") @Positive long boardId) {
        List<BoardComment> boardComments = boardCommentService.findBoardCommentsByBoardId(boardId);
        List<BoardCommentDto.Response> responseDtos = mapper.boardCommentsToBoardCommentResponseDtos(boardComments);

        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @DeleteMapping("/{comment-id}")
    public ResponseEntity deleteBoardComment(@PathVariable("comment-id") @Positive long boardCommentId,
                                             @RequestParam("employeeId") @Positive long employeeId) {
        boardCommentService.deleteBoardComment(boardCommentId, employeeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
