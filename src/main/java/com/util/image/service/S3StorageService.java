package com.util.image.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Slf4j
public class S3StorageService implements ImageService {
    private static final String BUCKET_NAME = "meetsipdrink-bucket";
    private static final String BUCKET_IMAGE_PATH = "imageserver";
    private final S3Client s3Client;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String store(MultipartFile multipartFile) {
        UUID uuid = UUID.randomUUID();
        String filename = uuid + "_" + multipartFile.getOriginalFilename();

        String key = makeS3ObjectKey(BUCKET_IMAGE_PATH, filename);

        PutObjectRequest request = createPutObjectRequest(BUCKET_NAME, key, multipartFile.getContentType());

        try {
            s3Client.putObject(request, RequestBody.fromBytes(multipartFile.getBytes()));
            URL fileUrl = s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(BUCKET_NAME).key(key).build());
            String uri = fileUrl.toString().replace(" ", "");

            return uri;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    private String makeS3ObjectKey(String bucketImagePath, String filename) {
        return bucketImagePath + "/" + filename;
    }

    // Content-Type 설정을 추가하는 메서드
    private PutObjectRequest createPutObjectRequest(String bucketName, String key, String contentType) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)  // Content-Type 설정
                .build();
    }

    private String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.indexOf(BUCKET_IMAGE_PATH));
    }
}
