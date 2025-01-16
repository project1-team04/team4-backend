package com.elice.team04backend.service;

import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.UUID;

@Service
public class FirebaseStorageService {

    @Value("${firebase.storage.bucket-name}")
    private String bucketName;

    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Blob blob = StorageClient.getInstance().bucket(bucketName).create(fileName, file.getInputStream(), file.getContentType());

        return "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + fileName + "?alt=media";
    }
}
