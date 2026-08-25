package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.CreateSharingPointRequest;
import gr.aueb.shelfapp.dto.SharingPointDto;
import gr.aueb.shelfapp.entity.SharingPoint;
import gr.aueb.shelfapp.repository.SharingPointRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SharingPointService {

    private final SharingPointRepository sharingPointRepository;

    public SharingPointService(SharingPointRepository sharingPointRepository) {
        this.sharingPointRepository = sharingPointRepository;
    }

    public List<SharingPointDto> findAll() {
        return sharingPointRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public SharingPointDto create(CreateSharingPointRequest request) {
        SharingPoint sharingPoint = new SharingPoint(request.name(), request.city());
        sharingPoint.setStreet(request.street());
        sharingPoint.setStreetNumber(request.streetNumber());
        sharingPoint.setPostalCode(request.postalCode());
        sharingPoint.setPhone(request.phone());

        return toDto(sharingPointRepository.save(sharingPoint));
    }

    private SharingPointDto toDto(SharingPoint s) {
        return new SharingPointDto(
                s.getId(), s.getName(), s.getCity(), s.getStreet(),
                s.getStreetNumber(), s.getPostalCode(), s.getPhone());
    }
}
