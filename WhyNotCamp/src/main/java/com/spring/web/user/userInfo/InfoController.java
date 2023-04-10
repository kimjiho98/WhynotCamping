package com.spring.web.user.userInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.web.user.vo.Alarm;
import com.spring.web.user.vo.Question;
import com.spring.web.user.vo.Review;
import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.CampingMain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/userinfo")
@Controller
public class InfoController {


    @Autowired
	private InfoService svc;

    @Autowired
	private HttpSession session;
	    
	@GetMapping("/myinfo") //내정보 보기 - 지환
	public String myinfo(Model m)
	{
		int unum = (int) session.getAttribute("unum");
		
		User user = svc.myinfo(unum);
		m.addAttribute("user",user);		
		
		return "thymeleaf/userInfo/userInfo";
	}	
	
	@GetMapping("/myinfoedit") // 내정보 수정폼 - 지환
	public String myinfoeditForm(Model m )
	{		
		int unum = (int) session.getAttribute("unum");
		
		User user = svc.myinfo(unum);
		m.addAttribute("user",user);		
		
		return "thymeleaf/userInfo/userInfoEditForm"; 
	}	

	@ResponseBody
	@PostMapping("/mypwedit") // 내비밀번호 수정 - 지환
	public Map<String,Object> myPwEdit(String uuid,String pastpw,String pw)
	{
		User user = new User();
		int unum = (int) session.getAttribute("unum");		
		
		user.setUuid(uuid);
		user.setUnum(unum);		
		user.setPwd(pastpw);
		Map<String,Object> map= svc.updatePw(user,pw);
			
		return map;
	}		
	
	@ResponseBody
	@PostMapping("/mailCheck") // 새로운 메일에 인증링크 보내기 - 지환
	public Map<String,Object> myinfoupdate(String email)
	{
		String code = svc.createRandomStr();
		session.setAttribute("code", code);
	    
		boolean isSent = svc.sendHTMLMessage(email,code);
	    String msg = isSent ? "인증 메일이 전송되었습니다." : "인증메일 전송에 실패하였습니다.";
	    Map<String,Object> map = new HashMap<>();
	      
	    map.put("isSent", isSent);
	    map.put("msg", msg);	      
			
		return map;
	}		
	
	@PostMapping("/codeCheck")
	@ResponseBody
	public boolean verifyEmail(@RequestParam("emailcode") String code,@RequestParam("email") String email)
	{	  
		int unum = (int)session.getAttribute("unum");		            
		boolean res = code.equals((String)session.getAttribute("code"));
		boolean editRes = false;
		if(res)
		{
		  editRes = svc.updateEmail(unum,email);
		}     
		return res && editRes ;
    }

	@ResponseBody
	@PostMapping("/editaddphone") 	//유저 연락처 및 주소 수정 - 지환 -
	public Map<String,Object> phoneAddEdit(User user)
	{		
		int unum = (int) session.getAttribute("unum");						
		user.setUnum(unum);		
		log.info("user : " + user);
		Map<String,Object> map= svc.upPhoneAdd(user);
			
		return map;
	}		
	
	
	@GetMapping("/mybooking")
	public String mybooking(Model m)  // 나의 예약 현황 - 지환
	{
		int unum = (int) session.getAttribute("unum");		

		List<Map<String,Object>> list = svc.bookList(unum);
		m.addAttribute("list", list);
		
		return "thymeleaf/userInfo/userBookingResult";
	}	
	
	
	@ResponseBody
	@PostMapping("/alCheck") // 알람여부 체크하기 - 지환
	public Map<String,Object> alCheck()
	{
		Map<String,Object> map = new HashMap<>();
		
		if(session.getAttribute("unum")!=null)
		{
			int unum = (Integer)session.getAttribute("unum");
			
			List<Map<String,Object>> list= svc.getAlarm(unum);		
			if(list.size()!=0)
			{
				map.put("bell", true);
			}
			else
			{
				map.put("bell", false);
				map.put("stop",true);
			}
		}
		else map.put("bell", false);
		return map;
	}			
	
	@ResponseBody
	@PostMapping("/getalist") // 알람 -링크 메세지 넣기 - 지환
	public Map<String,Object> getalist()
	{
		Map<String,Object> map = new HashMap<>();
		int unum = (Integer)session.getAttribute("unum");
		
		List<Map<String,Object>> list= svc.getAlarm(unum);		
		if(list.size()!=0)
		{
			map.put("list", list);
		}		
		return map;
	}
		
	@ResponseBody
	@PostMapping("/removeAlarm") // 알람삭제
	public Map<String,Object> removeAlarm(Alarm alarm)
	{
		svc.delAlarm(alarm);
		
		Map<String,Object> map = new HashMap<>();
		map.put("msg", "del");
		
		return map;
	}	
	
	
	
	@GetMapping("/myreview") // 내가 쓴 리뷰 - 지환
	public String myreview(Model m)
	{
		int unum = (int) session.getAttribute("unum");
		String nickname = (String) session.getAttribute("nickname");

		m.addAttribute("list", svc.myreview(unum));
		return "thymeleaf/userInfo/userReviewList"; 
	}	
	
	
	@GetMapping("/addreview/{bnum}/{cnum}")  // 리뷰쓰는폼주기 - 지환
	public String reviewForm(@PathVariable("cnum") int cnum,@PathVariable("bnum") int bnum,Model m) 
	{
		int unum = (int) session.getAttribute("unum");
		CampingMain camp = svc.getCamp(cnum);
		
		Review review = new Review();
		review.setCnum(cnum);
		review.setUnum(unum);
		review.setBnum(bnum);
		review.setNickname((String)session.getAttribute("nickname"));
		
		m.addAttribute("camp",camp);
		m.addAttribute("review", review);
		
		return "thymeleaf/userInfo/userReviewForm"; 
	}	
	
	@ResponseBody
	@PostMapping("/addreview") // 내가 갔던 곳 리뷰 쓰기 - 지환
	public Map<String,Object> addReivew(@RequestParam("rfiles") MultipartFile[] rfiles, HttpServletRequest request,Review review)  
	{	
		Map<String,Object> map = new HashMap<>();
		
		map.put("rfiles", rfiles);
		map.put("request", request);
		map.put("review", review);		
		String res = svc.addReview(map);		
		Map<String,Object> rmap = new HashMap<>();				
		rmap.put("msg", res);
		return rmap; 
	}
	
	@GetMapping("/addquestion/{cnum}")  //  Question쓰는 폼주기 - 지환
	public String questionForm(@PathVariable("cnum") int cnum,Model m) 
	{	
		CampingMain camp = svc.getCamp(cnum);
		
		
		m.addAttribute("cnum", cnum);
		m.addAttribute("cname",camp.getCname());
		return "thymeleaf/userInfo/questionForm"; 
	}	
	
	@ResponseBody
	@PostMapping("/addquestion")  // Question 쓰기 - 지환
	public Map<String,Object> addQuestion(Question q) 
	{	
		log.info("q" + q);
		
		Map<String,Object> map = new HashMap<>();
		map = svc.addQuestion(q);
			
		return map; 
	}	
	
	@GetMapping("/myqalist") // 나의 QA관리 - 지환
	public String maqalist(Model m)
	{	
		int unum = (int) session.getAttribute("unum");
		String nickname = (String) session.getAttribute("nickname");
		
		m.addAttribute("list", svc.userQAList(unum));
		m.addAttribute("nickname", nickname);//세션으로 들가면 빼도됨
				
		return "thymeleaf/userInfo/userQAList"; 
	}

	@GetMapping("/userchat")
	public String user()
	{
		return "thymeleaf/userInfo/index";
	}

}
