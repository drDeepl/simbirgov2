package ru.simbirgo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.simbirgo.models.Rent;

import java.util.List;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {

    @Query(value = "SELECT * FROM rents WHERE account_id = :accountId", nativeQuery = true)
    List<Rent> findRentsByAccountId(@Param("accountId") Long accountId);

    @Query(value = "SELECT * FROM rents WHERE transport_id = :transportId", nativeQuery = true)
    List<Rent> findRentsByTransportId(@Param("transportId") Long transportId);


}
