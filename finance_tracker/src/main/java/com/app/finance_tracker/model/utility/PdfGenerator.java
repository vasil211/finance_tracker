package com.app.finance_tracker.model.utility;

import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForReturnDTO;
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
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PdfGenerator<T> {

    @SneakyThrows
    public void generatePdfFile(List<T> list, HttpServletResponse response,
                                Map<CurrencyForReturnDTO, Double> totalAmountsSend,
                                Map<CurrencyForReturnDTO, Double> totalAmountsReceived) {
        Document document = new Document();
        String fileName = "reference-" + System.currentTimeMillis() + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
        for (T data : list) {
            String str = data.toString();
            document.add(new Paragraph(str, font));
            document.add(new Chunk(Chunk.NEWLINE));
        }
        if (totalAmountsSend != null && totalAmountsSend.size() > 0) {
            document.add(new Paragraph("Total amount spend:", font));
            document.add(new Chunk(Chunk.NEWLINE));

            for (Map.Entry<CurrencyForReturnDTO, Double> set :
                    totalAmountsSend.entrySet()) {
                String str = set.getKey().getCode() + " : -" + set.getValue() + set.getKey().getSymbol();
                document.add(new Paragraph(str, font));
                document.add(new Chunk(Chunk.NEWLINE));
            }
        }
        if (totalAmountsReceived != null && totalAmountsReceived.size() > 0) {
            document.add(new Paragraph("Total amount received:", font));
            document.add(new Chunk(Chunk.NEWLINE));

            for (Map.Entry<CurrencyForReturnDTO, Double> set :
                    totalAmountsReceived.entrySet()) {
                String str = set.getKey().getCode() + " : +" + set.getValue() + set.getKey().getSymbol();
                document.add(new Paragraph(str, font));
                document.add(new Chunk(Chunk.NEWLINE));
            }
        }
        document.close();
        File f = new File(fileName);
        if (!f.exists()) {
            throw new NotFoundException("File does not exist!");
        }
        response.setContentType(Files.probeContentType(f.toPath()));

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".pdf");
        response.setContentLength((int) f.length());
        Files.copy(f.toPath(), response.getOutputStream());
        f.delete();
    }
}
