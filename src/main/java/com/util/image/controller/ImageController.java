package com.util.image.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.util.image.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/images")
public class ImageController {

    final private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

//    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity postImage(@Valid @RequestPart("imageDto") ImageDto imageDto,
//                                    @RequestPart("multipartFile") MultipartFile multipartFile){
//        imageService.store(multipartFile);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    @PostMapping
    public ResponseEntity<?> uploadImage(
            @RequestParam("multipartFile") MultipartFile multipartFile) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ImageDto dto = objectMapper.readValue(imageDto, ImageDto.class);

        String uri = imageService.store(multipartFile);

        return new ResponseEntity<>(
                uri,
                HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            imageService.delete(imageUrl);  // S3에서 이미지 삭제
            return new ResponseEntity<>("이미지 삭제 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("이미지 삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
