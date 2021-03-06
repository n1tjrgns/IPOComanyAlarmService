package com.ipo.alarm;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class IPOCompanyService {

    public void ipoAlarmService(){
        //우선은 기능 구현부터 빠르게 진행
        String url = "http://www.38.co.kr/html/fund/?o=k";    //크롤링할 url지정
        Document doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        }catch(IOException e) {
            log.info("url을 불러오는 도중 오류 발생 : {}", e.getMessage());
        }

        //select를 이용하여 원하는 태그를 선택한다. select는 원하는 값을 가져오기 위한 중요한 기능이다.
        //                               ==>원하는 값들이 들어있는 덩어리를 가져온다
        Elements selectElements = doc.select("table[summary='공모주 청약일정'] tbody tr");

        LocalDate now = LocalDate.now();
        List<IPO> ipoList = new ArrayList<>();
        for (Element selectElement : selectElements) {
            //각각의 필요한 데이터와 공모주는 현재날짜보다 큰 것들만 가져오기
            Elements tdElements = selectElement.select("td");
            Element getA = selectElement.select("td > a").first();
            String link = getA.attr("href");
            String realLink = "http://www.38.co.kr"+link;
            //System.out.println("realLink = " + realLink);

            //System.out.println("tdElements = " + tdElements);
            String day = tdElements.get(1).text();
            String subDay = day.substring(0,10);
            //System.out.println("subDay = " + subDay);

            SimpleDateFormat beforesimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
            SimpleDateFormat aftersimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            //현재 desc되어 있음

            IPO ipo = null;
            try {
                Date formatDate = beforesimpleDateFormat.parse(subDay);
                String afterDate = aftersimpleDateFormat.format(formatDate);
                LocalDate parseDate = LocalDate.parse(afterDate);
                //System.out.println("afterDate = " + afterDate);
                //System.out.println("now = " + parseDate);

                if (now.isEqual(parseDate)){

                    String title = tdElements.get(0).text();
                    //System.out.println("title = " + title);

                    //확정공모가
                    String commitPrice = tdElements.get(3).text();

                    String hopePrice = tdElements.get(3).text();
                    //System.out.println("hopePrice = " + hopePrice);

                    //4번 경쟁률
                    String competiton = tdElements.get(4).text();
                    //System.out.println("competiton = " + competiton);
                    //5번 주관사
                    String[] stockCompany = tdElements.get(5).text().split(",");
                    //System.out.println("stockCompany = " + Arrays.toString(stockCompany));
                    List<String> tempStockCompany = new ArrayList<>();
                    for (String company : stockCompany) {
                        tempStockCompany.add(company);
                    }

                    ipo = IPO.builder()
                            .ipoName(title)
                            .ipoSchedule(day)
                            .hopePublicPrice(hopePrice)
                            .stockFirm(tempStockCompany)
                            .analystLink(realLink)
                            .build();

                    ipoList.add(ipo);
                }else{
                    continue;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //System.out.println("ipo = " + ipo);
        }

        SlackService slackService = new SlackService();
        slackService.sendMessage(ipoList);
    }



}
