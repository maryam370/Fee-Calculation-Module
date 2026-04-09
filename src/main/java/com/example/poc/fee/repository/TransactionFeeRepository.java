package com.example.poc.fee.repository;

import com.example.poc.fee.model.TransactionFeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionFeeRepository extends JpaRepository<TransactionFeeRecord, Long> {
}
