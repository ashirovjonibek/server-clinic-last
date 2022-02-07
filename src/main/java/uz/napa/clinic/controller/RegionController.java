package uz.napa.clinic.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.napa.clinic.repository.DistrictRepository;
import uz.napa.clinic.repository.RegionRepository;
import uz.napa.clinic.repository.SocialStatusRepository;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/region")
public class RegionController {
    private final RegionRepository regionRepository;

    private final DistrictRepository districtRepository;

    private final SocialStatusRepository socialStatusRepository;

    public RegionController(RegionRepository regionRepository, DistrictRepository districtRepository, SocialStatusRepository socialStatusRepository) {
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.socialStatusRepository = socialStatusRepository;
    }

    @GetMapping
    public HttpEntity<?> getAll(){
        return ResponseEntity.ok(regionRepository.findAll());
    }

    @GetMapping("/district")
    public HttpEntity<?> getAllDistrict(@RequestParam Long id){
        return ResponseEntity.ok(districtRepository.findAllByRegionId(id));
    }

    @GetMapping("/social")
    public HttpEntity<?> getSoc(){
        return ResponseEntity.ok(socialStatusRepository.findAll());
    }

    @GetMapping("/{disId}")
    public HttpEntity<?> getReg(@PathVariable Long disId){

        return ResponseEntity.ok(districtRepository.findById(disId).get().getRegion());
    }


}
