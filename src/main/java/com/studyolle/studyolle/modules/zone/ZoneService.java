package com.studyolle.studyolle.modules.zone;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        if(zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource( "zone_kr.csv" );
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map( line -> {
                        String[] splits = line.split( "," );
                        return Zone.builder().city( splits[0] ).localNameOfCity( splits[1] ).province( splits[2] ).build();
                    } ).collect( Collectors.toList());
            zoneRepository.saveAll( zoneList );
        }
    }

}
