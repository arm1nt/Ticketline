package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.splitting.DefaultSplitCharacters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static at.ac.tuwien.sepm.groupphase.backend.service.impl.OrderServiceImpl.COMPANY_UID;

@Component
public class InvoicePdfCreation {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected static Cell createCell(String text) throws IOException {
        LOGGER.trace("createCell({})", text);

        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);

        Paragraph paragraph = new Paragraph(text)
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(10F);
        paragraph.setProperty(Property.SPLIT_CHARACTERS, new InvoicePdfCreation.CustomSplitCharacters());
        cell.add(paragraph);
        return cell;
    }

    protected static Cell createCellWithMargin(String text) throws IOException {
        LOGGER.trace("createCellWithMargin({})", text);

        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);

        Paragraph paragraph = new Paragraph(text)
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(10F)
            .setMarginTop(10);
        paragraph.setProperty(Property.SPLIT_CHARACTERS, new InvoicePdfCreation.CustomSplitCharacters());
        cell.add(paragraph);
        return cell;
    }

    protected static void addCompanyBanner(Document document) throws MalformedURLException {
        LOGGER.trace("addCompanyBanner()");

        String path = OrderServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        int i = path.indexOf("backend/");
        String absolutePath = path.substring(0, i + "backend/".length());
        absolutePath += "/src/main/resources/pdf/InvoiceBanner.png";

        ImageData imgData = ImageDataFactory.create(absolutePath);
        Image image = new Image(imgData)
            .setWidth(UnitValue.createPercentValue(95))
            .setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(image);
    }

    protected static void addCompanyAddress(Document document, PdfFont standardFont) {
        LOGGER.trace("addCompanyAddress()");

        document.add(new Paragraph("Ticketline GmbH - Wiedner Hauptstraße 4 - 1040 Vienna - Austria")
            .setMarginTop(20)
            .setMarginBottom(20)
            .setFont(standardFont)
            .setTextAlignment(TextAlignment.LEFT)
            .setFontSize(10));
    }

    protected static void addOrderDetailsInvoice(Document document, ApplicationUser applicationUser,
                                        LocalDateTime time, long orderId) throws IOException {
        LOGGER.trace("addOrderDetailsInvoice({}, {}, {})", applicationUser, time, orderId);

        String combinedName = "" + applicationUser.getFirstName() + " " + applicationUser.getLastName();
        String combinedZipCodeAndCity = "" + applicationUser.getZipCode() + ", " + applicationUser.getCity();
        String[] userNameAndAddress = {combinedName, applicationUser.getStreet(), combinedZipCodeAndCity, applicationUser.getCountry()};

        String[] orderDetails = {"Order Date: " + time.toString().substring(0, 10), "Invoice number: " + orderId, "UID: " + COMPANY_UID};

        Table orderDetailsTable = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();

        int insertUserAddressCounter = 0;
        int orderInformationCounter = 0;

        for (int i = 1; i < 13; i++) {

            if (i % 3 == 0) {
                Cell cell = createCell(userNameAndAddress[insertUserAddressCounter]);
                cell.setTextAlignment(TextAlignment.RIGHT);
                insertUserAddressCounter++;
                orderDetailsTable.addCell(cell);
                continue;
            }

            if (i % 3 == 1 && orderInformationCounter < 3) {
                Cell cell = createCell(orderDetails[orderInformationCounter]);
                cell.setTextAlignment(TextAlignment.LEFT);
                orderInformationCounter++;
                orderDetailsTable.addCell(cell);
                continue;
            }

            Cell cell = createCell("");
            orderDetailsTable.addCell(cell);
        }
        document.add(orderDetailsTable);
    }

    protected static void orderOverview(Document document, PdfFont headlineFont, Collection<Ticket> ticketList,
                               long orderId, Payment currentOrder) throws IOException {
        LOGGER.trace("orderOverview({})", orderId);

        document.add(new Paragraph("Order Overview: ")
                .setFont(headlineFont)
                .setMarginTop(60F)
                .setMarginBottom(10))
            .setFontSize(20);

        ArrayList<Ticket> ticketsToRefund = new ArrayList<>(ticketList);
        Collections.sort(ticketsToRefund, new OrderServiceImpl.CustomSeparator());
        ticketList = ticketsToRefund;

        Table orderOverview = new Table(UnitValue.createPercentArray(6)).useAllAvailableWidth();

        String[] orderOverviewHeader = {"Quantity", "Performance", "Sector", "Row", "Seat", "Price"};
        for (int i = 0; i < 6; i++) {
            Cell cell = createCell(orderOverviewHeader[i]);
            cell.setPaddingLeft(5);
            orderOverview.addHeaderCell(cell);
        }

        ArrayList<OrderServiceImpl.CustomStandingStruct> standingTickets = new ArrayList<>();

        int countNumberOfStandingPlaces = 0;

        for (Ticket t : ticketList) {

            if (t.getStand() != null) {
                boolean foundFlag = false;

                for (OrderServiceImpl.CustomStandingStruct c : standingTickets) {
                    if (c.sectorId.equals(t.getStand().getStanding().getSectorId())
                        && c.performanceName.equals(t.getPerformance().getPerformanceName())) {
                        c.addTicketToSector();
                        foundFlag = true;
                        break;
                    }

                }

                if (!foundFlag) {
                    OrderServiceImpl.CustomStandingStruct addStandingSector = new OrderServiceImpl.CustomStandingStruct(
                        t.getStand().getStanding().getSectorId(),
                        t.getPerformance().getPerformanceName(),
                        t.getStand().getStanding().getPrice()
                    );
                    standingTickets.add(addStandingSector);
                }

                continue;
            }

            Cell quantity = createCell("1");
            quantity.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
            quantity.setPaddingLeft(5);

            Cell performance = createCell(t.getPerformance().getPerformanceName());
            performance.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
            performance.setPaddingLeft(5);

            Cell sector;
            Cell rowId;
            Cell seatId;
            Cell price;


            Seat checkForSeat = t.getSeat();
            if (checkForSeat == null) {

                sector = createCell(" - ");
                sector.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                sector.setPaddingLeft(5);

                rowId = createCell(" - ");
                rowId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                rowId.setPaddingLeft(5);

                seatId = createCell(" - ");
                seatId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                seatId.setPaddingLeft(5);

                price = createCell(" - ");
                price.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                price.setPaddingLeft(5);
            } else {

                sector = createCell("Sector " + checkForSeat.getRow().getSeating().getSectorId());
                sector.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                sector.setPaddingLeft(5);

                rowId = createCell(checkForSeat.getRow().getRowNumber() + "");
                rowId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                rowId.setPaddingLeft(5);

                seatId = createCell(checkForSeat.getSeatId() + "");
                seatId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                seatId.setPaddingLeft(5);

                price = createCell("" + t.getSeat().getRow().getSeating().getPrice() + " €");
                price.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                price.setPaddingLeft(5);
            }

            orderOverview.addCell(quantity).addCell(performance).addCell(sector).addCell(rowId).addCell(seatId).addCell(price);
        }

        if (standingTickets.size() > 0) {

            for (OrderServiceImpl.CustomStandingStruct c : standingTickets) {
                Cell quantity = createCell(c.ticketsInThisSector + "");
                quantity.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                quantity.setPaddingLeft(5);

                Cell performance = createCell(c.performanceName);
                performance.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                performance.setPaddingLeft(5);

                Cell sector = createCell("Sector " + c.sectorId);
                sector.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                sector.setPaddingLeft(5);

                Cell rowId = createCell(" - ");
                rowId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                rowId.setPaddingLeft(5);

                Cell seatId = createCell(" - ");
                seatId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                seatId.setPaddingLeft(5);

                Cell price = createCell(c.totalPrice + " €");
                price.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                price.setPaddingLeft(5);

                orderOverview.addCell(quantity).addCell(performance).addCell(sector).addCell(rowId).addCell(seatId).addCell(price);
            }
        }

        Cell placeholder1 = createCellWithMargin("VAT included");
        orderOverview.addCell(placeholder1);
        Cell placeholder2 = createCellWithMargin("");
        orderOverview.addCell(placeholder2);
        Cell placeholder3 = createCellWithMargin("");
        orderOverview.addCell(placeholder3);
        Cell placeholder4 = createCellWithMargin("");
        orderOverview.addCell(placeholder4);
        Cell placeholder5 = createCellWithMargin("Total sum: ");
        placeholder3.setPaddingLeft(5);
        orderOverview.addCell(placeholder5);

        Cell placeholder6 = createCellWithMargin("" + currentOrder.getTotal() + " €");
        placeholder4.setPaddingLeft(5);
        orderOverview.addCell(placeholder6);

        document.add(orderOverview);
    }

    protected static void addRefundDetailsCancellation(Document document, ApplicationUser applicationUser,
                                              LocalDateTime time, long orderId) throws IOException {
        LOGGER.trace("addRefundDetailsCancellation({}, {}, {})", applicationUser, time, orderId);

        String combinedName = "" + applicationUser.getFirstName() + " " + applicationUser.getLastName();
        String combinedZipCodeAndCity = "" + applicationUser.getZipCode() + ", " + applicationUser.getCity();
        String[] userNameAndAddress = {combinedName, applicationUser.getStreet(), combinedZipCodeAndCity, applicationUser.getCountry()};

        String[] refundDetails = {"Refund date: " + time.toString().substring(0, 10), "Refund number: " + orderId, "UID: " + COMPANY_UID};

        Table refundDetailsTable = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();

        int insertUserAddressCounter = 0;
        int orderInformationCounter = 0;

        for (int i = 1; i < 13; i++) {

            if (i % 3 == 0) {
                Cell cell = createCell(userNameAndAddress[insertUserAddressCounter]);
                cell.setTextAlignment(TextAlignment.RIGHT);
                insertUserAddressCounter++;
                refundDetailsTable.addCell(cell);
                continue;
            }

            if (i % 3 == 1 && orderInformationCounter < 3) {
                Cell cell = createCell(refundDetails[orderInformationCounter]);
                cell.setTextAlignment(TextAlignment.LEFT);
                orderInformationCounter++;
                refundDetailsTable.addCell(cell);
                continue;
            }

            Cell cell = createCell("");
            refundDetailsTable.addCell(cell);
        }
        document.add(refundDetailsTable);

    }

    protected static void addRefundOverview(Document document, PdfFont headlineFont, Collection<Ticket> ticketList,
                                   long orderId) throws IOException {
        LOGGER.trace("addRefundOverview({})", orderId);

        document.add(new Paragraph("Refund Overview: ")
                .setFont(headlineFont)
                .setMarginTop(5F)
                .setMarginBottom(10))
            .setFontSize(18);

        ArrayList<Ticket> ticketsToPurchase = new ArrayList<>(ticketList);
        Collections.sort(ticketsToPurchase, new OrderServiceImpl.CustomSeparator());
        ticketList = ticketsToPurchase;

        Table refundOverview = new Table(UnitValue.createPercentArray(6)).useAllAvailableWidth();

        String[] refundOverviewHeader = {"Quantity", "Performance", "Sector", "Row", "Seat", "Refund"};
        for (int i = 0; i < 6; i++) {
            Cell cell = createCell(refundOverviewHeader[i]);
            cell.setPaddingLeft(5);
            refundOverview.addHeaderCell(cell);
        }

        double sum = 0;
        ArrayList<OrderServiceImpl.CustomStandingStruct> standingTickets = new ArrayList<>();

        for (Ticket t : ticketList) {
            sum += (t.getSeat() == null)
                ? t.getStand().getStanding().getPrice()
                : t.getSeat().getRow().getSeating().getPrice();


            if (t.getStand() != null) {
                boolean foundFlag = false;

                for (OrderServiceImpl.CustomStandingStruct c : standingTickets) {
                    if (c.sectorId.equals(t.getStand().getStanding().getSectorId())
                        && c.performanceName.equals(t.getPerformance().getPerformanceName())) {
                        c.addTicketToSector();
                        foundFlag = true;
                        break;
                    }

                }

                if (!foundFlag) {
                    OrderServiceImpl.CustomStandingStruct addStandingSector = new OrderServiceImpl.CustomStandingStruct(
                        t.getStand().getStanding().getSectorId(),
                        t.getPerformance().getPerformanceName(),
                        t.getStand().getStanding().getPrice()
                    );
                    standingTickets.add(addStandingSector);
                }

                continue;
            }

            Cell quantity = createCell("1");
            quantity.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
            quantity.setPaddingLeft(5);

            Cell performance = createCell(t.getPerformance().getPerformanceName());
            performance.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
            performance.setPaddingLeft(5);

            Cell sector;
            Cell rowId;
            Cell seatId;
            Cell price;

            Seat checkForSeat = t.getSeat();
            if (checkForSeat == null) {
                sector = createCell(" - ");
                sector.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                sector.setPaddingLeft(5);

                rowId = createCell(" - ");
                rowId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                rowId.setPaddingLeft(5);

                seatId = createCell(" - ");
                seatId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                seatId.setPaddingLeft(5);

                price = createCell(" - ");
                price.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                price.setPaddingLeft(5);
            } else {
                sector = createCell("Sector " + checkForSeat.getRow().getSeating().getSectorId());
                sector.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                sector.setPaddingLeft(5);

                rowId = createCell(checkForSeat.getRow().getRowNumber() + "");
                rowId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                rowId.setPaddingLeft(5);

                seatId = createCell(checkForSeat.getSeatId() + "");
                seatId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                seatId.setPaddingLeft(5);

                price = createCell("-" + t.getSeat().getRow().getSeating().getPrice() + " €");
                price.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                price.setPaddingLeft(5);

            }

            refundOverview.addCell(quantity).addCell(performance).addCell(sector).addCell(rowId).addCell(seatId).addCell(price);
        }

        if (standingTickets.size() > 0) {

            for (OrderServiceImpl.CustomStandingStruct c : standingTickets) {
                Cell quantity = createCell(c.ticketsInThisSector + "");
                quantity.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                quantity.setPaddingLeft(5);

                Cell performance = createCell(c.performanceName);
                performance.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                performance.setPaddingLeft(5);

                Cell sector = createCell("Sector " + c.sectorId);
                sector.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                sector.setPaddingLeft(5);

                Cell rowId = createCell(" - ");
                rowId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                rowId.setPaddingLeft(5);

                Cell seatId = createCell(" - ");
                seatId.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                seatId.setPaddingLeft(5);

                Cell price = createCell("-" + c.totalPrice + " €");
                price.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
                price.setPaddingLeft(5);

                refundOverview.addCell(quantity).addCell(performance).addCell(sector).addCell(rowId).addCell(seatId).addCell(price);
            }
        }

        Cell placeholder1 = createCellWithMargin("VAT included");
        refundOverview.addCell(placeholder1);
        Cell placeholder2 = createCellWithMargin("");
        refundOverview.addCell(placeholder2);
        Cell placeholder3 = createCellWithMargin("");
        refundOverview.addCell(placeholder3);
        Cell placeholder4 = createCellWithMargin("");
        refundOverview.addCell(placeholder4);
        Cell placeholder5 = createCellWithMargin("Total refund: ");
        placeholder3.setPaddingLeft(5);
        refundOverview.addCell(placeholder5);

        Cell placeholder6 = createCellWithMargin("" + sum + " €");
        placeholder4.setPaddingLeft(5);
        refundOverview.addCell(placeholder6);

        document.add(refundOverview);
    }




    public static class CustomSplitCharacters extends DefaultSplitCharacters {

        @Override
        public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
            if (!text.get(glyphPos).hasValidUnicode()) {
                return false;
            }
            boolean baseResult = super.isSplitCharacter(text, glyphPos);
            boolean myResult = false;
            Glyph glyph = text.get(glyphPos);
            if (((glyph.getUnicode() >= 65) && (glyph.getUnicode() <= 90)) || ((glyph.getUnicode() >= 97) && (glyph.getUnicode() <= 122))) {
                myResult = true;
            }
            if (glyph.getUnicode() == '_') {
                myResult = true;
            }
            return myResult || baseResult;
        }
    }

}
