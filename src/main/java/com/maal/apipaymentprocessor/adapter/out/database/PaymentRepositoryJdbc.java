package com.maal.apipaymentprocessor.adapter.out.database;

import com.maal.apipaymentprocessor.domain.model.Payment;
import com.maal.apipaymentprocessor.domain.port.out.PaymentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.sql.Types;

@Repository
public class PaymentRepositoryJdbc implements PaymentRepository {

    // Query otimizada: sem cast UUID custoso, ordem otimizada para índice
    private static final String INSERT_SQL = 
        "INSERT INTO payments (correlation_id, payment_processor_type, requested_at, amount) VALUES (?, ?, ?, ?)";
    
    private static final String DELETE_ALL_SQL = "TRUNCATE TABLE payments";

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Payment payment) {
        // Prepared statement otimizado: UUID direto, sem cast
        jdbcTemplate.update(INSERT_SQL,
                new Object[]{
                    payment.getCorrelationId(),  // UUID direto
                    payment.getPaymentProcessorType().name(),
                    Timestamp.from(payment.getRequestedAt()),
                    payment.getAmount()
                },
                new int[]{Types.OTHER, Types.VARCHAR, Types.TIMESTAMP, Types.NUMERIC});
    }

    @Override
    public void deleteAll() {
        // TRUNCATE é mais rápido que DELETE para limpar tabela completa
        jdbcTemplate.execute(DELETE_ALL_SQL);
    }
}
