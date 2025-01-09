package com.rosy.web.controller.minervia;

import com.rosy.common.config.MinerviaConfig;
import com.rosy.common.core.domain.AjaxResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/file")
public class FileController {

    SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd");

    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file, HttpServletRequest request) {
        String format = sdf.format(new Date());
        String path = MinerviaConfig.getProfile() + format;
        File Folder = new File(path);
        if (!Folder.exists()) {
            Folder.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        String newFileName = System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
        try {
            file.transferTo(new File(path + "/" + newFileName));
            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/file/view" + format + "/" + newFileName;
            System.out.println("url = " + url);
            return AjaxResult.success(url);
        } catch (Exception e) {
            return AjaxResult.error("上传失败");
        }
    }

    @GetMapping("/view/{year}/{month}/{day}/{fileName}")
    public void viewFile(HttpServletResponse response,
                         @PathVariable(name = "year") String year,
                         @PathVariable(name = "month") String month,
                         @PathVariable(name = "day") String day,
                         @PathVariable(name = "fileName") String fileName) {

        String path = MinerviaConfig.getProfile() + "/" + year + "/" + month + "/" + day + "/" + fileName;
        File file = new File(path);
        if (!file.exists()) {
            response.setStatus(404);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            response.setContentType("image/jpeg");
            response.setContentLength((int) file.length());
            response.getOutputStream().write(data);
        } catch (IOException e) {
            throw new RuntimeException("文件读取错误");
        }
    }
}