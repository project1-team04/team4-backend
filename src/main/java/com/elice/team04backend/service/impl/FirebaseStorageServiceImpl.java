package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.service.FirebaseStorageService;
import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.UUID;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    @Value("${firebase.storage.bucket-name}")
    private String bucketName;

    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Blob blob = StorageClient.getInstance().bucket(bucketName).create(fileName, file.getInputStream(), file.getContentType());

        return "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + fileName + "?alt=media";
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            String fileName = imageUrl.substring(imageUrl.indexOf("/o/") + 3, imageUrl.indexOf("?alt=media"));
            StorageClient.getInstance().bucket(bucketName).get(fileName).delete();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IMAGE_DELETE_FAILED);
        }
    }
}
