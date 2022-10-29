package com.app.finance_tracker.service;

import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.app.finance_tracker.model.entities.Icon;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;

@Service
public class IconService extends AbstractService {

    @SneakyThrows
    public Icon addIcon(MultipartFile file) {
        Icon icon = new Icon();
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "-"
                + new Random().nextInt(1, 1000) + "." + ext;
        File f = new File("categories" + File.separator + name);
        Files.copy(file.getInputStream(), f.toPath());
        icon.setUrl(name);
        iconRepository.save(icon);
        return icon;
    }

    public void deleteIcon(long id) {
        Icon icon = iconRepository.findById(id).orElseThrow(() -> new NotFoundException("Icon not found"));
        File f = new File("categories" + File.separator + icon.getUrl());
        f.delete();
        iconRepository.delete(icon);
    }

    @SneakyThrows
    public void download(String filePath, HttpServletResponse resp) {
        File f = new File("categories" + File.separator + filePath);
        if(!f.exists()){
            throw new NotFoundException("File does not exist!");
        }
        resp.setContentType(Files.probeContentType(f.toPath()));
        Files.copy(f.toPath(), resp.getOutputStream());
    }
}

