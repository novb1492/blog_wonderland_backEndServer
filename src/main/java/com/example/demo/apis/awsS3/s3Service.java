package com.example.demo.apis.awsS3;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.UUID;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo.utill.utillService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class s3Service {
    private final static Logger LOGGER=LoggerFactory.getLogger(s3Service.class);
    @Autowired
    private AmazonS3 amazonS3;
   
    public String uploadImage(MultipartFile multipartFile,String bucketName) {
        LOGGER.info("uploadImage");
        File file=convert(multipartFile);
        String saveName=file.getName();
        amazonS3.putObject(bucketName,saveName, file);
        file.delete();
        LOGGER.info("파일업로드 완료");
        return saveName;
    }
    public void deleteFile(String bucktetName,String fileName) {
        LOGGER.info("deleteFile");
        amazonS3.deleteObject(bucktetName, fileName);
    }
    private File convert(MultipartFile multipartFile) {
        LOGGER.info("convert");
        File file=new File(LocalDate.now().toString()+UUID.randomUUID()+multipartFile.getOriginalFilename());
        try(FileOutputStream fileOutputStream=new FileOutputStream(file)){
            fileOutputStream.write(multipartFile.getBytes()); 
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(e.getMessage());
            throw utillService.makeRuntimeEX("파일형식변환에 실패했습니다","convert");
        }
        return file;
    }
}
