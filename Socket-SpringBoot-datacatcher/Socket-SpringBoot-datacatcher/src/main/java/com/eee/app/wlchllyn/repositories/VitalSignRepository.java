package com.eee.app.wlchllyn.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eee.app.wlchllyn.entities.wlchllynObservationDTO;

@Repository
public interface VitalSignRepository extends CrudRepository<wlchllynObservationDTO, Long>{

}
