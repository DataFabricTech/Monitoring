package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.ServicesHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesHistoryRepository extends JpaRepository<ServicesHistory, UUID> {
    // 최근 등록/수정된 연결정보 -> event가 등록, 수정일 경우를 찾아야한다 entityType이랑 eventType 둘 다 join해서 찾아야할듯?.
    List<ServicesHistory> findTopByOrderByUpdatedAtDesc(Pageable pageable);
    // 최근 등록/수정된 연결정보 -> event가 등록, 수정일 경우를 찾아야한다 entityType이랑 eventType 둘 다 join해서 찾아야할듯?.
    List<ServicesHistory> findTopByServiceIDOrderByUpdatedAtDesc(UUID serviceID, Pageable pageable);
//    List<ServicesHistory> findTopByOrderByUpdatedAtDesc(Pageable pageable);
//    List<ServicesHistory> findTopByServiceIDOrderByUpdatedAtDesc(UUID serviceID, Pageable pageable);
}
