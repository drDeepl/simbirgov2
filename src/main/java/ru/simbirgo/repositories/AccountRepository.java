package ru.simbirgo.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.simbirgo.dtos.AccountDTO;
import ru.simbirgo.models.Account;
import ru.simbirgo.repositories.interfaces.AccountI;
import ru.simbirgo.repositories.interfaces.IdAndUsernameAndIsAdminAndBalance;

import java.util.List;
import java.util.Optional;



@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    Boolean existsByUsername(String username);


    @Query(value="SELECT * FROM accounts", nativeQuery = true)
    List<Account> findAccounts();
}
