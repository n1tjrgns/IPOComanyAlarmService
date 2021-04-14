package com.ipo.alarm;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class IPO {
    //청약전 알 수 있는 항목
    private String ipoName; //공모주
    private String ipoSchedule; //공모주 일정
    private String hopePublicPrice; //희망 공모가

    private List<String> stockFirm; //주관사
    private String analystLink;

    //청약이 끝난 뒤 알 수 있는 항목
    private String commitPublicPrice;
    private String subscriptionCompetitionRate; //청약경쟁률
}
