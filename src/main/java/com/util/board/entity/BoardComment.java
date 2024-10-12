package com.util.board.entity;

import com.util.audit.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class BoardComment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardCommentId;

    @Column(name = "board_comment_content")
    private String content;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    public void setBoard(Board board) {
        this.board = board;
        if (!board.getBoardCommentList().contains(this)) {
            board.setBoardComments(this);
        }
    }

    public BoardComment(Long boardCommentId) {
        this.boardCommentId = boardCommentId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private BoardComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardComment> replies = new ArrayList<>();

    public void addReply(BoardComment reply) {
        replies.add(reply);
        reply.setParentComment(this);
    }
}
