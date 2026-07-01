package com.swiftcart.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseMigrationConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationConfig.class);

    @Bean
    @Order(0)
    CommandLineRunner razorpayPaymentColumnMigration(JdbcTemplate jdbcTemplate) {
        return args -> {
            addColumnIfMissing(jdbcTemplate, "payments", "razorpay_order_id", "VARCHAR(120)");
            addColumnIfMissing(jdbcTemplate, "payments", "razorpay_payment_id", "VARCHAR(120)");
            addColumnIfMissing(jdbcTemplate, "payments", "razorpay_signature", "VARCHAR(512)");
        };
    }

    private void addColumnIfMissing(JdbcTemplate jdbcTemplate, String table, String column, String definition) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "select count(*) from information_schema.columns where table_name = ? and column_name = ?",
                    Integer.class,
                    table,
                    column
            );
            if (count != null && count > 0) {
                return;
            }
        } catch (Exception ex) {
            log.debug("Could not inspect column {}.{} before migration", table, column, ex);
        }

        try {
            jdbcTemplate.execute("alter table " + table + " add column " + column + " " + definition);
            log.info("Added missing column {}.{}", table, column);
        } catch (Exception ex) {
            log.debug("Skipping column migration for {}.{}", table, column, ex);
        }
    }
}
