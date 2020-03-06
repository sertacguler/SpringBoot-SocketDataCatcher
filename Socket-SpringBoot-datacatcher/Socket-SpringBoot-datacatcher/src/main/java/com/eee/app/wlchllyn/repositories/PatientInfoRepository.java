package com.eee.app.wlchllyn.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.eee.app.wlchllyn.entities.PrsPatientDTO;

public interface PatientInfoRepository extends CrudRepository<PrsPatientDTO, Long> {

	Optional<PrsPatientDTO> findById(Long id);

	@Query("SELECT c FROM PatientDT c WHERE c.patientNo = ?1")
	PatientD findByPatientNo(String id);

}
