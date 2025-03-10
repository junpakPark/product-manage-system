package com.github.junpakpark.productmanage.acceptance;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleanup {

    @Autowired
    private EntityManager entityManager;
    private List<String> tableNames;

    @PostConstruct
    private void setUpDatabaseTableNames() {
        this.tableNames = getTableNames();
    }

    private List<String> getTableNames() {
        return getTableMetaData().stream().map(this::getTableName).toList();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getTableMetaData() {
        return entityManager.createNativeQuery("SHOW TABLES").getResultList();
    }

    private String getTableName(final Object[] tableInfos) {
        return (String) tableInfos[0];
    }

    @Transactional
    public void clear() {
        executeQuery("SET REFERENTIAL_INTEGRITY FALSE;");
        tableNames.forEach(tableName -> executeQuery("TRUNCATE TABLE " + tableName + " RESTART IDENTITY;"));
        executeQuery("SET REFERENTIAL_INTEGRITY TRUE;");
    }

    private void executeQuery(final String query) {
        entityManager.createNativeQuery(query).executeUpdate();
    }
}
