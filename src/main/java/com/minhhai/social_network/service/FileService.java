package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.request.MediaRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final CloudinaryService cloudinaryService;

    public String upload(MediaRequestDTO mediaDataRequestDTO, String folderName) throws IOException {
        String extension = getExtension(mediaDataRequestDTO.getName());
        String publicId = UUID.randomUUID().toString();

        String url = String.format("https://res.cloudinary.com/%s/%s/upload/%s/%s.%s",
                cloudinaryService.getCloudName(),
                mediaDataRequestDTO.getType().split("/")[0],
                folderName, publicId, extension
        );

        MultipartFile fileData = base64ToMultipartFile(mediaDataRequestDTO);
        cloudinaryService.uploadFile(fileData, folderName, publicId);

        return url;
    }

    private MultipartFile base64ToMultipartFile(MediaRequestDTO data) {
        byte[] bytes = decodeBase64(data.getBase64Data());
        return new MockMultipartFile(data.getName(), data.getName(), data.getType(), bytes);
    }

    private byte[] decodeBase64(String base64Data) {
        if (base64Data.contains(",")) {
            base64Data = base64Data.split(",")[1];
        }

        return Base64.getDecoder().decode(base64Data);
    }

    private String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return (i > 0) ? filename.substring(i + 1) : "jpg";
    }
}
