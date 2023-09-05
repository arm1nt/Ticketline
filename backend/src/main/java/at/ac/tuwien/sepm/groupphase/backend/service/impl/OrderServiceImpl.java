package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.CancellationInvoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.Order;
import at.ac.tuwien.sepm.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketPdf;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.IllegalOperationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CancellationInvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketPdfRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static at.ac.tuwien.sepm.groupphase.backend.service.impl.InvoicePdfCreation.*;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    protected static final String SECRET_QR_CODE_GENERATOR_KEY = "Xui85381Axs62315";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String INVOICE_TITLE = "INVOICE";
    private static final String REFUND_TITLE = "REFUND";
    private static final String COMPANY_NAME = "Ticketline";
    protected static final String COMPANY_UID = "ATU67769454";
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final CancellationInvoiceRepository cancellationInvoiceRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final OrderMapper orderMapper;

    private final TicketMapper ticketMapper;
    private final EventRepository eventRepository;
    private final PerformanceRepository performanceRepository;
    private final TicketService ticketService;
    private final UserService userService;
    private final TicketPdfRepository ticketPdfRepository;

    public OrderServiceImpl(OrderRepository orderRepository, InvoiceRepository invoiceRepository,
                            UserRepository userRepository, TicketRepository ticketRepository,
                            CancellationInvoiceRepository cancellationInvoiceRepository,
                            OrderMapper orderMapper, EventRepository eventRepository,
                            PerformanceRepository performanceRepository,
                            TicketService ticketService, UserService userService,
                            TicketMapper ticketMapper, TicketPdfRepository ticketPdfRepository) {
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.cancellationInvoiceRepository = cancellationInvoiceRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.orderMapper = orderMapper;
        this.eventRepository = eventRepository;
        this.performanceRepository = performanceRepository;
        this.ticketService = ticketService;
        this.userService = userService;
        this.ticketMapper = ticketMapper;
        this.ticketPdfRepository = ticketPdfRepository;

    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderOverviewDto> getAllOrdersForUser(String username) {
        LOGGER.trace("getAllOrdersForUser({})", username);

        var orders = orderRepository.findByApplicationUser_UsernameOrderByCreationDateTimeDesc(username);
        var performanceIds = orders.stream().map(Order::getPerformanceId).toList();
        var performances = performanceRepository.findByIdIn(performanceIds).stream().collect(Collectors.toUnmodifiableMap(Performance::getId, Function.identity()));
        return this.orderMapper.OrdersEventsPerformancesToOrderOverviewDto(orders, performances);
    }


    @Override
    @Transactional(readOnly = true)
    public Order getById(Long id, ApplicationUser applicationUser) throws NotFoundException, ForbiddenException {
        LOGGER.trace("getById({}, {})", id, applicationUser);

        Order order = orderRepository.getOrderById(id);

        if (order == null) {
            throw new NotFoundException("This order does not exist");
        }

        if (!applicationUser.isAdmin() && !order.getApplicationUser().getUsername().equals(applicationUser.getUsername())) {
            throw new ForbiddenException("Order belongs to other user");
        }

        return order;
    }


    @Override
    @Transactional(readOnly = true)
    public String getCancellation(String username, long id) {
        LOGGER.trace("getCancellation({}, {})", username, id);

        Order order = orderRepository.getOrderById(id);
        if (order == null) {
            throw new NotFoundException("There exists no cancellation by this id");
        }

        if (order.getDiscriminatorValue().equals("Reservation")) {
            throw new ConflictException("There exists no such cancellation, as this id does not reference a previously paid order");
        }

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
        if (!applicationUser.isAdmin() && !order.getApplicationUser().getUsername().equals(username)) {
            throw new ForbiddenException("This cancellation does not belong to the user requesting it");
        }

        Payment payment = (Payment) order;
        return payment.getCancellation().getCancellationPdf();
    }


    @Override
    @Transactional(readOnly = true)
    public String getInvoice(String username, long id) {
        LOGGER.trace("getInvoice({}, {})", username, id);

        Order order = orderRepository.getOrderById(id);
        if (order == null) {
            throw new NotFoundException("There exists no order by this id");
        }

        if (order.getDiscriminatorValue().equals("Reservation")) {
            throw new ConflictException("There exists no such invoice, as this id does not reference an paid order");
        }

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
        if (!applicationUser.isAdmin() && !order.getApplicationUser().getUsername().equals(username)) {
            throw new ForbiddenException("This invoice does not belong to the user requesting it");
        }

        Payment payment = (Payment) order;

        if (payment.getInvoice() == null) {
            throw new ConflictException("There is no invoice set for this order");
        }

        if (payment.getInvoice().getPdf() == null) {
            throw new NotFoundException("The requested invoice was not found.");
        }

        return payment.getInvoice().getPdf();
    }


    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void cancelReservedTicketsAtDueTime() {
        LOGGER.trace("cancelReservedTicketsAtDueTime()");

        List<Order> orders = this.orderRepository.findAll();
        for (Order o : orders) {
            if (o.getDiscriminatorValue().equals("Reservation")) {
                Reservation reservation = (Reservation) o;

                if (reservation.getDueTime() == null) {
                    continue;
                }

                if (reservation.getDueTime().isBefore(LocalDateTime.now())) {

                    Set<Ticket> ticketsToCancel = new HashSet<>();
                    for (Ticket t : reservation.getTickets()) {
                        Ticket addTicket = Ticket.builder()
                            .id(t.getId())
                            .ticketId(t.getTicketId())
                            .ticketStatus(TicketStatus.CANCELED)
                            .build();
                        ticketsToCancel.add(addTicket);
                    }

                    Reservation reservation1 = Reservation.builder()
                        .id(reservation.getId())
                        .reservationCode(reservation.getReservationCode())
                        .dueTime(reservation.getDueTime())
                        .performanceId(reservation.getPerformanceId())
                        .applicationUser(reservation.getApplicationUser())
                        .tickets(ticketsToCancel)
                        .build();

                    try {
                        this.update(reservation1, true);
                    } catch (ObjectOptimisticLockingFailureException e) {
                        LOGGER.debug(String.format("There was an error cancelling order %s as it is currently" +
                            "being changed", reservation.getId()));
                    } catch (IOException e) {
                        LOGGER.debug(String.format("There was an error cancelling order %s", reservation.getId()));
                    }
                }
            }
        }
    }


    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    public Order save(Order order) throws IOException, NoSuchAlgorithmException {
        LOGGER.trace("save({},{})", order, order.getApplicationUser());

        Set<Long> ids = order.getTickets().stream().map(Ticket::getId).collect(Collectors.toSet());
        Set<Ticket> tickets = ticketRepository.getTicketsByIds(ids);

        Performance performance = performanceRepository.getPerformanceById(order.getPerformanceId());

        if (performance == null) {
            throw new NotFoundException("There exists no performance with id " + order.getPerformanceId() + " to which the order with id " + order.getId() + " belongs");
        }

        if (performance.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("The performance referenced by this order is already in the past and tickets aren't sold anymore");
        }

        if (ids.size() != order.getTickets().size()) {
            throw new ConflictException("The same ticket can only be selected once!");
        }

        if (tickets.size() != order.getTickets().size()) {
            throw new NotFoundException("Not all tickets referenced exist");
        }

        TicketStatus status = (order.getClass() == Payment.class) ? TicketStatus.PURCHASED : TicketStatus.RESERVED;
        for (Ticket t : tickets) {
            if (!Objects.equals(t.getPerformance().getId(), performance.getId())) {
                throw new ConflictException("Tickets do not belong to the performance mentioned in the order");
            }
            if (t.getTicketStatus() != TicketStatus.FREE) {
                throw new ConflictException("Some of the selected tickets have already been purchased or reserved.");
            }
            if (status == TicketStatus.PURCHASED) {
                TicketPdf ticketPdf = TicketPdf.builder()
                    .ticketPdf(this.ticketService.createPdfOfTicket(t.getId()))
                    .build();
                this.ticketPdfRepository.save(ticketPdf);

                t.setTicketPdf(ticketPdf);
                t.setApplicationUser(
                    this.userRepository.findApplicationUserByUsernameIgnoreCase(order.getApplicationUser().getUsername()));
                Performance performance1 = t.getPerformance();
                if(performance1 != null){
                    int soldTickets = performance1.getSoldTickets();
                    performance1.setSoldTickets(soldTickets+1);
                    performanceRepository.save(performance1);
                }
            }

            try {
                t.setTicketStatus(status);
                this.ticketRepository.saveAndFlush(t);
            } catch (ObjectOptimisticLockingFailureException e) {
                LOGGER.debug("Same ticket has been tried to be purchased simultaneously");
                throw new ConflictException("Some of the selected tickets have already been purchased or reserved.");
            }
        }

        Order buildOrder;

        if (order.getClass() == Reservation.class) {
            Optional<Performance> p = performanceRepository.findById(order.getPerformanceId());

            if (p.isEmpty()) {
                throw new ConflictException("Could not find referenced performance");
            }

            if ((LocalDateTime.now().isAfter(p.get().getStartTime().minusMinutes(30L)))) {
                throw new ForbiddenException("The performance referenced by this order starts in less then 30 min and tickets can't be reserved");
            }

            buildOrder = Reservation.builder()
                .applicationUser(order.getApplicationUser())
                .dueTime(p.get().getStartTime().minusMinutes(30L))
                .performanceId(order.getPerformanceId())
                .tickets(tickets)
                .reservationCode(calcReservationCode())
                .build();


            buildOrder.setCreationDateTime(LocalDateTime.now());
            try {
                return orderRepository.save(buildOrder);
            } catch (DataIntegrityViolationException e) {
                throw new ConflictException("Some of the selected tickets have already been purchased or reserved.");
            }
        }

        buildOrder = Payment.builder()
            .applicationUser(order.getApplicationUser())
            .time(LocalDateTime.now())
            .total(calcTotal(tickets))
            .performanceId(order.getPerformanceId())
            .tickets(tickets)
            .build();

        buildOrder.setCreationDateTime(LocalDateTime.now());
        Order order1 = orderRepository.save(buildOrder);
        createInvoice(order.getApplicationUser().getUsername(), order1.getId(), ((Payment) buildOrder).getTime(), order1.getTickets());
        try {
            return orderRepository.getOrderById(order1.getId());
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Some of the selected tickets have already been purchased or reserved.");
        }
    }


    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    public Order update(Order order, boolean isRunByScheduledTask)
        throws NotFoundException, ForbiddenException, IOException, ObjectOptimisticLockingFailureException {
        LOGGER.trace("update({},{})", order, order.getApplicationUser());

        Order orderFetched = orderRepository.getOrderById(order.getId());

        if (orderFetched == null) {
            throw new NotFoundException("There exists no order with such id");
        }

        Performance performance = performanceRepository.getPerformanceById(order.getPerformanceId());

        if (performance == null) {
            throw new NotFoundException("There exists no performance with id " + order.getPerformanceId() + " to which the order with id " + order.getId() + " belongs");
        }

        if (!isRunByScheduledTask && performance.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("The performance referenced by this order is already in the past and can't be changed");
        }

        ApplicationUser applicationUser = order.getApplicationUser();
        if (!applicationUser.isAdmin() && !applicationUser.getUsername().equals(orderFetched.getApplicationUser().getUsername())) {
            throw new ForbiddenException("This order can not be updated as it belongs to an other user");
        }

        boolean reservationToPayment = false;
        boolean cancelAllTickets = false;
        boolean cancelSomeTickets = false;

        boolean checkForCancel = false;
        boolean checkForReservationToPayment = false;

        Set<Ticket> currentTickets = orderFetched.getTickets();

        for (Ticket t : order.getTickets()) {
            TicketStatus status = t.getTicketStatus();

            if (status == TicketStatus.CANCELED) {
                checkForCancel = true;
                updateOrderTicketCancellingPreProcessing(t, currentTickets);
            } else if (status == TicketStatus.PURCHASED) {
                checkForReservationToPayment = true;
                updateOrderTicketBuyingPreProcessing(t, currentTickets, status, order);
            } else {
                throw new IllegalOperationException("This operation is not allowed");
            }
        }

        if (checkForCancel && checkForReservationToPayment) {
            throw new IllegalOperationException("Only one operation must be performed at once");
        }

        if (checkForCancel) {
            if (currentTickets.size() > order.getTickets().size()) {
                cancelSomeTickets = true;
            } else {
                cancelAllTickets = true;
            }
        } else if (checkForReservationToPayment) {
            reservationToPayment = true;
        }

        if (reservationToPayment) {

            if (!orderFetched.getDiscriminatorValue().equals("Reservation")) {
                throw new IllegalOperationException("Cant turn reservation to payment as this is not a reservation");
            }
            Order savedOrder = reservationToPaymentForUpdate(order, orderFetched, currentTickets);
            return orderRepository.save(savedOrder);
        }

        if (cancelAllTickets) {

            for (Ticket t : currentTickets) {
                ticketRepository.updateTicket(t.getId(), TicketStatus.FREE);
                Performance performance1 = t.getPerformance();
                if(performance1 != null){
                    int soldTickets = performance1.getSoldTickets();
                    performance1.setSoldTickets(soldTickets-1);
                    performanceRepository.save(performance1);
                }
            }

            if (order.getClass() == Reservation.class) {
                ((Reservation) orderFetched).setDueTime(null);
                ((Reservation) orderFetched).setReservationCode(null);
                Reservation res = (Reservation) orderFetched;
                res.setTickets(new HashSet<>());
                return orderRepository.save(res);
            }
            createCancellationInvoice(orderFetched.getApplicationUser().getUsername(), LocalDateTime.now(), orderFetched.getId(), orderFetched.getTickets());
            Payment orderWithCancellation = (Payment) orderRepository.getOrderById(orderFetched.getId());

            orderWithCancellation.setTime(LocalDateTime.now());
            orderWithCancellation.setTotal(calcTotal(currentTickets));
            orderWithCancellation.setTickets(new HashSet<>());
            return orderRepository.save(orderWithCancellation);
        }

        //cancel some tickets but not all
        Set<Ticket> refundedTickets = ticketRepository.getTicketsByIds(order.getTickets().stream().map(Ticket::getId).collect(Collectors.toUnmodifiableSet()));
        Set<Ticket> notRefundedTickets = new HashSet<>();
        List<Long> refundedTicketIds = refundedTickets.stream().map(Ticket::getId).toList();
        if (orderFetched.getDiscriminatorValue().equals("Payment")) {

            for (Ticket t : currentTickets) {
                if (!refundedTicketIds.contains(t.getId())) {
                    ticketRepository.updateTicket(t.getId(), TicketStatus.PURCHASED);
                    notRefundedTickets.add(t);
                }
            }
            for (Ticket t : refundedTickets) {
                Performance performance1 = t.getPerformance();
                if(performance1 != null){
                    int soldTickets = performance1.getSoldTickets();
                    performance1.setSoldTickets(soldTickets-1);
                    performanceRepository.save(performance1);
                }
            }

            createCancellationInvoice(applicationUser.getUsername(), LocalDateTime.now(), orderFetched.getId(), currentTickets);
            Payment orderWithCancellation = (Payment) orderRepository.getOrderById(orderFetched.getId());
            orderWithCancellation.setTime(LocalDateTime.now());
            orderWithCancellation.setTotal(calcTotal(currentTickets));
            orderWithCancellation.setTickets(new HashSet<>());
            orderRepository.save(orderWithCancellation);

            Payment payment = Payment.builder()
                .time(LocalDateTime.now())
                .tickets(notRefundedTickets)
                .performanceId(orderFetched.getPerformanceId())
                .applicationUser(orderFetched.getApplicationUser())
                .total(calcTotal(notRefundedTickets))
                .build();

            payment.setCreationDateTime(LocalDateTime.now());
            Order savedOrder = orderRepository.save(payment);
            createInvoice(savedOrder.getApplicationUser().getUsername(), savedOrder.getId(), payment.getTime(), savedOrder.getTickets());
            return orderRepository.save(savedOrder);
        }

        if (!isRunByScheduledTask && (LocalDateTime.now().isAfter(performance.getStartTime().minusMinutes(30L)))) {
            throw new ForbiddenException("The performance referenced by this order starts in less then 30 min and tickets can't be reserved");
        }

        for (Ticket t : currentTickets) {
            if (!refundedTicketIds.contains(t.getId())) {
                ticketRepository.updateTicket(t.getId(), TicketStatus.RESERVED);
                notRefundedTickets.add(t);
            }
        }

        Reservation reservation = (Reservation) orderRepository.getOrderById(orderFetched.getId());
        reservation.setTickets(notRefundedTickets);

        return orderRepository.save(reservation);
    }


    public void createInvoice(String username, long orderId, LocalDateTime time, Set<Ticket> ticketList) throws IOException {
        LOGGER.trace("createInvoice({}, {}, {})", username, orderId, time);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream); //create PDF in-memory

        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(PageSize.A4);

        PdfDocumentInfo info = pdf.getDocumentInfo();
        info.setTitle(INVOICE_TITLE);
        info.setAuthor(COMPANY_NAME);
        info.setCreator(COMPANY_NAME);
        info.setKeywords("Invoice, Ticketline, Tickets, Order");
        info.addCreationDate();

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        final PdfFont headlineFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        final PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addCompanyBanner(document);

        addCompanyAddress(document, standardFont);

        Payment currentOrder = (Payment) orderRepository.getOrderById(orderId);

        ApplicationUser applicationUser = this.userRepository.findApplicationUserByUsernameIgnoreCase(username);
        addOrderDetailsInvoice(document, applicationUser, currentOrder.getTime(), orderId);

        orderOverview(document, headlineFont, ticketList, orderId, currentOrder);


        document.add(new Paragraph("QR Code to prove the authenticity of this invoice")
            .setFontSize(12)
            .setMarginTop(30));

        //Important: This QR Code has no functional use, It's only for visuals
        String encryptedText = null;
        try {
            Key key = new SecretKeySpec(SECRET_QR_CODE_GENERATOR_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, key);
            String toEncrypt = "Invoice " + orderId + " of user : " + applicationUser.getUsername()
                + ", firstname: " + applicationUser.getFirstName() + ", lastname: " + applicationUser.getLastName();
            byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());
            encryptedText = Base64.getEncoder().encodeToString(cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IOException("Failed to create qr code");
        }


        BarcodeQRCode myCode = new BarcodeQRCode(encryptedText);
        PdfFormXObject pdfFormxObject = myCode.createFormXObject(ColorConstants.BLACK, pdf);
        Image qrImage = new Image(pdfFormxObject).setWidth(175).setHeight(175).setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(qrImage);


        document.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();

        Invoice invoice = Invoice.builder()
            .username(username)
            .pdf(new String(Base64.getEncoder().encode(bytes)))
            .build();

        invoiceRepository.save(invoice);
        currentOrder.setInvoice(invoice);
        orderRepository.save(currentOrder);
    }


    @Override
    public void createCancellationInvoice(String username, LocalDateTime time, long orderId,
                                          Set<Ticket> ticketList) throws IOException {
        LOGGER.trace("createCancellationInvoice({}, {}, {})", username, time, orderId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream); //create PDF in-memory

        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(PageSize.A4);

        PdfDocumentInfo info = pdf.getDocumentInfo();
        info.setTitle(REFUND_TITLE);
        info.setAuthor(COMPANY_NAME);
        info.setCreator(COMPANY_NAME);
        info.setKeywords("Refund, Cancellation, Ticketline, Tickets");
        info.addCreationDate();

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        final PdfFont headlineFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        final PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addCompanyBanner(document);

        document.add(new Paragraph("Refund")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(20)
            .setFont(headlineFont)
            .setMarginTop(20));

        addCompanyAddress(document, standardFont);

        ApplicationUser applicationUser = this.userRepository.findApplicationUserByUsernameIgnoreCase(username);
        addRefundDetailsCancellation(document, applicationUser, time, orderId);

        addRefundOverview(document, headlineFont, ticketList, orderId);

        document.add(new Paragraph("QR Code to prove the authenticity of this refund")
            .setFontSize(12)
            .setMarginTop(30));

        //Warning: This QR code has no functional use, it's only for visuals
        String encryptedText = null;
        try {
            Key key = new SecretKeySpec(SECRET_QR_CODE_GENERATOR_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, key);
            String toEncrypt = "Refund " + orderId + " of user : " + applicationUser.getUsername()
                + ", firstname: " + applicationUser.getFirstName() + ", lastname: " + applicationUser.getLastName();
            byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());
            encryptedText = Base64.getEncoder().encodeToString(cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IOException("Failed to create qr code");
        }

        BarcodeQRCode myCode = new BarcodeQRCode(encryptedText);
        PdfFormXObject pdfFormxObject = myCode.createFormXObject(ColorConstants.BLACK, pdf);
        Image qrImage = new Image(pdfFormxObject).setWidth(175).setHeight(175).setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(qrImage);

        document.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();

        CancellationInvoice cancellationInvoice = CancellationInvoice.builder()
            .username(username)
            .cancellationPdf(new String(Base64.getEncoder().encode(bytes)))
            .build();

        Payment order = (Payment) orderRepository.getOrderById(orderId);

        cancellationInvoiceRepository.save(cancellationInvoice);
        order.setCancellation(cancellationInvoice);
        orderRepository.save(order);
    }

    private void updateOrderTicketBuyingPreProcessing(
        Ticket t, Set<Ticket> currentTickets, TicketStatus status, Order order) throws
        IllegalOperationException,
        IOException{
        LOGGER.trace("updateOrderTicketCancellingPreProcessing()");

        Ticket inspectedTicket = getTicketById(currentTickets, t.getId());

        if (inspectedTicket == null) {
            throw new IllegalOperationException(String.format("Ticket %s can not be purchased as it is not part of the current order", t.getId()));
        }

        if (inspectedTicket.getTicketStatus() != TicketStatus.RESERVED) {
            throw new IllegalOperationException(String.format("Tickets %s reservation can not be turned into a purchase"
                + "as it is not currently reserved", inspectedTicket.getTicketId()));
        }
        Ticket addInfoToTicket = this.ticketRepository.findById(t.getId()).get();
        addInfoToTicket.setTicketStatus(status);
        TicketPdf ticketPdf = TicketPdf.builder()
            .ticketPdf(this.ticketService.createPdfOfTicket(t.getId()))
            .build();
        this.ticketPdfRepository.save(ticketPdf);
        addInfoToTicket.setTicketPdf(ticketPdf);
        addInfoToTicket.setApplicationUser(
            this.userRepository.findApplicationUserByUsernameIgnoreCase(order.getApplicationUser().getUsername()));
        this.ticketRepository.save(addInfoToTicket);
        ticketRepository.updateTicket(inspectedTicket.getId(), status);
    }

    //Set tickets reserved or bought tickets to free
    private void updateOrderTicketCancellingPreProcessing(Ticket t, Set<Ticket> currentTickets) throws IllegalOperationException {
        LOGGER.trace("updateOrderTicketCancellingPreProcessing()");

        Ticket inspectedTicket = getTicketById(currentTickets, t.getId());

        if (inspectedTicket == null) {
            throw new IllegalOperationException(String.format("Ticket %s can not be cancelled as it is not part of the current order", t.getId()));
        }

        if (inspectedTicket.getTicketStatus() != TicketStatus.RESERVED && inspectedTicket.getTicketStatus() != TicketStatus.PURCHASED) {
            throw new IllegalOperationException(String.format("Ticket %s can not be cancelled", inspectedTicket.getTicketId()));
        }
        inspectedTicket.setTicketStatus(TicketStatus.FREE);
        inspectedTicket.setTicketPdf(null);
        inspectedTicket.setApplicationUser(null);
        ticketRepository.save(inspectedTicket);
    }


    private Order reservationToPaymentForUpdate(Order order, Order orderFetched, Set<Ticket> currentTickets)
        throws IllegalOperationException, IOException {

        Set<Ticket> ticketsToPurchase = ticketRepository.getTicketsByIds(order.getTickets().stream().map(Ticket::getId).collect(Collectors.toUnmodifiableSet()));
        List<Long> idsOfTicketsToPurchase = ticketsToPurchase.stream().map(Ticket::getId).toList();

        orderRepository.deleteById(order.getId());

        Payment payment = Payment.builder()
            .time(LocalDateTime.now())
            .tickets(ticketsToPurchase)
            .performanceId(orderFetched.getPerformanceId())
            .applicationUser(orderFetched.getApplicationUser())
            .total(calcTotal(ticketsToPurchase))
            .build();

        payment.setCreationDateTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(payment);
        createInvoice(savedOrder.getApplicationUser().getUsername(), savedOrder.getId(), payment.getTime(), savedOrder.getTickets());
        updateTicketsForReservationToPayment(currentTickets, idsOfTicketsToPurchase);

        return savedOrder;
    }


    private void updateTicketsForReservationToPayment(Set<Ticket> currentTickets, List<Long> idsOfTicketsToPurchase) {
        for (Ticket t : currentTickets) {
            if (!idsOfTicketsToPurchase.contains(t.getId())) {
                ticketRepository.updateTicket(t.getId(), TicketStatus.FREE);
            }
            Performance performance1 = t.getPerformance();
            if(performance1 != null){
                int soldTickets = performance1.getSoldTickets();
                performance1.setSoldTickets(soldTickets+1);
                performanceRepository.save(performance1);
            }
        }
    }


    private String calcReservationCode() throws NoSuchAlgorithmException {
        LOGGER.trace("calcReservationCode()");

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest((LocalDateTime.now() + "" + this.hashCode()).getBytes(StandardCharsets.UTF_8));

        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        return hexString.substring(0, 6).toUpperCase();
    }


    private Ticket getTicketById(Set<Ticket> tickets, long id) {
        if (tickets == null) {
            return null;
        }

        for (Ticket t : tickets) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }


    private double calcTotal(Set<Ticket> tickets) {
        LOGGER.trace("calcTotal()");

        double total = 0;
        for (Ticket t : tickets) {
            total += t.getPrice();
        }
        return total;
    }

    public static class CustomStandingStruct {
        String sectorId;
        String performanceName;
        int ticketsInThisSector;
        double totalPrice;
        double sectorPrice;

        public CustomStandingStruct(String sectorId, String performanceName, double sectorPrice) {
            this.sectorId = sectorId;
            this.performanceName = performanceName;
            this.ticketsInThisSector = 1;
            this.sectorPrice = sectorPrice;
            this.totalPrice = this.sectorPrice;
        }

        public void addTicketToSector() {
            this.ticketsInThisSector++;
            this.totalPrice = this.totalPrice + this.sectorPrice;
        }
    }

    public static class CustomSeparator implements Comparator<Ticket> {

        @Override
        public int compare(Ticket o1, Ticket o2) {
            if ((o1.getSeat() == null && o1.getStand() == null)
                || (o2.getSeat() == null && o2.getStand() == null)) {
                return 0;
            }

            if (o1.getSeat() != null) {
                if (o2.getSeat() != null) {
                    return o1.getSeat().getRow().getSeating().getSectorId().compareTo(o2.getSeat().getRow().getSeating().getSectorId());
                } else {
                    return o1.getSeat().getRow().getSeating().getSectorId().compareTo(o2.getStand().getStanding().getSectorId());
                }
            } else {
                if (o2.getSeat() != null) {
                    return o1.getStand().getStanding().getSectorId().compareTo(o2.getSeat().getRow().getSeating().getSectorId());
                } else {
                    return o1.getStand().getStanding().getSectorId().compareTo(o2.getStand().getStanding().getSectorId());
                }
            }
        }
    }
}
