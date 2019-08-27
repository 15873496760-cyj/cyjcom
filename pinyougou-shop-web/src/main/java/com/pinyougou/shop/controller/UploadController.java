package com.pinyougou.shop.controller;

import com.pinoyougou.util.FastDFSClient;
import com.pinyougou.pojo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by WF on 2019/8/23 16:42
 */
@RestController
@RequestMapping
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL ;
    //文件上传
    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        try {
            //得到上传的文件名
            String originalFilename = file.getOriginalFilename();
            //得到文件的后缀名
            //得到文件名的最后一个.
            int beginIndex = originalFilename.lastIndexOf(".");
            //得到文件后缀名
            String suffix = originalFilename.substring(beginIndex);
            //构造文件上传的客户端对象
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //上传文件，得到文件信息
            String fileInfo = fastDFSClient.uploadFile(file.getBytes(), suffix);
            //拼凑文件的全名称
            fileInfo = FILE_SERVER_URL + fileInfo;
            //返回
            return new Result(true,fileInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败!");
        }
    }
}
