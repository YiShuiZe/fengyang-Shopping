package com.fengyang.upload.service;

import com.fengyang.common.enums.ExceptionEnum;
import com.fengyang.common.exception.FyException;
import com.fengyang.upload.config.UploadProperties;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UploadProperties prop;

    //private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg", "image/png", "image/bmp");

    public String uploadImage(MultipartFile file) {
        try {
            // 校验文件类型
            String contentType = file.getContentType();
            if (!prop.getAllowTypes().contains(contentType)) {
                throw new FyException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            // 校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new FyException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            /*
            // 准备目标路径
            File dest = new File("/home/yang/disk30/IdeaProject/upload", file.getOriginalFilename());
            // 保存文件到本地
            file.transferTo(dest);
            */
            // 上传到FastDFS
            // 效率差String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            // 返回路径
            return prop.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            // 上传失败 保存日志
            log.error("【文件上传】上传文件失败！", e);
            throw new FyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
