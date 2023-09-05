package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

@Profile({"generateData"})
@Component("LocationDataGenerator")
public class LocationDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationDataGenerator(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @PostConstruct
    private void generateLocations() {
        Gson gson = new Gson();

        if(locationRepository.findAll().size() < 25) {
            try {
                Type locationDtoListType = new TypeToken<List<LocationDto>>(){}.getType();
                List<LocationDto> locationDtoList = gson.fromJson(new FileReader(
                    "src/main/java/at/ac/tuwien/sepm/groupphase/backend/datagenerator/jsons/locations.json"), locationDtoListType);
                Iterator<LocationDto> locationIterator = locationDtoList.iterator();

                while(locationIterator.hasNext()) {
                    LocationDto next = locationIterator.next();
                    locationRepository.save(locationMapper.locationDtoToLocation(next));
                }


            } catch (FileNotFoundException e) {
                LOGGER.warn("Could not find the file to parse the into an object in the absolut path " + Paths.get("").toAbsolutePath());
            }

        }


    }


}
