package com.app.finance_tracker.model.utility;

import com.app.finance_tracker.model.dto.transactionDTO.TransactionReturnDto;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class PdfGenerator<T> {

    @SneakyThrows
    public void generatePdfFile(List<T> list, HttpServletResponse response) {
        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream("dataOutput.pdf"));

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA,14, BaseColor.BLACK);
        for (T data: list) {
            String str = data.toString();
            document.add(new Paragraph(str,font));
            document.add(new Chunk(Chunk.NEWLINE));
        }
        document.close();
        File f = new File("dataOutput.pdf");
        if (!f.exists()) {
            throw new NotFoundException("File does not exist!");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + f.getName() + currentDateTime+".pdf");
        response.setContentLength((int) f.length());
    }
}
