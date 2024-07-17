package com.mobigen.monitoring.repository.DBRepository;

public interface DBRepository extends AutoCloseable {

    int itemsCount() throws Exception;

    void close() throws Exception;
    Long measureExecuteResponseTime() throws Exception;
}
