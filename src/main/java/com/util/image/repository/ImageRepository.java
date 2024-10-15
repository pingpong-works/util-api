package com.util.image.repository;


import com.util.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    //해당 포스트의 모든 그림
    List<Image> findAllByPostId(long postid);
}
