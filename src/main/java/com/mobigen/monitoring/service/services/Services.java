package com.mobigen.monitoring.service.services;

import java.util.Map;

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
     *  3. Check Connection Response Time - Avg
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
     * recent created/updated Service
     * items
     * Service Name / Database Type / Connection Status / Owner(Creator) / Created At / Updated At / Description
     * The number of items depend on config (Default is 5)
     *
     * @return Change Service History
     */
    Map<String, String> getRecentChangeServices();

    /**
     * Service History
     * items
     * Updated At / Event Type / Service Name / Database Type / Owner(Creator) / Description
     * The number of items depend on config (Default is 5)
     *
     * @return service history
     */
    Map<String, String> ServiceHistory();

    /**
     * Target Service History
     * items
     * Event Occurred At / Event Type / Service Name / Database Type / Owner(Creator) / Description
     * The number of items depend on config (Default is 3)
     *
     * @return service history
     */
    Map<String, String> ServiceHistory(String serviceID);

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
