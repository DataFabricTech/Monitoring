package com.mobigen.monitoring.service.services;

import com.mobigen.monitoring.dto.ServicesChange;
import com.mobigen.monitoring.dto.ServicesConnect;
import com.mobigen.monitoring.dto.ServicesEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Services {
    /**
     * 신경 써야 하는 것
     *   Date / Target / Multi user
     * - 3. Recent Created/Updated Services
     * -  3.1. Get Service ID/Type/Owner
     * -  3.2. Join Connection Status (Connected, Disconnected - 시도도 안한 것은 Disconnected)
     * -  3.4. CreatedAt/UpdatedAt
     * -  3.5. Description (수정, 등록)
     * @. Connection
     *  1. Service Connection Search (사용자의 Connection Check / Scheduler Connection Check로 인한 결과 가져오기)
     *  2. run Connection Test
     *   2.1. set timeout using config
     *  3. Connect Response Time Average calculator is DBMS's function
     * @. Event Monitoring
     *  1. 위의 이벤트 볼 수 있도록
     * @. DB의 row 생성 및 삭제 기능 필요
     *
     * @. DB 구조 설계
     * Unique Key = Service ID & Updated At
     * Service ID / Name / Database Type / Connection Status / Connect Response Time / created At / updated At / Description
     * UUID?String / String / Enum / Boolean / Integer(millisecond) / LocalDateTime / longOrTimeStamp / String
     *
     *
     */

    /**
     * Get Services' recent `created/updated` history
     * items
     * Service Name / Database Type / Connection Status / Owner(Creator) / Created At / Updated At / Description
     * The number of items depend on config (Default is 5)
     *
     * @return Service's recent `create/update` history
     */
    List<ServicesChange> getServiceRecentChange();

    /**
     * Get Target Service's recent `created/updated` history
     * @param serviceID target serviceID
     * @return Target Service's recent `create/update` history
     */
    List<ServicesChange> getServiceRecentChange(UUID serviceID);

    /**
     * Service History
     * items
     * Updated At / Event Type / Service Name / Database Type / Owner(Creator) / Description
     * The number of items depend on config (Default is 5)
     *
     * @return service history
     */
    List<ServicesEvent> getServiceEvent();

    /**
     * Target Service History
     * items
     * Event Occurred At / Event Type / Service Name / Database Type / Owner(Creator) / Description
     * The number of items depend on config (Default is 3)
     *
     * @return service history
     */
    List<ServicesEvent> getServiceEvent(UUID serviceID);

    List<ServicesConnect> getServiceConnect();
    List<ServicesConnect> getServiceConnect(UUID serviceID);

    /**
     * for measure current connection status
     */
    void runConnection();

//    /**
//     * Get Count by Type and Count by Connection Status
//     * @return
//     */
//    Map<String, Integer> getServiceTypeCount();
//

//    /**
//     * get target entity monitoring data with filter(todo)
//     * @return specify entity data
//     */
//    T getEntity();
}
