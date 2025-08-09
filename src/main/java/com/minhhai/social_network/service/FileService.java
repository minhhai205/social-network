package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.request.MessageMediaRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class FileService {
    private final CloudinaryService cloudinaryService;

    public String upload(MessageMediaRequestDTO mediaDataRequestDTO, String folderName) throws IOException {
        MultipartFile fileData = base64ToMultipartFile(mediaDataRequestDTO);

        return cloudinaryService.uploadFile(fileData, folderName)
                .get("secure_url").toString();
    }

    private MultipartFile base64ToMultipartFile(MessageMediaRequestDTO data) {
        byte[] bytes = decodeBase64(data.getBase64Data());
        return new MockMultipartFile(data.getName(), data.getName(), data.getType(), bytes);
    }

    private byte[] decodeBase64(String base64Data) {
        if (base64Data.contains(",")) {
            base64Data = base64Data.split(",")[1];
        }

        return Base64.getDecoder().decode(base64Data);
    }
}
