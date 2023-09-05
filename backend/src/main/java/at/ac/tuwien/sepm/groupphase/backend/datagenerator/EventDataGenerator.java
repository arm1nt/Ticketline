package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Profile("generateData")
@DependsOn("PerformerDataGenerator")
@Component("EventDataGenerator")
public class EventDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PerformerDataGenerator performerDataGenerator;
    private final EventRepository eventRepository;
    private final ArtistRepository artistRepository;
    private final List<Event> generatedEvents;
    private Event harryStylesTour;
    private Event taylorSwiftTour;
    private Event eltonJohnTour;

    private static final int NUMBER_OF_REAL_EVENTS_TO_GENERATE = 3;
    private static final int NUMBER_OF_SAMPLE_EVENTS_TO_GENERATE = 250;
    private static final int NUMBER_OF_PERFORMERS_PER_EVENT = 3;

    private final String[] eventNames;

    public EventDataGenerator(PerformerDataGenerator performerDataGenerator, EventRepository eventRepository, ArtistRepository artistRepository) {
        this.performerDataGenerator = performerDataGenerator;
        this.eventRepository = eventRepository;
        this.artistRepository = artistRepository;
        this.generatedEvents = new ArrayList<>();

        this.eventNames = new String[]{"Legends Tour", "Vienna Tour", "Rock Festival", "Music Mania", "Metal Head"};
    }

    public List<Event> getGeneratedEvents() {
        return generatedEvents;
    }

    @PostConstruct
    public void generatePerformers() {
        if (eventRepository.findAll().size() > 0) {
            LOGGER.debug("Events already exist");
        } else {
            LOGGER.debug("Generating {} events", NUMBER_OF_SAMPLE_EVENTS_TO_GENERATE);
            String eventName = "";
            for (int i = 0; i < NUMBER_OF_SAMPLE_EVENTS_TO_GENERATE; i++) {
                if(i < 50) {
                    eventName = eventNames[0]+i;
                } else if(i < 100) {
                    eventName = eventNames[1]+i;
                } else if(i < 150) {
                    eventName = eventNames[2]+i;
                } else if(i < 200) {
                    eventName = eventNames[3]+i;
                } else {
                    eventName = eventNames[4]+i;
                }
                Event event = Event.builder()
                    .name(eventName)
                    .eventType(getEventType(i))
                    .performers(addPerformers())
                    .duration(45 * ( (i % 4) + 1))
                    .build();
                eventRepository.save(event);
                this.generatedEvents.add(event);
            }
            Event harryStylesEvent = Event.builder()
                .name("Harry Styles Tour")
                .eventType(EventType.CONCERT)
                .performers(this.performerDataGenerator.getHarryStylesTourPerformers())
                .duration(75)
                .build();
            eventRepository.save(harryStylesEvent);
            harryStylesTour = harryStylesEvent;

            Event taylorSwiftEvent = Event.builder()
                .name("Taylor Swift Tour")
                .eventType(EventType.CONCERT)
                .performers(this.performerDataGenerator.getTaylorSwiftTourPerformers())
                .duration(120)
                .build();
            eventRepository.save(taylorSwiftEvent);
            taylorSwiftTour = taylorSwiftEvent;

            Event eltonJohnEvent = Event.builder()
                .name("Elton John Tour")
                .eventType(EventType.CONCERT)
                .performers(this.performerDataGenerator.getEltonJohnTourPerformers())
                .duration(150)
                .build();
            eventRepository.save(eltonJohnEvent);
            eltonJohnTour = eltonJohnEvent;
        }


    }

    private EventType getEventType(int eventType){
        int select = eventType%EventType.values().length;
        if(select == 0){
            return EventType.CONCERT;
        } else if(select == 1){
            return EventType.BALL;
        } else if(select == 2){
            return EventType.OPERA;
        } else {
            return EventType.FESTIVAL;
        }
    }

    public Event getHarryStylesTour(){
        return harryStylesTour;
    }

    public Event getTaylorSwiftTour(){
        return taylorSwiftTour;
    }

    public Event getEltonJohnTour() {
        return eltonJohnTour;
    }

    private List<Performer> addPerformers(){
        List<Performer> performers = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PERFORMERS_PER_EVENT; i++){
            if (this.performerDataGenerator.getGeneratedPerformers().size() > 0){
                performers.add(performerDataGenerator.getRandomPerformer());
            }
        }
        return performers;
    }
}
