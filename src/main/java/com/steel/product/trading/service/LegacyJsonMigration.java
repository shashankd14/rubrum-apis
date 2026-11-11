package com.steel.product.trading.service;

import com.steel.product.trading.repository.MaterialDetailRepository;
import com.steel.product.trading.repository.StockDocumentRepository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Log4j2
public class LegacyJsonMigration implements ApplicationRunner {
    private final TransactionTemplate transaction;
    private final JdbcTemplate jdbc;
    private final StockDocumentRepository stockDocuments;
    private final InwardAttachmentService inwardAttachments;
    private final MaterialDetailRepository materialDetails;

    public LegacyJsonMigration(PlatformTransactionManager transactionManager, JdbcTemplate jdbc, StockDocumentRepository stockDocuments,
            InwardAttachmentService inwardAttachments, MaterialDetailRepository materialDetails) {
        this.transaction = new TransactionTemplate(transactionManager);
        this.jdbc = jdbc;
        this.stockDocuments = stockDocuments;
        this.inwardAttachments = inwardAttachments;
        this.materialDetails = materialDetails;
    }

    @Override
    public void run(ApplicationArguments args) {
        migrate("stock document lines", stockDocuments::migrateLegacyLines);
        migrate("inward attachments", inwardAttachments::migrateLegacy);
        migrate("material details", materialDetails::migrateLegacy);
    }

    private void migrate(String name, Supplier<Integer> step) {
        try {
            Integer migrated = jdbc.execute((ConnectionCallback<Integer>) connection -> {
                if (!lock(connection, "SELECT GET_LOCK('legacy_json_migration', 120)")) {
                    log.warn("Skipped {} migration; another instance holds the migration lock", name);
                    return 0;
                }
                try {
                    return transaction.execute(status -> step.get());
                } finally {
                    lock(connection, "SELECT RELEASE_LOCK('legacy_json_migration')");
                }
            });
            if (migrated != null && migrated > 0) log.info("Migrated {} legacy JSON records to {}", migrated, name);
        } catch (Exception e) {
            log.error("Unable to migrate legacy JSON to " + name + "; run scripts/2026-11-06-json-to-child-tables.sql first", e);
        }
    }

    private static boolean lock(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            return result.next() && result.getInt(1) == 1;
        }
    }
}
