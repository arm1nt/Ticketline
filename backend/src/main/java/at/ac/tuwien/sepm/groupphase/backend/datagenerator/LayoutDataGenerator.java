package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventHallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GeometryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventHallService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Profile("generateData")
@Component("LayoutDataGenerator")
@DependsOn({"LocationDataGenerator"})
public class LayoutDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_STANDINGS_TO_GENERATE = 1;
    private static final int NUMBER_OF_SEATINGS_TO_GENERATE = 2;
    private static final int NUMBER_OF_SEATS_PER_ROW = 12;
    private final LayoutRepository layoutRepository;
    private final EventHallRepository eventHallRepository;
    private final SectorRepository sectorRepository;
    private final StandRepository standRepository;
    private final RowRepository rowRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final GeometryRepository geometryRepository;
    private final OrderRepository orderRepository;
    private final PerformanceRepository performanceRepository;
    private final LocationRepository locationRepository;
    private final EventHallService eventHallService;
    private final ObjectMapper objectMapper;
    private Location location = null;

    private final List<Layout> generatedLayouts;

    public LayoutDataGenerator(LayoutRepository layoutRepository, EventHallRepository eventHallRepository,
                               SectorRepository sectorRepository, StandRepository standRepository,
                               RowRepository rowRepository, SeatRepository seatRepository,
                               TicketRepository ticketRepository, GeometryRepository geometryRepository,
                               OrderRepository orderRepository, PerformanceRepository performanceRepository,
                               LocationRepository locationRepository, ObjectMapper objectMapper,
                               EventHallService eventHallService) {
        this.layoutRepository = layoutRepository;
        this.eventHallRepository = eventHallRepository;
        this.sectorRepository = sectorRepository;
        this.standRepository = standRepository;
        this.rowRepository = rowRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.geometryRepository = geometryRepository;
        this.orderRepository = orderRepository;
        this.performanceRepository = performanceRepository;
        this.locationRepository = locationRepository;
        this.objectMapper= objectMapper;
        this.eventHallService = eventHallService;
        this.generatedLayouts = new ArrayList<>();
        this.location = Location.builder()
            .name("Super Location")
            .country("Austria")
            .city("Vienna")
            .street("Super Street")
            .zipCode("1120")
            .build();
        locationRepository.save(location);
    }

    public List<Layout> getGeneratedLayouts() {
        return generatedLayouts;
    }

    @PostConstruct
    public long generateLayout() {
        Gson gson = new Gson();

        if (layoutRepository.getLayoutById(1) == null) {
            /* all repositories which data will be generated in dependence on the generated layout are cleared here */
            orderRepository.deleteAll();
            ticketRepository.deleteAll();
            performanceRepository.deleteAll();
            layoutRepository.deleteAll();
        }

        if (layoutRepository.findAll().size() > 0) {
            LOGGER.debug("layout already generated");
            return -1;
        } else {

            try {
                Type createEventhallDtoListType = new TypeToken<List<CreateEventHallDto>>(){}.getType();
                List<CreateEventHallDto> eventHallDtoList = gson.fromJson(new FileReader(
                    "src/main/java/at/ac/tuwien/sepm/groupphase/backend/datagenerator/jsons/layouts.json"),
                    createEventhallDtoListType);
                Iterator<CreateEventHallDto> createEventHallDtoIterator = eventHallDtoList.iterator();

                while (createEventHallDtoIterator.hasNext()) {
                    CreateEventHallDto eventhall = createEventHallDtoIterator.next();
                    this.eventHallService.createEventHall(eventhall);
                }
            } catch (FileNotFoundException e) {
                LOGGER.warn("Could not find the file to parse the into an object in the absolut path " + Paths.get("").toAbsolutePath());
            }

            EventHall eventHall = generateEventHall();
            return generateLayouts(eventHall);
        }
    }


    public long generateCustomLayout() {
        EventHall eventHall = generateEventHall();
        return generateLayouts(eventHall);
    }

    private EventHall generateEventHall() {
        LOGGER.debug("generating {} event hall geometry entries", 1);
        RectangleGeometry geometry = RectangleGeometry.rectangleBuilder()
            .width(560)
            .height(560)
            .build();
        LOGGER.debug("saving event hall geometry {}", geometry);
        geometryRepository.save(geometry);
        LOGGER.debug("generating {} event hall entries", 1);
        Location location2 = Location.builder()
            .name("Super Location")
            .country("Austria")
            .city("Vienna")
            .street("Super Street")
            .zipCode("1120")
            .build();
        //locationRepository.save(location);
        if (this.location == null) {
            this.location = Location.builder()
                .name("Super Location")
                .country("Austria")
                .city("Vienna")
                .street("Super Street")
                .zipCode("1120")
                .build();
            locationRepository.save(location);
        }
        Location stadion = Location.builder()
            .name("Ernst-Happel Stadion")
            .country("Austria")
            .city("Vienna")
            .street("Meireistraße 7")
            .zipCode("1020")
            .build();

        EventHall stadionMainStage = EventHall.builder()
            .name("Main Stage")
            .geometry(geometry)
            .location(stadion)
            .build();

        EventHall eventHall = EventHall.builder()
            .name("Super hall")
            .geometry(geometry)
            .location(this.location)
            .build();
        LOGGER.debug("saving event hall {}", eventHall);
        eventHallRepository.save(eventHall);
        return eventHall;
    }

    private long generateLayouts(EventHall eventHall) {
        LOGGER.debug("generating layout entries");
        Layout layout = generateLayout(eventHall);
        generateStandings(layout);
        generateSeatings(layout);
        return layout.getId();
    }


    private Layout generateLayout(EventHall eventHall) {
        Layout layout = Layout.builder()
            .eventHall(eventHall)
            .name("Super Jazz Layout")
            .build();
        LOGGER.debug("saving layout {}", layout);
        layout = layoutRepository.save(layout);
        this.generatedLayouts.add(layout);
        return layout;
    }

    private void generateStandings(Layout layout) {
        LOGGER.debug("generating {} standing entries", NUMBER_OF_STANDINGS_TO_GENERATE);
        for (int j = 0; j < NUMBER_OF_STANDINGS_TO_GENERATE; j++) {
            LOGGER.debug("generating {} standing geometry entries", 1);
            RectangleGeometry geometry = RectangleGeometry.rectangleBuilder()
                .x(10)
                .y(90)
                .width(535)
                .height(100)
                .build();
            LOGGER.debug("saving standing geometry {}", geometry);
            geometryRepository.save(geometry);
            Standing standing = Standing.builder()
                .sectorId(j + "")
                .price(100.0)
                .capacity(100)
                .color("lightblue")
                .geometry(generateRectangleGeometry(10, 90, 535, 100))
                .layout(layout)
                .build();
            LOGGER.debug("saving standing {}", standing);
            sectorRepository.save(standing);
            generateStands(standing);
            LOGGER.debug("updating standing {}", standing);
            sectorRepository.save(standing);
        }
    }

    private void generateStands(Standing standing) {
        Ticket ticket;
        Stand stand;
        LOGGER.debug("generating {} stand entries", standing.getCapacity());
        for (int k = 0; k < standing.getCapacity(); k++) {
            stand = Stand.builder()
                .standing(standing)
                .build();
            standing.getStands().add(stand);
            LOGGER.debug("saving stand {}", stand);
            standRepository.save(stand);
        }
    }

    private void generateSeatings(Layout layout) {
        LOGGER.debug("generating {} seating entries", NUMBER_OF_SEATINGS_TO_GENERATE);
        for (int j = 0; j < NUMBER_OF_SEATINGS_TO_GENERATE; j++) {
            Seating seating = Seating.builder()
                .sectorId((j + 1) + "")
                .price(100.0)
                .geometry(generateRectangleGeometry(10, 200 + 230 * j, 535, 120 + (1 - j) * 100))
                .color(j == 0 ? "lightgreen" : "lightyellow")
                .layout(layout)
                .build();
            LOGGER.debug("saving standing {}", seating);
            sectorRepository.save(seating);
            generateRows(220 + 230 * j, j == 0 ? 4 : 2, j * 4, seating);
            LOGGER.debug("updating seating {}", seating);
            sectorRepository.save(seating);
        }
    }

    private void generateRows(double startingPosition, int amountOfRows, int offset, Seating seating) {
        LOGGER.debug("generating {} row entries", amountOfRows);
        Row row;
        for (int i = 1; i <= amountOfRows; i++) {
            row = generateRow(i + offset, seating, 20, startingPosition + (i - 1) * 50);
            for (int j = 1; j <= NUMBER_OF_SEATS_PER_ROW; j++) {
                generateSeat(j + "", row, 40 + (j - 1) * 40, startingPosition + (i - 1) * 50);
            }
        }
    }

    private Row generateRow(int rowNumber, Seating seating, double x, double y) {
        Row row = Row.builder()
            .rowNumber(rowNumber)
            .seating(seating)
            .geometry(generateGeometry(x, y))
            .build();
        LOGGER.debug("saving row {}", row);
        rowRepository.save(row);
        return row;
    }

    private Seat generateSeat(String seatId, Row row, double x, double y) {
        SeatGeometry seatGeometry = generateStandardSeatAtPosition(x, y);
        LOGGER.debug("generating seat entry");
        Seat seat = Seat.builder()
            .seatId(seatId)
            .geometry(seatGeometry)
            .row(row)
            .build();
        LOGGER.debug("saving seat {}", seat.getId());
        seatRepository.save(seat);
        return seat;
    }

    private SeatGeometry generateStandardSeatAtPosition(double x, double y) {
        LOGGER.debug("generating standard seat geometry entry");
        SeatGeometry seatGeometry = SeatGeometry.seatBuilder()
            .x(x)
            .y(y)
            .width(30)
            .height(30)
            .legSpaceDepth(12)
            .build();
        LOGGER.debug("saving standing geometry {}", seatGeometry);
        geometryRepository.save(seatGeometry);
        return seatGeometry;
    }

    private Geometry generateGeometry(double x, double y) {
        LOGGER.debug("generating geometry entry");
        Geometry geometry = Geometry.builder()
            .x(x)
            .y(y)
            .build();
        LOGGER.debug("saving geometry {}", geometry);
        geometryRepository.save(geometry);
        return geometry;
    }

    private RectangleGeometry generateRectangleGeometry(double x, double y, double width, double height) {
        LOGGER.debug("generating rectangle geometry entry");
        RectangleGeometry geometry = RectangleGeometry.rectangleBuilder()
            .x(x)
            .y(y)
            .width(width)
            .height(height)
            .build();
        LOGGER.debug("saving rectangle geometry {}", geometry);
        geometryRepository.save(geometry);
        return geometry;
    }
}
