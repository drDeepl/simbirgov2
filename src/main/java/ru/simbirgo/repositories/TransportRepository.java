package ru.simbirgo.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.simbirgo.dtos.TransportDTO;
import ru.simbirgo.models.ETransportType;
import ru.simbirgo.models.Transport;
import ru.simbirgo.repositories.interfaces.TransportI;


import java.util.List;
import java.util.Optional;
@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {

    @Query(value = "SELECT * FROM transports WHERE transport_type = :transport_type", nativeQuery = true)
    List<Transport> findAllByTransportType(@Param("transport_type") String transportType, Pageable pageable);

    @Query(value = "SELECT * FROM transports", nativeQuery = true)
    List<Transport> findAllTransports(Pageable pageable);




}
