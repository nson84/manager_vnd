package Manager_vnd.Manager.feature.cashbook;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import Manager_vnd.Manager.feature.cashbook.dto.CashStatsResponse;

@Component
public class CashbookPdfExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] export(
            List<CashEntry> entries,
            CashStatsResponse stats,
            LocalDate from,
            LocalDate to,
            String exportedAt) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);

            document.add(new Paragraph("SO QUY / CASHBOOK", titleFont));
            document.add(new Paragraph(
                    "Ky: " + formatDate(from) + " - " + formatDate(to) + " | Xuat luc: " + exportedAt + " (VN)",
                    normal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Tong thu: " + money(stats.totalIn())
                            + "  |  Tong chi: " + money(stats.totalOut())
                            + "  |  Ton quy: " + money(stats.balance()),
                    normal));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{1.2f, 1f, 1.4f, 1.8f, 2.5f, 2f, 1.2f, 1.5f});
            table.setWidthPercentage(100);
            addHeader(table, headerFont, "Ngay", "Chieu", "So tien", "Loai", "Mo ta", "Ghi chu", "Doi chieu", "Nguon");

            for (CashEntry entry : entries) {
                addCell(table, normal, formatDate(entry.getEntryDate()));
                addCell(table, normal, entry.getDirection().name());
                addCell(table, normal, money(entry.getAmount()));
                addCell(table, normal, entry.getCategory() != null ? entry.getCategory().getName() : "");
                addCell(table, normal, nullToEmpty(entry.getDescription()));
                addCell(table, normal, nullToEmpty(entry.getNote()));
                addCell(table, normal, entry.isChecked() ? "X" : "");
                addCell(table, normal, entry.getRefType().name());
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Không tạo được PDF sổ quỹ", ex);
        }
    }

    private void addHeader(PdfPTable table, Font font, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addCell(PdfPTable table, Font font, String text) {
        table.addCell(new Phrase(text == null ? "" : text, font));
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : DATE_FMT.format(date);
    }

    private String money(BigDecimal amount) {
        return amount == null ? "0" : amount.toPlainString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
