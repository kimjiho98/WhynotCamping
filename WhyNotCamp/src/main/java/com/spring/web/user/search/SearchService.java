package com.spring.web.user.search;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.web.user.booking.BookingMapper;
import com.spring.web.user.vo.Review;
import com.spring.web.vendor.vo.C_Attach;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.Facilities;

import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SearchService 
{
	@Autowired
	   private SearchMapper dao;
	
	@Autowired
	private HttpSession session;
	
	private static final String URL = "https://www.google.com";
    private static final String GET = "GET";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String DATA = "test data";	
    private static final String POST = "POST";
	
	
	
	public List<Map<String,Object>> searchMain(String keyword)
	{
		return dao.searchMain(keyword);
	}
	

	//유저가 클릭한 캠핑장 상세보기 (캠핑장 상세, 이미지, 약도, 리뷰, Qna)
	public Map<String,Object> usermapdetail(int cnum)
	{
		List<Map<String, Object>> list = dao.usermapdetail(cnum);
	
		CampingMain main = new CampingMain();
		
		Map<String,Object> map = list.get(0);
		
		Map<String,Object> map1 = new HashMap<>();
 
		BigDecimal	big = (BigDecimal)map.get("CNUM");
		main.setCnum(big.intValue());
		
		main.setCname((String) map.get("CNAME"));
		main.setAddress((String)map.get("ADDRESS"));
		main.setPhone((String)map.get("PHONE"));
		
		BigDecimal in = (BigDecimal)map.get("IN_TIME");		
		main.setIn_time(in.intValue());
		
		BigDecimal out = (BigDecimal)map.get("OUT_TIME");
		main.setOut_time(out.intValue());



		Facilities fac = new Facilities();
		
		BigDecimal pet = (BigDecimal)map.get("PET");
		fac.setPet(pet.intValue());
		BigDecimal cook = (BigDecimal)map.get("COOK");
		fac.setCook(cook.intValue());
		BigDecimal bbq = (BigDecimal)map.get("BBQ");
		fac.setBbq(bbq.intValue());
		BigDecimal fire = (BigDecimal)map.get("FIRE");
		fac.setFire(fire.intValue());
		BigDecimal wifi = (BigDecimal)map.get("WIFI");
		fac.setWifi(wifi.intValue());
		BigDecimal toilet = (BigDecimal)map.get("TOILET");
		fac.setToilet(toilet.intValue());
		
		
		for(int i=0; i<list.size();i++)
		{
			
			map = list.get(i);
			
		   boolean found = false;
			
			
			Object obj = map.get("FNAME");
			if(obj==null)
			{
				if(!found) continue;
			}
			
			//attach list 
			
			
			C_Attach att = new C_Attach();
			
			att.setFname((String) obj);
			BigDecimal anum = (BigDecimal)map.get("ANUM");
			att.setAnum(anum.intValue());
			
			if((!main.getAttList().contains(att))) main.getAttList().add(att);	
			
			//site list
			/*
			CampingSite site = new CampingSite();
			
			BigDecimal sitenum = (BigDecimal)map.get("SNUM");
			site.setSnum(sitenum.intValue());
			log.info("sitenum={}",sitenum.intValue());
			BigDecimal sitebookalbe= (BigDecimal)map.get("BOOKABLE");
			site.setBookable(sitebookalbe.intValue());
			BigDecimal ppl = (BigDecimal)map.get("MAX_PPL");
			site.setMax_ppl(ppl.intValue());
			BigDecimal siteprice = (BigDecimal)map.get("PRICE");
			site.setPrice(siteprice.intValue());
			if((!main.getSiteList().contains(site))) main.getSiteList().add(site);
			System.out.println(ppl.intValue());
			*/
			
			
			//review list
			Review rev = new Review();
			obj = map.get("RNUM");
			if(obj==null)
			{
				if(!found) continue;
			}
				BigDecimal revrnum = (BigDecimal)map.get("RNUM");
				rev.setRnum(revrnum.intValue());
				/*
				BigDecimal revunum = (BigDecimal)map.get("UNUM");
				rev.setRnum(revunum.intValue());
				*/
			    rev.setNickname((String)map.get("NICKNAME"));
				rev.setContent((String)map.get("CONTENT"));
				
				BigDecimal revsocre = (BigDecimal)map.get("SCORE");
				rev.setScore(revsocre.intValue());
				
			    /*
				Timestamp reg = (java.sql.Timestamp)map.get("REGDATE");
				Date regdate = new Date(reg.getTime());
				rev.setRegDate(regdate);
				*/
				
				if((!main.getRevList().contains(rev))) main.getRevList().add(rev);
				
			}
		Optional<Object> optional = Optional.ofNullable(dao.scoreAvg(cnum));
		if(optional.isPresent())
		{			
			float avg = dao.scoreAvg(cnum);		
			float rnd = Math.round(avg*10)/10.0f;	// 평균을 소수점 첫째짜리 까지만 표현
			map.put("score",rnd);
		}
		map.put("fac", fac);
		map.put("main",main);	
	
		return map;		
	
	}
	
	//파이썬 연결 - 지환 
	//Post방식
	public List<Integer> pyPostTest(int unum) throws IOException {
		
	    URL url = new URL("http://localhost:7717/recommend/"+unum);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	
	    connection.setRequestMethod(POST);     // POST 방식 요청
	    connection.setRequestProperty("User-Agent", USER_AGENT);
	    connection.setDoOutput(true);
	
	    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
	    outputStream.writeBytes(DATA);
	    outputStream.flush();
	    outputStream.close();
	
	    int responseCode = connection.getResponseCode();
	
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    StringBuffer stringBuffer = new StringBuffer();
	    String inputLine;
	
	    while ((inputLine = bufferedReader.readLine()) != null)  {
	        stringBuffer.append(inputLine);
	    }
	    bufferedReader.close();
	
	    String response = stringBuffer.toString();
	    String[] strArr = response.replaceAll("\\[|\\]|\\s", "").split(",");
	    List<Integer> list = new ArrayList<>();
	    for(int i=0; i<strArr.length ; i++ )
	    {	    	
	    	int res1 = Integer.valueOf(strArr[i]);
	    	list.add(res1);
	    }	        
	    return list;
	}
	
	
	public List<CampingMain> recommendList(int unum)
	{
		
		try 
		{
			List<Integer> RecommendCampingList = pyPostTest(unum);
			List<CampingMain> recList = dao.userRecommend(RecommendCampingList);			
			
			return recList;
		
		} 
		catch (IOException e)
		{
			System.err.println("파이썬연결안됨");
			e.printStackTrace();
		}		
		
		return null;
	}
	
	
	
	
	
	
	


}
