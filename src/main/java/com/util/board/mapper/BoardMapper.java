package com.util.board.mapper;

import com.util.board.dto.BoardDto;
import com.util.board.entity.Board;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BoardCommentMapper.class})
public interface BoardMapper {

    Board boardPostDtoToPost(BoardDto.Post requestBody);

    Board boardPatchDtoToBoard(BoardDto.Patch requestBody);

    BoardDto.Response boardToBoardResponseDto(Board board);

    List<BoardDto.Response> boardsToBoardResponseDtos(List<Board> boards);
}
