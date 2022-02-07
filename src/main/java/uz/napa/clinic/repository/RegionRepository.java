package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import uz.napa.clinic.entity.Region;
import uz.napa.clinic.projection.CustomInfoRegion;
import uz.napa.clinic.projection.CustomRegion;

import javax.validation.constraints.Negative;
import java.util.List;
//@CrossOrigin("*")
//@RepositoryRestResource(path = "region", excerptProjection = CustomRegion.class)
public interface RegionRepository extends JpaRepository<Region, Long> {


    @Query(nativeQuery = true, value = "select r.id as regionId,COUNT(r.id) from application a\n" +
            "left join users u\n" +
            "on a.created_by=u.id\n" +
            "left join district d\n" +
            "on u.district_id=d.id\n" +
            "left join region r\n" +
            "on d.region_id=r.id\n" +
            "group by r.id\n")
    List<CustomInfoRegion> getCountByRegion();
}
