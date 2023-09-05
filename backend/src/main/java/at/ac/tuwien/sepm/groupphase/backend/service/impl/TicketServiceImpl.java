package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stand;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketPdfRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.splitting.DefaultSplitCharacters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import static at.ac.tuwien.sepm.groupphase.backend.service.impl.OrderServiceImpl.SECRET_QR_CODE_GENERATOR_KEY;


@Service
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketRepository ticketRepository;
    private final TicketPdfRepository ticketPdfRepository;
    private final UserRepository userRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository,
                             TicketPdfRepository ticketPdfRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketPdfRepository = ticketPdfRepository;
    }


    @Override
    public Set<Ticket> getAllByPerformanceId(Long id) throws NotFoundException {
        LOGGER.trace("getAllByPerformanceId({})", id);

        return ticketRepository.getTicketsByPerformanceId(id);
    }



    @Override
    public String getTicketPdf(long id, String username) throws IOException, ForbiddenException, NotFoundException {
        LOGGER.trace("getTicketPdf({}, {})", id, username);

        Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (ticketOptional.isEmpty()) {
            throw new NotFoundException(String.format("Ticket with id %d does not exist", id));
        }
        Ticket ticket = ticketOptional.get();
        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);

        if (!applicationUser.isAdmin() && ticket.getApplicationUser() == null) {
            throw new ForbiddenException("Ticket does not belong to the user requesting it");
        }

        if (!applicationUser.isAdmin() && !applicationUser.getUsername().equals(ticket.getApplicationUser().getUsername())) {
            throw new ForbiddenException("Ticket does not belong to the user requesting it");
        }

        return ticket.getTicketPdf().getTicketPdf();
    }


    @Override
    public String createPdfOfTicket(long id) throws IOException {
        LOGGER.trace("createPdfOfTicket({})", id);

        Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (ticketOptional.isEmpty()) {
            throw new NotFoundException(String.format("Ticket with id %d does not exist", id));
        }
        Ticket ticket = ticketOptional.get();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);

        PdfDocument pdf = new PdfDocument(writer);

        Rectangle ticketFormat = new Rectangle(600, 250);
        Document doc = new Document(pdf, new PageSize(ticketFormat));

        doc.setMargins(1f, 10f, 1f, 20f);
        PdfCanvas canvas = new PdfCanvas(pdf.addNewPage());

        String path = TicketServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        int i = path.indexOf("backend/");
        String absolutePath = path.substring(0, i + "backend/".length());
        absolutePath += "/src/main/resources/pdf/TicketBackground.jpg";
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(absolutePath), ticketFormat, false);

        //Used to split the whole ticket in half (horizontally) so we can have the text on the left side
        //and the qr code on the right side.
        Table wholeTicket = new Table(2);
        wholeTicket.setWidth(UnitValue.createPercentValue(100));
        wholeTicket.setHeight(UnitValue.createPointValue(250f));

        Cell information = new Cell();
        information.setWidth(UnitValue.createPercentValue(80));
        information.setBorder(Border.NO_BORDER);

        //This table splits the information cell in a top and a bottom half
        Table splitInformation = new Table(1);
        splitInformation.setWidth(UnitValue.createPercentValue(100));
        splitInformation.setHeight(UnitValue.createPointValue(250f));

        Cell eventInformationCell = new Cell();
        eventInformationCell.setWidth(UnitValue.createPercentValue(100));
        eventInformationCell.setHeight(UnitValue.createPercentValue(70));
        eventInformationCell.setBorder(Border.NO_BORDER);

        //This table splits the cell 'cellInformationCell' into a top and a bottom half
        //We do this so we can separate the performance name and the eventhall name from the location and time
        Table tableForInformationCell = new Table(1);
        tableForInformationCell.setWidth(UnitValue.createPercentValue(100));
        tableForInformationCell.setHeight(UnitValue.createPercentValue(100));

        Cell eventHallInformationCell = new Cell();
        eventHallInformationCell.setWidth(UnitValue.createPercentValue(100));
        eventHallInformationCell.setHeight(UnitValue.createPercentValue(60));
        eventHallInformationCell.setVerticalAlignment(VerticalAlignment.TOP);
        eventHallInformationCell.setBorder(Border.NO_BORDER);

        String performanceName = ticket.getPerformance().getPerformanceName();
        Paragraph performance = new Paragraph(performanceName);
        performance.setMarginTop(5f);
        eventHallInformationCell.add(addStylingBold(performance, calculateFontSize(performanceName.length(), 0)));

        String eventhallName = ticket.getPerformance().getLayout().getEventHall().getName();
        float eventHallFontSize = 18f;
        if ( calculateFontSize(eventhallName.length(), 0) > calculateFontSize(performanceName.length(), 0) ) {
            eventHallFontSize = calculateFontSize(performanceName.length(), 5);
        } else {
            if (Math.abs(calculateFontSize(eventhallName.length(), 0) - calculateFontSize(performanceName.length(), 0)) < 5) {
                eventHallFontSize = calculateFontSize(performanceName.length(), 5);
            } else {
                eventHallFontSize = calculateFontSize(eventhallName.length(), 0);
            }
        }

        Paragraph eventhall = new Paragraph("AT " + eventhallName);
        eventHallInformationCell.add(addStylingBold(eventhall, eventHallFontSize));

        Cell eventHallOrgaInformationCell = new Cell();
        eventHallOrgaInformationCell.setWidth(UnitValue.createPercentValue(100));
        eventHallOrgaInformationCell.setHeight(UnitValue.createPercentValue(50));
        eventHallOrgaInformationCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        eventHallOrgaInformationCell.setBorder(Border.NO_BORDER);

        Location location = ticket.getPerformance().getLayout().getEventHall().getLocation();
        String locationString = location.getStreet()
            + " - " + location.getZipCode()
            + " - " + location.getCity()
            + " - " + location.getCountry();
        Paragraph locationInfo = new Paragraph(locationString);
        eventHallOrgaInformationCell.add(addStylingBold(locationInfo, 12f));

        LocalDateTime startTime = ticket.getPerformance().getStartTime();
        String[] splitInDateAndTime = startTime.toString().split("T");
        String[] dateParts = splitInDateAndTime[0].split("-");
        String[] timeParts = splitInDateAndTime[1].split(":");
        String timeDateString = dateParts[2] + "." + dateParts[1] + "." + dateParts[0]
            + " - " + timeParts[0] + ":" + timeParts[1];
        Paragraph dateInfo = new Paragraph(timeDateString);
        eventHallOrgaInformationCell.add(addStylingBold(dateInfo, 12f));


        tableForInformationCell.addCell(eventHallInformationCell).addCell(eventHallOrgaInformationCell);
        eventInformationCell.add(tableForInformationCell);


        //contains information about the bought spot: sector, row, place number
        Cell spotInformationCell = new Cell();
        spotInformationCell.setWidth(UnitValue.createPercentValue(100));
        spotInformationCell.setBorder(Border.NO_BORDER);

        if(ticket.getSeat() != null) {
            Seat seat = ticket.getSeat();

            Table seatingInformation = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();

            Cell cell1 = new Cell().add(addStylingBold(new Paragraph("Sector"), 15f));
            cell1.setBorder(Border.NO_BORDER);
            cell1.setMaxHeight(UnitValue.createPointValue(20f));

            Cell cell2 = new Cell().add(addStylingBold(new Paragraph("Row"), 15f));
            cell2.setBorder(Border.NO_BORDER);
            cell2.setMaxHeight(UnitValue.createPointValue(20f));

            Cell cell3 = new Cell().add(addStylingBold(new Paragraph("Seat"), 15f));
            cell3.setBorder(Border.NO_BORDER);
            cell3.setMaxHeight(UnitValue.createPointValue(20f));

            Cell sector = new Cell().add(addStylingBold( new Paragraph(seat.getRow().getSeating().getSectorId()), 14f));
            sector.setBorder(Border.NO_BORDER);
            sector.setMaxHeight(UnitValue.createPointValue(20f));

            Cell row = new Cell().add(addStylingBold(new Paragraph(seat.getRow().getRowNumber() + ""), 14f));
            row.setBorder(Border.NO_BORDER);
            row.setMaxHeight(UnitValue.createPointValue(20f));

            Cell place = new Cell().add(addStylingBold( new Paragraph(seat.getSeatId()), 14f));
            place.setBorder(Border.NO_BORDER);
            place.setMaxHeight(UnitValue.createPointValue(20));

            seatingInformation.addCell(cell1).addCell(cell2).addCell(cell3).addCell(sector).addCell(row).addCell(place);
            spotInformationCell.add(seatingInformation);

        } else {
            Stand stand = ticket.getStand();

            Table standingInformation = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

            Cell cell1 = new Cell().add(addStylingBold(new Paragraph("Sector"), 15f));
            cell1.setBorder(Border.NO_BORDER);
            cell1.setMaxHeight(UnitValue.createPointValue(20f));

            Cell cell2 = new Cell().add(addStylingBold(new Paragraph("Place"), 15f));
            cell2.setBorder(Border.NO_BORDER);
            cell2.setMaxHeight(UnitValue.createPointValue(20f));

            Cell sector = new Cell().add(addStylingBold( new Paragraph(stand.getStanding().getSectorId()), 14f));
            sector.setBorder(Border.NO_BORDER);
            sector.setMaxHeight(UnitValue.createPointValue(20f));

            Cell place = new Cell().add(addStyling( new Paragraph("Standing"), 14f));
            place.setBorder(Border.NO_BORDER);
            place.setMaxHeight(UnitValue.createPointValue(20));

            standingInformation.addCell(cell1).addCell(cell2).addCell(sector).addCell(place);
            spotInformationCell.add(standingInformation);
        }


        splitInformation.addCell(eventInformationCell).addCell(spotInformationCell);
        information.add(splitInformation);

        Cell qrCodeCell = new Cell();
        qrCodeCell.setBorder(Border.NO_BORDER);

        //Warning: No functional use, only for visuals
        String encryptedText = null;
        try {
            Key key = new SecretKeySpec(SECRET_QR_CODE_GENERATOR_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            String toEncrypt = "" + ticket.getTicketId();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedText = Base64.getEncoder().encodeToString(cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IOException("There was an error creating your ticket");
        }

        BarcodeQRCode barcode = new BarcodeQRCode(encryptedText);
        PdfFormXObject qrCodeObject = barcode.createFormXObject(ColorConstants.WHITE, pdf);
        Image barcodeAsImage = new Image(qrCodeObject)
            .setWidth(100).setHeight(100).setHorizontalAlignment(HorizontalAlignment.RIGHT);
        barcodeAsImage.setMarginLeft(5f);
        qrCodeCell.add(barcodeAsImage);


        wholeTicket.addCell(information).addCell(qrCodeCell);
        doc.add(wholeTicket);

        doc.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return new String(Base64.getEncoder().encode(bytes));
    }


    private static Paragraph addStyling(Paragraph p, float fontsize) throws IOException {
        LOGGER.trace("addStyling()");

        p.setFontColor(ColorConstants.WHITE);
        p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
        p.setFontSize(fontsize);
        p.setProperty(Property.SPLIT_CHARACTERS, new CustomSplitCharacters());
        return p;
    }

    private static Paragraph addStylingBold(Paragraph p, float fontsize) throws IOException {
        LOGGER.trace("addStylingBold()");

        p.setFontColor(ColorConstants.WHITE);
        p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
        p.setFontSize(fontsize);
        p.setProperty(Property.SPLIT_CHARACTERS, new CustomSplitCharacters());
        return p;
    }

    private static float calculateFontSize(int length, int offset) {
        LOGGER.trace("calculateFontSize({}, {})", length, offset);

        if (length < 0) {
            return 0;
        }

        if (length < 39) {
            return 25f - offset;
        }

        float beforeOffset =  (float)  (25 - ((Math.pow(length, 1.13) * 0.1)));
        return beforeOffset - offset;
    }

    private static class CustomSplitCharacters extends DefaultSplitCharacters {

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
