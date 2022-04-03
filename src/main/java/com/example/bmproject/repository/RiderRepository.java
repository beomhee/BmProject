package com.example.bmproject.repository;

import com.example.bmproject.entity.RiderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RiderRepository extends JpaRepository<RiderEntity, Long> {
    @Query(value = "SELECT * FROM bm_rider WHERE rider_run = 'N' ORDER BY RAND() LIMIT 1", nativeQuery = true)
    RiderEntity randomRiderInfo();

    @Query(value = "SELECT * FROM bm_rider WHERE rider_no = ?1", nativeQuery = true)
    RiderEntity getRiderInfo(String riderNo);

}
