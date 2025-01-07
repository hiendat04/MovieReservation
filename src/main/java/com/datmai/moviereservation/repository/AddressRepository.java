package com.datmai.moviereservation.repository;

import com.datmai.moviereservation.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer>, JpaSpecificationExecutor<Address> {
}
