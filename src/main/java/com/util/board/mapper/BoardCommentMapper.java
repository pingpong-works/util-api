package com.util.board.mapper;

import com.util.board.dto.BoardCommentDto;
import com.util.board.entity.BoardComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BoardCommentMapper {

    @Mapping(target = "parentComment", expression = "java(requestBody.getParentCommentId() != null ? new BoardComment(requestBody.getParentCommentId()) : null)")
    BoardComment boardCommentPostDtoToBoardComment(BoardCommentDto.Post requestBody);

    BoardComment boardCommentPatchDtoToBoardComment(BoardCommentDto.Patch requestBody);

    @Mapping(source = "board.boardId", target = "boardId")
    @Mapping(source = "parentComment.boardCommentId", target = "parentCommentId")
    BoardCommentDto.Response boardCommentToBoardCommentResponseDto(BoardComment boardComment);

    @Named("boardCommentToBoardCommentResponse")
    List<BoardCommentDto.Response> boardCommentsToBoardCommentResponseDtos(List<BoardComment> boardComments);
}
