package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

@Profile("generateData")
@DependsOn({"EventDataGenerator", "LayoutDataGenerator"})
@Component("PerformanceDataGenerator")
public class PerformanceDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PerformanceRepository performanceRepository;

    private final LayoutRepository layoutRepository;

    private final LayoutDataGenerator layoutDataGenerator;
    private final EventDataGenerator eventDataGenerator;

    private static final int NUMBER_OF_PERFORMANCES_PER_EVENT = 2;

    public PerformanceDataGenerator(PerformanceRepository performanceRepository, LayoutRepository layoutRepository, LayoutDataGenerator layoutDataGenerator, EventDataGenerator eventDataGenerator) {
        this.performanceRepository = performanceRepository;
        this.layoutRepository = layoutRepository;
        this.layoutDataGenerator = layoutDataGenerator;
        this.eventDataGenerator = eventDataGenerator;
    }

    @PostConstruct
    public void generatePerformances() {

        List<Layout> layouts = layoutRepository.findAll();

        if (eventDataGenerator.getGeneratedEvents().size() > 0) {
            LOGGER.debug("Generating {} performances", NUMBER_OF_PERFORMANCES_PER_EVENT);
            for (int i = 0; i < eventDataGenerator.getGeneratedEvents().size(); i++) {
                Event e = eventDataGenerator.getGeneratedEvents().get(i);
                for (int j = 0; j < NUMBER_OF_PERFORMANCES_PER_EVENT; j++) {
                    Performance performance = Performance.builder()
                        .performanceName("Performance" + j)
                        .startTime(LocalDateTime.of(2024, (i + j) % 12 + 1, (i + j) % 28 + 1, 12 + 2 * j, 0))
                        .endTime(LocalDateTime.of(2024, (i + j) % 12 + 1, (i + j) % 28 + 1, 13 + 2 * j, 0))
                        .event(e)
                        .layout(layouts.get((int)(Math.random() * layouts.size())))
                        .build();

                    performanceRepository.save(performance);
                }
            }

            Performance harryStylesGermany = Performance.builder()
                .performanceName("Deutsche Bank Park")
                .startTime(LocalDateTime.of(2023, 7, 5, 18, 0))
                .endTime(LocalDateTime.of(2023, 7, 5, 20, 0))
                .event(eventDataGenerator.getHarryStylesTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();
            Performance harryStylesAustria = Performance.builder()
                .performanceName("Ernst-Happel-Stadion")
                .startTime(LocalDateTime.of(2023, 7, 8, 18, 0))
                .endTime(LocalDateTime.of(2023, 7, 8, 20, 0))
                .event(eventDataGenerator.getHarryStylesTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();
            Performance harryStylesSpain = Performance.builder()
                .performanceName("Estadi Olimpic LLuis Company")
                .startTime(LocalDateTime.of(2023, 7, 12, 18, 0))
                .endTime(LocalDateTime.of(2023, 7, 12, 20, 0))
                .event(eventDataGenerator.getHarryStylesTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();
            performanceRepository.save(harryStylesGermany);
            performanceRepository.save(harryStylesAustria);
            performanceRepository.save(harryStylesSpain);

            Performance taylorSwiftChicago1 = Performance.builder()
                .performanceName("Chicago, IL")
                .startTime(LocalDateTime.of(2023, 6, 2, 18, 0))
                .endTime(LocalDateTime.of(2023, 6, 2, 20, 0))
                .event(eventDataGenerator.getTaylorSwiftTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();
            Performance taylorSwiftChicago2 = Performance.builder()
                .performanceName("Chicago, IL")
                .startTime(LocalDateTime.of(2023, 6, 3, 18, 0))
                .endTime(LocalDateTime.of(2023, 6, 3, 20, 0))
                .event(eventDataGenerator.getTaylorSwiftTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();
            Performance taylorSwiftChicago3 = Performance.builder()
                .performanceName("Chicago, IL")
                .startTime(LocalDateTime.of(2023, 6, 4, 18, 0))
                .endTime(LocalDateTime.of(2023, 6, 4, 20, 0))
                .event(eventDataGenerator.getTaylorSwiftTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();

            performanceRepository.save(taylorSwiftChicago1);
            performanceRepository.save(taylorSwiftChicago2);
            performanceRepository.save(taylorSwiftChicago3);

            Performance eltonLiverpool = Performance.builder()
                .performanceName("M&S Bank Arena Liverpool")
                .startTime(LocalDateTime.of(2023, 3, 23, 18, 0))
                .endTime(LocalDateTime.of(2023, 3, 23, 20, 0))
                .event(eventDataGenerator.getEltonJohnTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();
            Performance eltonBirmingham = Performance.builder()
                .performanceName("Resorts World Arena Birmingham")
                .startTime(LocalDateTime.of(2023, 3, 26, 18, 0))
                .endTime(LocalDateTime.of(2023, 3, 26, 20, 0))
                .event(eventDataGenerator.getEltonJohnTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();
            Performance eltonDublin = Performance.builder()
                .performanceName("3Arena Dublin")
                .startTime(LocalDateTime.of(2023, 3, 29, 18, 0))
                .endTime(LocalDateTime.of(2023, 3, 29, 20, 0))
                .event(eventDataGenerator.getEltonJohnTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();
            Performance eltonLondon = Performance.builder()
                .performanceName("O2 Arena London")
                .startTime(LocalDateTime.of(2023, 4, 13, 18, 0))
                .endTime(LocalDateTime.of(2023, 4, 13, 20, 0))
                .event(eventDataGenerator.getEltonJohnTour())
                .layout(this.layoutDataGenerator.getGeneratedLayouts().get(0))
                .build();

            performanceRepository.save(eltonLiverpool);
            performanceRepository.save(eltonBirmingham);
            performanceRepository.save(eltonDublin);
            performanceRepository.save(eltonLondon);


        }



    }
}
