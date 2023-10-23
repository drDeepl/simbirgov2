package ru.simbirgo.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.simbirgo.models.Transport;


import java.util.List;
import java.util.Optional;
@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {

    List<Transport> findAllByTransportType(String transportType, Pageable pageable);


}
