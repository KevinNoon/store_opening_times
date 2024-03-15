package com.optimised.repository;

import com.optimised.model.Place;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepo extends JpaRepository<Place,Long> {

  Optional<Place> findByAddress(String address);

  Optional<Place> findAllByPlaceId(String placeId);

  @Query("select c from Place c " +
      "where lower(c.name) like lower(concat('%', :searchName, '%')) " +
      "and lower(c.address) like lower(concat('%', :searchAddress, '%'))")
  List<Place> filterByNameAndAddress(@Param("searchName") String searchName, @Param("searchAddress") String searchAddress);

  @Query("select c from Place c " +
      "where lower(c.name) like lower(concat('%', :searchName, '%')) " +
      "and lower(c.address) like lower(concat('%', :searchAddress, '%'))" +
      "and c.inuse = true")
  List<Place> filterByNameAndAddressAAndInuse(@Param("searchName") String searchName, @Param("searchAddress") String searchAddress);

  Place findFirstByName(String name);
  Place findFirstByStoreNo(Integer storeNo);

  List<Place> findAllByInuseIsTrue();

  Optional<Place> findById(Long placeId);
}

