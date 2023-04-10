package com.spring.web.user.booking;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.CampingMain;

import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/book")
@Controller
public class BookingController 
{
	@Autowired
	private BookingService svc;
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("/bookform")	// 예약 폼 불러오기 - 소영
	public String bookingForm(Model m,@RequestParam int cnum)
	{
		Map<String, Object> map = svc.booking(cnum);
		
		CampingMain camp = (CampingMain) map.get("camping_main");
		m.addAttribute("camp",map.get("camping_main"));	// camping_main VO에 c_attach list,c_review 있음
		m.addAttribute("bnum",map.get("bnum"));
		m.addAttribute("score",map.get("score"));	// score은 전체 평점을 평균낸 값
		m.addAttribute("sitemap",map.get("sitemap"));
		
		log.info((String) map.get("sitemap"));
		
		return "thymeleaf/booking/bookingForm";
	}
	
	@PostMapping("/available")
	@ResponseBody
	public Map<String,Object> roomAvailable(Date checkin, Date checkout, int cnum)
	{
		List<String> list = svc.roomAvailable(checkin,checkout,cnum);
		Map<String,Object> res = new HashMap<>();
		res.put("bookable",list);
		return res;
	}
	
	
	@PostMapping("/summary")	// 최종 예약서 - 소영
	public String bookingSummary(Booking book,Model m)
	{
		boolean result = svc.putBooking(book);
		
		int bnum = book.getBnum();
		
		Map<String, Object> map = svc.bookSummary(bnum);
		m.addAttribute("camp",map.get("camping_main"));
		m.addAttribute("book",map.get("booking"));
		m.addAttribute("user",map.get("user"));
		
		return "thymeleaf/booking/bookingSummary";
	}
	
	@GetMapping("/facility")	// 캠핑장 안내문 팝업 - 소영
	public String facility(Model m,@RequestParam int cnum)
	{
		Map<String, Object> map = svc.getFacility(cnum);
		m.addAttribute("facility",map.get("facility"));
		m.addAttribute("in_time",map.get("in_time"));
		m.addAttribute("out_time",map.get("out_time"));		
		
		return "thymeleaf/booking/facility";
	}
	
	// 예약 최종 확정 및 메일 전송 - 소영
	@PostMapping("/confirm")	
	@ResponseBody
	public Map<String,Object> bookingConfirm(Booking book)
	{
		boolean confirm = svc.bookConfirm(book);
		
		Map<String,Object> map = new HashMap<>();
		String email = "jihoan2@naver.com";
		
		map.put("book", svc.findBooking(book.getBnum()));
		map.put("facility", svc.getFacility(book.getCnum()).get("facility"));
		map.put("camp", svc.getCampingMain(book.getCnum()));
			    
		boolean isSent = svc.sendSimpleText(email,map);

	    String msg = confirm ? "예약이 성공적으로 완료 되었습니다." : "예약에 실패하였습니다.";
	    Map<String,Object> responseMap = new HashMap<>();
	      
	    responseMap.put("confirm", confirm);
	    responseMap.put("msg", msg);	      
			
		return responseMap;
	}
	
		
	
	
}
