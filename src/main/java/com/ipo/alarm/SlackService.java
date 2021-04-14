package com.ipo.alarm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class SlackService {

    /**
     * to-do
     * 1. 링크도 같이추가
     * 2. 공모주 건마다 연결맺지 말고 한 번에 다 뿌려주기
     * - 슬랙 템플릿 제한
     * - 그로인해 구조적으로 한 번에 다 뿌려줄 수 없다.
     * - 공모주 신청 날짜 당일에만 데이터를 가공하도록 수정
     */

    public void sendMessage(List<IPO> ipoList) {
        System.out.println("ipoList = " + ipoList);
        String inputLine = null;
        String jsonValue = "";
        StringBuffer outResult = new StringBuffer();
        System.out.println("ipoList.size() = " + ipoList.size());
        for (int i = ipoList.size()-1; i>=0; i--) {
            System.out.println("이게왜 안돌아");
            System.out.println("ipoList.get(i).getIpoName() = " + ipoList.get(i).getIpoName());
            //ipoList.get(i)
            jsonValue = "{\n" +
                    "  \"blocks\": [\n" +
                    "    {\n" +
                    "      \"type\": \"section\",\n" +
                    "      \"text\": {\n" +
                    "        \"type\": \"mrkdwn\",\n" +
                    "        \"text\": \"*공모주 청약일정* :pushpin:\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"type\": \"section\",\n" +
                    "      \"block_id\": \"section567\",\n" +
                    "      \"text\": {\n" +
                    "        \"type\": \"mrkdwn\",\n" +
                    "        \"text\": \"기업명 : " + ipoList.get(i).getIpoName() + "\\n 공모 일정: " + ipoList.get(i).getIpoSchedule() + "\\n 희망 공모가 : " + ipoList.get(i).getHopePublicPrice() + "\\n 주간사 : " + ipoList.get(i).getStockFirm() + "\\n\"\n" +
                    "      },\n" +
                    "      \"accessory\": {\n" +
                    "        \"type\": \"image\",\n" +
                    "        \"image_url\": \"https://is5-ssl.mzstatic.com/image/thumb/Purple3/v4/d3/72/5c/d3725c8f-c642-5d69-1904-aa36e4297885/source/256x256bb.jpg\",\n" +
                    "        \"alt_text\": \"Haunted hotel image\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";


            System.out.println("jsonValue = " + jsonValue);

            String slackUrl = "https://hooks.slack.com/services/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";
            try {
                URL url = new URL(slackUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true); //쓰기모드
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                OutputStream os = conn.getOutputStream();
                os.write(Objects.requireNonNull(jsonValue).getBytes("UTF-8"));
                os.flush();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //응답받은 메시지를 버퍼를 생성해 읽어들이고, "UTF-8"로 디코딩해서 읽음.
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                    //한 라인씩 출력
                    while ((inputLine = in.readLine()) != null) {
                        outResult.append(inputLine);
                    }
                } else {
                    System.out.println(conn.getResponseMessage());
                }

                //연결종료
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                log.info("url 호출 에러 : {}", e2.getMessage());
                e2.printStackTrace();
            }
            log.info("결과 : {}", outResult.toString());
        }
    }
}
