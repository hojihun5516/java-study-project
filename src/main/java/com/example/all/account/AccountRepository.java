package com.example.all.account;

import com.example.all.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Account findByEmail(String email);

    Account findByNickname(String nickname);
}
