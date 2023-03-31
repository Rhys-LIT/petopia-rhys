package com.example.assignmenttwo_starter.utilities;

import com.example.assignmenttwo_starter.model.Order;
import com.example.assignmenttwo_starter.model.OrderItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.OutputStream;

public class OrderPdfPrinter {
    private final Order order;
    private final String productsDirectoryPath;

    public OrderPdfPrinter(Order order, String productsDirectoryPath) {
        this.order = order;
        this.productsDirectoryPath = productsDirectoryPath;
    }

    public void generatePdfReport(OutputStream outputStream) throws DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.add(getHeaderParagraph(order));
        document.add(getOrderItemsTable(order));
        document.add(getFooterParagraph(order));
        document.close();
    }

    private static PdfPTable getOrderItemsTable(Order order) {
        var table = new PdfPTable(5);

        table.addCell(getHeaderCell("Category", Element.ALIGN_LEFT));
        table.addCell(getHeaderCell("Image", Element.ALIGN_LEFT));
        table.addCell(getHeaderCell("Product", Element.ALIGN_LEFT));
        table.addCell(getHeaderCell("Quantity", Element.ALIGN_RIGHT));
        table.addCell(getHeaderCell("Price", Element.ALIGN_RIGHT));

        for (OrderItem orderItem : order.getOrderItems()) {
            table.addCell(getCell(orderItem.getProduct().getCategory().getName(), Element.ALIGN_LEFT));
            table.addCell(getCell("Image", Element.ALIGN_LEFT));
            table.addCell(getCell(orderItem.getProduct().getName(), Element.ALIGN_LEFT));
            table.addCell(getCell(orderItem.getQuantity().toString(), Element.ALIGN_RIGHT));
            table.addCell(getCell(orderItem.getPrice().toString(), Element.ALIGN_RIGHT));
        }
        return table;
    }
    public static PdfPHeaderCell getHeaderCell(String text, int alignment) {
        var paragraph = new Paragraph(text);
        paragraph.setAlignment(alignment);
        return getHeaderCell(paragraph);
    }
    public static PdfPHeaderCell getHeaderCell(Paragraph paragraph) {
        var headerCell = new PdfPHeaderCell();
        headerCell.setUseAscender(true);
        headerCell.setUseDescender(true);
        headerCell.addElement(paragraph);
        return headerCell;
    }

    public static PdfPCell getCell(Paragraph paragraph) {
        PdfPCell cell = new PdfPCell();
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.addElement(paragraph);
        return cell;
    }

    public static PdfPCell getCell(String value, int alignment) {
        Paragraph paragraph = new Paragraph(value);
        paragraph.setAlignment(alignment);
        return getCell(paragraph);
    }

    private static Paragraph getHeaderParagraph(Order order) {
        var headerParagraph = new Paragraph();
        headerParagraph.add(new Paragraph("Order #:" + order.getId().toString()));
        headerParagraph.add(new Paragraph("Date:" + order.getOrderDate().toString()));
        headerParagraph.add(new Paragraph("Status: " + order.getOrderStatus().getName()));
        return headerParagraph;
    }
    private static Paragraph getFooterParagraph(Order order) {
        return new Paragraph("Total: " + order.getTotal());
    }
}
