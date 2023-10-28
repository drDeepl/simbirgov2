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

    @Query(value = "SELECT owner_id_id FROM transports WHERE id = :transportId", nativeQuery = true)
    Long findOwnerIdByTransportId(@Param("transportId") Long transportId);

    @Query(value="SELECT * FROM transports WHERE acos(sin(radians(:lat)) * sin(radians(latitude)) + cos(radians(:lat)) * cos(radians(latitude)) * cos( radians(:lng) - radians(longitude))) * 6371 <= :radius",
            nativeQuery = true)
    List<Transport> findTransportByParams(@Param("lat") Double lat, @Param("lng") Double lng, Double radius);

    @Query(value="SELECT * FROM transports WHERE acos(sin(radians(:lat)) * sin(radians(latitude)) + cos(radians(:lat)) * cos(radians(latitude)) * cos( radians(:lng) - radians(longitude))) * 6371 <= :radius AND transport_type = :transportType",
            nativeQuery = true)
    List<Transport> findTransportByParamsFilterTransportType(@Param("lat") Double lat, @Param("lng") Double lng, Double radius, @Param("transportType") String transportType);


    @Query(value="SELECT * FROM transports WHERE id = :transportId AND owner_id_id = :ownerId", nativeQuery = true)
    Transport findTransportByOwnerIdAndTransportId(@Param("ownerId") Long ownerId, @Param("transportId") Long transportId);





}
