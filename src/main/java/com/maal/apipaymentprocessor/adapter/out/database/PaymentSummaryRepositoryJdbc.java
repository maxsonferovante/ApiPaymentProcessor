package com.maal.apipaymentprocessor.adapter.out.database;

import com.maal.apipaymentprocessor.domain.model.PaymentProcessorType;
import com.maal.apipaymentprocessor.domain.port.out.PaymentSummaryRepository;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentSummaryGetResponse;
import com.maal.apipaymentprocessor.entrypoint.web.dto.SummaryDetails;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PaymentSummaryRepositoryJdbc implements PaymentSummaryRepository {
    
    // Query otimizada: COUNT(*) mais rápido, ordem de colunas otimizada para índice
    private static final String BASE_SUMMARY_SQL = """
        SELECT
            payment_processor_type,
            COUNT(*) AS total_requests,
            COALESCE(SUM(amount), 0) AS total_amount
        FROM
            payments
        """;
    
    private static final String GROUP_BY_CLAUSE = " ORDER BY payment_processor_type";
    
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PaymentSummaryRepositoryJdbc(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public PaymentSummaryGetResponse getSummaryByTimeRange(Instant from, Instant to) {
        QueryBuilder queryBuilder = buildOptimizedQuery(from, to);
        Map<PaymentProcessorType, SummaryDetails> summaryMap = new HashMap<>();

        namedParameterJdbcTemplate.query(queryBuilder.sql, queryBuilder.params, rs -> {
            PaymentProcessorType type = PaymentProcessorType.valueOf(rs.getString("payment_processor_type"));
            int totalRequests = rs.getInt("total_requests");
            BigDecimal totalAmount = rs.getBigDecimal("total_amount");
            
            summaryMap.put(type, new SummaryDetails(totalRequests, totalAmount));
        });

        return createResponseWithDefaults(summaryMap);
    }

    @Override
    public PaymentSummaryGetResponse getTotalSummary() {
        return getSummaryByTimeRange(null, null);
    }

    private QueryBuilder buildOptimizedQuery(Instant from, Instant to) {
        StringBuilder sql = new StringBuilder(BASE_SUMMARY_SQL);
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<String> whereConditions = new ArrayList<>();
        
        addOptimizedTimeConditions(whereConditions, params, from, to);
        
        if (!whereConditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", whereConditions)).append(" ");
        }
        
        sql.append("GROUP BY payment_processor_type").append(GROUP_BY_CLAUSE);
        
        return new QueryBuilder(sql.toString(), params);
    }
    
    private void addOptimizedTimeConditions(List<String> conditions, MapSqlParameterSource params, Instant from, Instant to) {
        if (from != null) {
            conditions.add("requested_at >= :from");
            params.addValue("from", Timestamp.from(from), Types.TIMESTAMP);
        }
        
        if (to != null) {
            conditions.add("requested_at <= :to");  
            params.addValue("to", Timestamp.from(to), Types.TIMESTAMP);
        }
    }
    
    private PaymentSummaryGetResponse createResponseWithDefaults(Map<PaymentProcessorType, SummaryDetails> summaryMap) {
        SummaryDetails defaultSummary = summaryMap.getOrDefault(
            PaymentProcessorType.DEFAULT,
            new SummaryDetails(0, BigDecimal.ZERO)
        );

        SummaryDetails fallbackSummary = summaryMap.getOrDefault(
            PaymentProcessorType.FALLBACK,
            new SummaryDetails(0, BigDecimal.ZERO)
        );

        return new PaymentSummaryGetResponse(defaultSummary, fallbackSummary);
    }
    
    private static class QueryBuilder {
        final String sql;
        final MapSqlParameterSource params;
        
        QueryBuilder(String sql, MapSqlParameterSource params) {
            this.sql = sql;
            this.params = params;
        }
    }
}
