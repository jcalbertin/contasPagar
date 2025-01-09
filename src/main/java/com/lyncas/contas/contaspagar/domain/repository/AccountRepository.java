package com.lyncas.contas.contaspagar.domain.repository;

import com.lyncas.contas.contaspagar.domain.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findByDataVencimentoBetweenAndDescricaoContainingIgnoreCase(
            LocalDate dataVencimentoStart,
            LocalDate dataVencimentoEnd,
            String descricao,
            Pageable pageable
    );

    @Query("""
            SELECT SUM(a.valor) FROM Account a 
            WHERE a.dataPagamento BETWEEN :startDate AND :endDate
                    AND a.situacao = com.lyncas.contas.contaspagar.domain.enums.AccountStatusEnum.PAGA
        """)
    BigDecimal sumValorByDataPagamento(LocalDate startDate, LocalDate endDate);
}