package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Band;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performer;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.BandRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformerRepository;
import org.aspectj.weaver.patterns.PerObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Profile("generateData")
@Component("PerformerDataGenerator")
public class PerformerDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ARTISTS_TO_GENERATE = 20;
    private static final int NUMBER_OF_BANDS_TO_GENERATE = 15;


    private final ArtistRepository artistRepository;
    private final BandRepository bandRepository;
    private final PerformerRepository performerRepository;

    private final String[] firstNames;
    private final String[] lastNames;
    private final String[] bandNames;

    private List<Performer> generatedPerformers;
    private List<Performer> harryStylesTourPerformers;
    private List<Performer> taylorSwiftTourPerformers;
    private List<Performer> eltonJohnTourPerformers;



    public PerformerDataGenerator(ArtistRepository artistRepository, BandRepository bandRepository, PerformerRepository performerRepository) {
        this.artistRepository = artistRepository;
        this.bandRepository = bandRepository;
        this.performerRepository = performerRepository;
        this.generatedPerformers = new ArrayList<>();
        this.harryStylesTourPerformers = new ArrayList<>();
        this.taylorSwiftTourPerformers = new ArrayList<>();
        this.eltonJohnTourPerformers = new ArrayList<>();
        this.firstNames = new String[]{"John", "Timothy", "William", "Erica", "Vanessa", "Diana", "Justin", "Mark", "Vanessa", "Frederick",
            "Rhonda", "Sharon", "Richard", "Patrick", "Brenda", "Teresa", "Andrew", "Michael", "William", "Jonathan"};
        this.lastNames = new String[]{"Smith ", "Johnson ", "Williams", "Brown", "Jones", "Garcia", "Davis", "Miller", "Rodriguez", "Martinez",
            "Hernandez", "Lopez", "Gonzalez", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin", "White"};
        this.bandNames = new String[]{"Stage Revolution", "Velvet Concord", "Ecstasy", "Afternoon Daydream", "Turning Jane", "Elaborate Constellations",
            "Double Helix", "Zombie Hoax", "Hero of Refusal", "Armageddon Day", "Perpetual Sorrow", "Kinetic Street", "Greatest Day", "Tokyo Lights", "Ghost Town"};

    }

    public List<Performer> getGeneratedPerformers() {
        return generatedPerformers;
    }

    public Performer getRandomPerformer(){
        Collections.shuffle(this.generatedPerformers);
        return this.generatedPerformers.get(0);
    }

    public List<Performer> getHarryStylesTourPerformers(){
        return harryStylesTourPerformers;
    }

    public List<Performer> getTaylorSwiftTourPerformers(){
        return taylorSwiftTourPerformers;
    }

    public List<Performer> getEltonJohnTourPerformers() {
        return eltonJohnTourPerformers;
    }

    @PostConstruct
    public void generatePerformers() {
        if (performerRepository.findAll().size() > 0) {
            LOGGER.debug("Performers already exist");
        } else {
            LOGGER.debug("Generating {} artists", NUMBER_OF_ARTISTS_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_ARTISTS_TO_GENERATE; i++) {
                Artist artist = Artist.builder()
                    .firstName(firstNames[i])
                    .lastName(lastNames[i])
                    .performerName(firstNames[i]+ " " + lastNames[i])
                    .build();
                artistRepository.save(artist);

                this.generatedPerformers.add(artist);
            }

            Artist harry = Artist.builder()
                .firstName("Harry")
                .lastName("Styles")
                .performerName("Harry Styles")
                .build();

            artistRepository.save(harry);
            harryStylesTourPerformers.add(harry);

            Artist taylor = Artist.builder()
                .firstName("Taylor")
                .lastName("Swift")
                .performerName("Taylor Swift")
                .build();

            artistRepository.save(taylor);
            taylorSwiftTourPerformers.add(taylor);

            Artist girlInRed = Artist.builder()
                .firstName("Marie")
                .lastName("Ringheim")
                .performerName("Girl in Red")
                .build();

            artistRepository.save(girlInRed);
            taylorSwiftTourPerformers.add(girlInRed);

            Artist gracie = Artist.builder()
                .firstName("Gracie")
                .lastName("Abrams")
                .performerName("Gracie Abrams")
                .build();

            artistRepository.save(gracie);
            taylorSwiftTourPerformers.add(gracie);

            Artist owenn = Artist.builder()
                .firstName("Christian")
                .lastName("Owens")
                .performerName("OWENN")
                .build();

            artistRepository.save(owenn);
            taylorSwiftTourPerformers.add(owenn);

            Artist elton = Artist.builder()
                .firstName("Elton")
                .lastName("John")
                .performerName("Elton John")
                .build();

            artistRepository.save(elton);
            eltonJohnTourPerformers.add(elton);

            LOGGER.debug("Generating {} bands", NUMBER_OF_BANDS_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_BANDS_TO_GENERATE; i++) {
                Band band = Band.builder()
                    .performerName(bandNames[i])
                    .build();
                bandRepository.save(band);

                this.generatedPerformers.add(band);
            }
        }
    }
}
