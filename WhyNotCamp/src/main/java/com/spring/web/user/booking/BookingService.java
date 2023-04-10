package com.spring.web.user.booking;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.spring.web.user.vo.Review;
import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.C_Attach;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.CampingSite;
import com.spring.web.vendor.vo.Facilities;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@SessionAttributes("unum")
public class BookingService 
{
	@Autowired
	   private BookingMapper dao;
	
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private TemplateEngine htmlTemplateEngine;
	
	@Autowired
	private HttpSession session;
	
	// booking form에 필요한 모든정보 - 소영
	public Map<String, Object> booking(int cnum)	
	{
		Map<String,Object> ttlMap = new HashMap<String,Object>();
		
		List<Map<String,Object>> list = dao.campDetail(cnum);
		
		CampingMain camp = new CampingMain();	// for문을 돌려 campingmain객체에 저장한다
		
		for(int i=0;i<list.size();i++)
		{
			Map<String,Object> map = list.get(i);
			
			BigDecimal big = (java.math.BigDecimal)map.get("CNUM");
			boolean found = false;
			
			camp.setCnum(big.intValue());
			big = (BigDecimal)map.get("VNUM");
			camp.setVnum(big.intValue());
			camp.setCname((String)map.get("CNAME"));
			camp.setAddress((String)map.get("ADDRESS"));
			camp.setPhone((String)map.get("PHONE"));
			
			// C_Attach 리스트 저장			
			Object obj = map.get("FNAME");
			if(obj==null)
			{
				if(!found) continue;
			}
			String fname = (String)obj;
			if(fname.startsWith("map"))
			{
			
				ttlMap.put("sitemap", fname);

				
				continue;
			}
			
				C_Attach att = new C_Attach();
				att.setFname((String) obj);
				big = (BigDecimal)map.get("ANUM");
				att.setAnum(big.intValue());
				if(!(camp.getAttList().contains(att))) camp.getAttList().add(att);
				
			
			// CampingSite 리스트 저장
				CampingSite site = new CampingSite();
				big = (BigDecimal)map.get("SNUM");
				site.setSnum(big.intValue());
				site.setSname((String)map.get("SNAME"));
				big = (BigDecimal)map.get("BOOKABLE");
				site.setBookable(big.intValue());
				big = (BigDecimal)map.get("MAX_PPL");
				site.setMax_ppl(big.intValue());
				big = (BigDecimal)map.get("PRICE");
				site.setPrice(big.intValue());
				if(!(camp.getSiteList().contains(site))) camp.getSiteList().add(site);
				
				
			// Review 리스트 저장

				obj = map.get("RNUM");
				if(obj==null)
				{
					if(!found) continue;
				}
				Review rev = new Review();
				big = (BigDecimal)obj;
				rev.setRnum(big.intValue());
				rev.setNickname((String)map.get("NICKNAME"));
				rev.setContent((String)map.get("CONTENT"));
				big = (BigDecimal)map.get("SCORE");
				rev.setScore(big.intValue());
				
				Timestamp reg =(java.sql.Timestamp)map.get("REGDATE");
			    Date regdate = new Date(reg.getTime());
				rev.setRegDate(regdate);
				
				if(!(camp.getRevList().contains(rev))) camp.getRevList().add(rev);
				
		}
		
		ttlMap.put("camping_main", camp);	// 별점 평균은 따로 mapper로 구현
		/*
		float avg = dao.scoreAvg(cnum);
		if(avg==0.0f)
		{
			float rnd = Math.round(avg*10)/10.0f;	// 평균을 소수점 첫째짜리 까지만 표현
			ttlMap.put("score",rnd );
		}*/
		if(dao.scoreAvg(cnum)!=null)
		{
			float avg = (float)dao.scoreAvg(cnum);
			float rnd = Math.round(avg*10)/10.0f;	// 평균을 소수점 첫째짜리 까지만 표현
			ttlMap.put("score",rnd );
		}
			
		
		ttlMap.put("bnum", dao.findBnum());
				
		return ttlMap;
	}

	// 예약가능 사이트 확인 - 소영
	public List<String> roomAvailable(Date checkin, Date checkout,int cnum)
	{
		List<Map<String,Object>> listmap = dao.roomAvailable(checkin,checkout,cnum);
		List<String> list = new ArrayList<>();
		for(int i=0;i<listmap.size();i++)
		{
			Map<String,Object> rmap = listmap.get(i);
			BigDecimal snumDec = (java.math.BigDecimal)rmap.get("SNUM");
			BigDecimal num = (java.math.BigDecimal)rmap.get("NUM_RESERVATIONS");
			log.info("snum"+snumDec);
	        if (num.intValue() != 0) {
	        	String snum = String.valueOf(snumDec.intValue());
	            list.add(snum);
	        }
		}
		return list;
	}
	
	
	// 예약정보 booking 테이블에 저장 - 소영
	public boolean putBooking(Booking book)	
	{
		int unum = (int) session.getAttribute("unum");
		String[] site = book.getPhone().split(";");
		int price = Integer.parseInt(site[0]);
		int snum = Integer.parseInt(site[1]);
		String sname = site[2];
						
		long in = book.getCheckin().getTime();
		long out = book.getCheckout().getTime();
		
		long nights = (out-in)/1000/(24*60*60); // 숙박 일수 구하기
				
		Date checkin = book.getCheckin();
		Date checkout = book.getCheckout();		
		
		int ttlprice = (int) (price*nights);	// html에서 가져온 1박당 가격에 사람 수를 곱해 최종 금액을 구한다
			
		int result = dao.putBooking(book.getBnum(), book.getCnum(), unum ,snum
				,sname, checkin, checkout,book.getPpl(), ttlprice);
		
		return result>0?true:false;
	}

	// 최종 예약사항 요약 및 detail - 소영 
	public Map<String, Object> bookSummary(int bnum)	
	{
		Map<String, Object> map = dao.bookSummary(bnum);
		
		User user = new User();		
		BigDecimal big = (java.math.BigDecimal)map.get("UNUM");
		user.setUnum(big.intValue());
		user.setName((String)map.get("NAME"));
		user.setEmail((String)map.get("EMAIL"));
		user.setPhone((String)map.get("PHONE"));
		
		System.out.println("user:"+user.toString());
		
		CampingMain camp = new CampingMain();
		big = (BigDecimal)map.get("CNUM");
		camp.setCnum(big.intValue());
		camp.setCname((String) map.get("CNAME"));
		camp.setAddress((String)map.get("ADDRESS"));

	    Booking book = new Booking();
	    big = (BigDecimal)map.get("BNUM");
		book.setBnum(big.intValue());
		book.setSname((String)map.get("SNAME"));
		Timestamp in =(java.sql.Timestamp)map.get("CHECKIN");
	    Date checkin = new Date(in.getTime());
	    book.setCheckin(checkin);
	    Timestamp out =(java.sql.Timestamp)map.get("CHECKOUT");
	    Date checkout = new Date(in.getTime());
	    book.setCheckout(checkout);
		big = (BigDecimal)map.get("TTLPRICE");
		book.setTtlprice(big.intValue());
		big = (BigDecimal)map.get("PPL");
		book.setPpl(big.intValue());
		
		map.put("camping_main", camp);
		map.put("booking", book);
		map.put("user", user);
		
		return map;
	}
	

	// 캠핑장 안내문 - 소영
	public Map<String,Object> getFacility(int cnum)	

	{

		Map<String,Object> n_map = new HashMap<>();
		Map<String,Object> map = dao.getFacility(cnum);
		
		Facilities f = new Facilities();
		
		BigDecimal big = (java.math.BigDecimal)map.get("PET");
		f.setPet(big.intValue());
		big = (java.math.BigDecimal)map.get("COOK");
		f.setCook(big.intValue());
		big = (java.math.BigDecimal)map.get("BBQ");
		f.setFire(big.intValue());
		big = (java.math.BigDecimal)map.get("FIRE");
		f.setFire(big.intValue());
		big = (java.math.BigDecimal)map.get("WIFI");
		f.setWifi(big.intValue());
		big = (java.math.BigDecimal)map.get("TOILET");
		f.setToilet(big.intValue());
		
		big = (java.math.BigDecimal)map.get("IN_TIME");
		n_map.put("in_time", big.intValue());

		big = (java.math.BigDecimal)map.get("OUT_TIME");
		n_map.put("out_time", big.intValue());
		
		n_map.put("facility", f);
		
		return n_map;
	}
	
	// 마지막 예약정보 업데이트 - 소영
	public boolean bookConfirm(Booking book)	
	{
		return dao.bookConfirm(book)>0?true:false;
	}
	
	// 예약자에게 예약 정보 메일 보내기 - 소영
	 public boolean sendSimpleText(String email,Map<String, Object> variables) 
	 {

 		 MimeMessage mimeMessage = sender.createMimeMessage();
 		 Context context = new Context();
         context.setVariables(variables);

	      try 
	      {
	 		 MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	 		 	 		 
	         String htmlTemplate = htmlTemplateEngine.process("thymeleaf/booking/bookingMail", context);

	         //InternetAddress[] addressTo = new InternetAddress[1];
	         //addressTo[0] = new InternetAddress(email);
	         InternetAddress addressTo = new InternetAddress(email);
	         mimeMessage.setRecipient(Message.RecipientType.TO, addressTo);
	         mimeMessage.setSubject("캠핑어때 이메일 인증을 해주세요");      

	         helper.setSubject("[캠핑어때] 예약 확정 안내");
	         helper.setText(htmlTemplate,true);
	         
	         // 메일에 이미지 첨부하기
	         ClassPathResource image1 = new ClassPathResource("static/img/bonfire.png");
	         helper.addInline("bonfire", image1);
	         ClassPathResource image2 = new ClassPathResource("static/img/check_icon.png");
	         helper.addInline("check_icon", image2);
	         ClassPathResource image3 = new ClassPathResource("static/img/캠핑 로고.jpg");
	         helper.addInline("logo", image3);
	         
	         sender.send(mimeMessage);
	         return true;
	      }
	      catch (MessagingException e) 
	      {
	         log.error("에러={}", e);
	      }
	     return false;
	 } 	 
	
	 // bnum으로 예약정보 가져오기 - 소영
	 public Booking findBooking(int bnum)
	 {
		 return dao.findBooking(bnum);
	 }
	 
	 public CampingMain getCampingMain(int cnum)
	 {
		 return dao.getCampingMain(cnum);
	 }
}
