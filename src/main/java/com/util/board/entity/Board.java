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
public class Board extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Column(name = "board_title")
    private String title;

    @Column(name = "board_content")
    private String content;

    @Column(name = "board_views")
    private int views = 0;

    @Column(name = "comment_count")
    private int commentCount = 0;

    @Column(name = "board_category")
    private String category;

    @Column(name = "board_image_url")
    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardComment> boardCommentList = new ArrayList<>();

    public void setBoardComments(BoardComment boardComment) {
        boardCommentList.add(boardComment);
        if (boardComment.getBoard() != this) {
            boardComment.setBoard(this);
        }
    }
}
