package com.example.demo.apis.awsS3;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.UUID;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class s3Service {
   @Autowired
   private AmazonS3 amazonS3;
   
    public String uploadImage(MultipartFile multipartFile,String bucketName) {
        System.out.println("uploadImage");
        File file=convert(multipartFile);
        String saveName=file.getName();
        amazonS3.putObject(bucketName,saveName, file);
        file.delete();
        System.out.println("파일업로드 완료");
        return saveName;
    }
    public void deleteFile(String bucktetName,String fileName) {
        System.out.println("deleteFile");
        amazonS3.deleteObject(bucktetName, fileName);
    }
    private File convert(MultipartFile multipartFile) {
        System.out.println("convert");
        File file=new File(LocalDate.now().toString()+UUID.randomUUID()+multipartFile.getOriginalFilename());
        try(FileOutputStream fileOutputStream=new FileOutputStream(file)){
            fileOutputStream.write(multipartFile.getBytes()); 
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException("파일형식변환에 실패했습니다");
        }
        return file;
    }
}
