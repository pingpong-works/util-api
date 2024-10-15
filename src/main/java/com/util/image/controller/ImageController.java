package com.util.image.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.util.image.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/util/images")
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
}
