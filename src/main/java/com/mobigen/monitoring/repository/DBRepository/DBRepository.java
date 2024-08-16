package com.mobigen.monitoring.repository.DBRepository;

public interface DBRepository extends AutoCloseable {

    int itemsCount();

    void close() throws Exception;

    Long measureExecuteResponseTime();
}
