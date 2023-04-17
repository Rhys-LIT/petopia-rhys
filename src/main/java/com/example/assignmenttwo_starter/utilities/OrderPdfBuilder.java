package com.example.assignmenttwo_starter.utilities;

import com.example.assignmenttwo_starter.model.Order;
import com.example.assignmenttwo_starter.model.OrderItem;
import com.example.assignmenttwo_starter.model.Product;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.OutputStream;

public class OrderPdfBuilder {
    private final Order order;
    private final String productsDirectoryPath;

    public OrderPdfBuilder(Order order, String productsDirectoryPath) {
        this.order = order;
        this.productsDirectoryPath = productsDirectoryPath;
    }
    // Instance methods
    public void generatePdfReport(OutputStream outputStream) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.add(getHeaderParagraph(order));
        document.add(getOrderItemsTable(order, productsDirectoryPath));
        document.add(getFooterParagraph(order));
        document.close();
    }

    // Static methods
    /**
     * Adds the (5) cells for the order item to the table
     *
     * @param table                 the table to add the cells to
     * @param orderItem             the order item to get the data from
     * @param productsDirectoryPath the path to the directory where the product images are stored
     */
    public static void addCellsForOrderItemToTable(PdfPTable table, OrderItem orderItem, String productsDirectoryPath) throws BadElementException, IOException {
        table.addCell(getCell(orderItem.getProduct().getCategory().getName(), Element.ALIGN_LEFT));
        table.addCell(new PdfPCell(getProductImage(orderItem.getProduct(), productsDirectoryPath)));
        table.addCell(getCell(orderItem.getProduct().getName(), Element.ALIGN_LEFT));
        table.addCell(getCell(orderItem.getQuantity().toString(), Element.ALIGN_RIGHT));
        table.addCell(getCell(orderItem.getPrice().toString(), Element.ALIGN_RIGHT));
    }

    /**
     * Adds the (5) header cells to the table
     * @param table the table to add the cells to
     */
    public static void addHeaderCellsToTable(PdfPTable table) {
        table.addCell(getHeaderCell("Category", Element.ALIGN_LEFT));
        table.addCell(getHeaderCell("Image", Element.ALIGN_LEFT));
        table.addCell(getHeaderCell("Product", Element.ALIGN_LEFT));
        table.addCell(getHeaderCell("Quantity", Element.ALIGN_RIGHT));
        table.addCell(getHeaderCell("Price", Element.ALIGN_RIGHT));
    }

    /**
     * Returns a paragraph with the header information
     * @param paragraph the paragraph to be added to the cell
     * @return a cell with the paragraph ascender and descender set to true
     */
    public static PdfPCell getCell(Paragraph paragraph) {
        var cell = new PdfPCell(paragraph);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        return cell;
    }

    public static PdfPCell getCell(String value, int alignment) {
        var paragraph = new Paragraph(value);
        paragraph.setAlignment(alignment);
        return getCell(paragraph);
    }

    private static Paragraph getFooterParagraph(Order order) {
        return new Paragraph("Total: " + order.getTotal());
    }

    /**
     * Returns a HeaderCell with a paragraph for the header title with the specified alignment
     * @param headerTitle the title of the header
     * @param alignment the alignment of the header title
     * @return a HeaderCell with a paragraph for the header title with the specified alignment
     */
    public static PdfPHeaderCell getHeaderCell(String headerTitle, int alignment) {
        var paragraph = new Paragraph(headerTitle);
        paragraph.setAlignment(alignment);
        return getHeaderCell(paragraph);
    }

    /**
     * Returns a HeaderCell with the specified paragraph for the header cell with ascender and descender set to true
     * @param paragraph the paragraph to be added to the header cell
     * @return a HeaderCell with the specified paragraph for the header cell with ascender and descender set to true
     */
    public static PdfPHeaderCell getHeaderCell(Paragraph paragraph) {
        var headerCell = new PdfPHeaderCell();
        headerCell.setUseAscender(true);
        headerCell.setUseDescender(true);
        headerCell.addElement(paragraph);
        return headerCell;
    }

    public static Paragraph getHeaderParagraph(Order order) {
        var headerParagraph = new Paragraph();
        headerParagraph.add(new Paragraph("Name:" + order.getCustomer().getFirstName() +" " + order.getCustomer().getLastName() ));
        headerParagraph.add(new Paragraph("Order #:" + order.getId().toString()));
        headerParagraph.add(new Paragraph("Date:" + order.getOrderDate().toString()));
        headerParagraph.add(new Paragraph("Status: " + order.getOrderStatus().getName()));
        headerParagraph.add(new Paragraph(" "));
        return headerParagraph;
    }

    public static PdfPTable getOrderItemsTable(Order order, String productsDirectoryPath) throws BadElementException, IOException {
        var table = new PdfPTable(5);

        addHeaderCellsToTable(table);

        for (OrderItem orderItem : order.getOrderItems()) {
            addCellsForOrderItemToTable(table, orderItem, productsDirectoryPath);
        }
        return table;
    }

    private static Image getProductImage(Product product, String productsDirectoryPath) throws IOException, BadElementException {
        String path = productsDirectoryPath + product.getImage();
        ClassPathResource resource = new ClassPathResource(path);
        return getImage(resource);
    }

    private static Image getImage(ClassPathResource resource) throws IOException, BadElementException {
        Image image = Image.getInstance(resource.getURL());
        image.scalePercent(50, 50);
        image.setAlignment(Element.ALIGN_RIGHT);
        return image;
    }
}
