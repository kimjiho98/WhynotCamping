package com.spring.web.vendor.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.spring.web.user.vo.Question;
import com.spring.web.user.vo.Review;
import com.spring.web.vendor.vo.Answer;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.C_Attach;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.CampingSite;
import com.spring.web.vendor.vo.Facilities;
import com.spring.web.vendor.vo.Vendor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/vendor")
@Slf4j
public class VendorController {
	
	@Autowired
	private VendorService svc;
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("/main") // 메인 화면
	public String main(HttpSession session,Model m)
	{
		int vnum = (int)session.getAttribute("vnum");
		CampingMain main = svc.getCampingMain(vnum);
		if (main!=null)
		{
			session.setAttribute("exist", true);
		}
		else session.setAttribute("exist", false);
		m.addAttribute("main",main);

		return "thymeleaf/vendor/vendorMain";
	}
	
	@GetMapping("/login") 		// 로그인 폼 호출
	public String loginForm(HttpServletRequest request)
	{
		return "thymeleaf/vendor/vendorLogin";
	}
	
	@PostMapping("/get_booking")
	@ResponseBody
	public List<Booking> getBookingList(){
		int vnum = (int)session.getAttribute("vnum");
		List<Booking> list = svc.getAllBooking(vnum);
		return list;
	}
	
	@PostMapping("/login") 		// 아이디와 패스워드를 받아서 로그인 실행
	@ResponseBody
	public Map<String,Object> login(String vid,String pwd)
	{
		Map<String,Object> map = new HashMap<>();
		boolean suc = svc.login(vid, pwd,session);
		map.put("suc", suc);
		return map;
	}
	
	@GetMapping("/detail") // 캠핑장 상세정보 - 지환
	public String campSpotDetail(HttpSession session,Model m)
	{
		int vnum = (int)session.getAttribute("vnum");
		Map<String,Object> cmain = svc.campDetail(vnum);
	
		CampingMain camp = (CampingMain)cmain.get("camp");
		session.setAttribute("cnum", camp.getCnum());

		m.addAttribute("cmain", cmain);
				
		return "thymeleaf/vendor/campingDetail";
	}
	
	@GetMapping(value={"/reservation","/reservation/{num}"})
	public String reservation(Model m,@PathVariable(required = false) Optional<Integer> num)
	{
		int pageNum = 1;
		if (num.isPresent())
		{
			pageNum = num.get(); 
		}
		int vnum = (int)session.getAttribute("vnum");
		Map<String,Object> map  = svc.getBookingList(vnum,pageNum);
		PageInfo<Booking> pageInfo = (PageInfo)map.get("pageInfo");
		m.addAttribute("blist", pageInfo.getList());
		m.addAttribute("pages", pageInfo.getPages());
		return "thymeleaf/vendor/reservation";
	}
	
	
	@GetMapping("/campingedit") // 캠핑장 수정폼 - 지환
	public String campEditForm(Model m)
	{
		int vnum = (int)session.getAttribute("vnum");
		Map<String,Object> cmain = svc.campDetail(vnum);		
		CampingMain c = (CampingMain) cmain.get("camp");
		m.addAttribute("cmain", cmain);
		
		return "thymeleaf/vendor/campingEditForm";
	}
	
	@ResponseBody // 캠핑장 정보 수정 - 지환
	@PostMapping("/editcampmain")
	public Map<String,Object> editcampmain(CampingMain camp)
	{	
		int vnum = (int)session.getAttribute("vnum");
		camp.setVnum(vnum);
		Map<String,Object> map = svc.editmain(camp);
					
		return map;
	} 
	
	@ResponseBody // 캠핑장 부대시설 수정 - 지환 
	@PostMapping("/editfacilities")
	public Map<String,Object> editfac(Facilities fac)
	{	
		int cnum = (int)session.getAttribute("cnum");
		fac.setCnum(cnum);                     
		Map<String,Object> map = svc.editfaci(fac);
					
		return map;
	}
	
	@ResponseBody // 캠핑site 수정 - 지환
	@PostMapping("/editcampsite")
	public Map<String,Object> editcamp(CampingSite site)
	{	
		int cnum = (int)session.getAttribute("cnum");
		site.setCnum(cnum);                     
		Map<String,Object> map = svc.editSite(site);
					
		return map;
	}
	
	@ResponseBody // 캠핑site 삭제 - 지환
	@PostMapping("/delcampsite")
	public Map<String,Object> delcamp(CampingSite site)
	{	
		int cnum = (int)session.getAttribute("cnum");
		site.setCnum(cnum);                     
		Map<String,Object> map = svc.delSite(site);
					
		return map;
	}
	
	@ResponseBody // 캠핑site 삭제 - 지환
	@PostMapping("/addonesite")
	public Map<String,Object> addSiteSpot(CampingSite site)
	{			 
		Map<String,Object> map = svc.addSiteOn(site);
					
		return map;
	}
	
	@ResponseBody // 캠핑 첨부사진 삭제 - 지환
	@PostMapping("/delimg")
	public Map<String,Object> delimg(C_Attach ca,String cname)
	{	
		Map<String,Object> map = svc.delimg(ca,cname);
					
		return map;
	}
	
	
	@ResponseBody
	@PostMapping("/addimg") // 캠핑 첨부사진 추가 - 지환
	public Map<String,Object> addReivew(@RequestParam("ifiles") MultipartFile[] ifiles,@RequestParam("mapimg") MultipartFile[] mapimg, HttpServletRequest request,C_Attach ca,String cname)  
	{	
		Map<String,Object> map = new HashMap<>();
		map.put("ifiles", ifiles);
		map.put("mapimg", mapimg);
		map.put("request", request);
		map.put("ca", ca);		
		map.put("cname", cname);
		String res = svc.addImg(map);		
		Map<String,Object> rmap = new HashMap<>();
		
		rmap.put("msg", res);
		rmap.put("url", "/vendor/campingedit");
		return rmap; 
	}
	
	
	
	
	
	@GetMapping("/join") 		// 회원가입 폼 호출
	public String vendorJoinForm()
	{
		return "thymeleaf/vendor/vendorJoinForm";
	}
	
	@PostMapping("/vidcheck/{vid}") 		// 아이디 중복체크
	@ResponseBody
	public Map<String,Object> vidCheck(@PathVariable String vid)
	{
		Map<String,Object> map = new HashMap<>();
		boolean ok = svc.vidCheck(vid);
		map.put("ok", ok);
		return map;
	}
	
	@PostMapping("/join")		 // 회원가입 버튼 클릭시 회원가입 쿼리 구동
	@ResponseBody
	public Map<String,Object> vendorJoin(Vendor vendor)
	{
		int rows = svc.addVendor(vendor);
		Map<String,Object> map = new HashMap<>();
		map.put("suc", rows>0);
		return map;
	}
	
	@GetMapping("/reg") 		// 캠핑지 등록 폼 호출
	public String campRegForm()
	{
		return "thymeleaf/vendor/campingRegForm";
	}
	
	@PostMapping("/reg") 		// 캠핑지 등록
	@ResponseBody
	public boolean campRegister(CampingMain main,@RequestParam("snum_list") List<String> snum_list,@RequestParam("sname_list") List<String> sname_list,
			@RequestParam("max_ppl_list") List<String> max_ppl_list,@RequestParam("price_list") List<String> price_list)
	{
		log.info("sname={}",sname_list);
		log.info("sname={}",price_list);
		List <CampingSite> slist = new ArrayList<>();
		for(int i=0;i<snum_list.size();i++)
		{
			CampingSite site = new CampingSite();
			site.setSnum(Integer.parseInt(snum_list.get(i)));
			site.setPrice(Integer.parseInt(price_list.get(i)));
			site.setMax_ppl(Integer.parseInt(max_ppl_list.get(i)));
			site.setSname(sname_list.get(i));
			site.setVnum((int)session.getAttribute("vnum"));
			slist.add(site);
		}
		
		return svc.saveToSession(main,slist,session);
	}
	
	@GetMapping("/reg2") 		// 캠핑지 등록 폼 호출
	public String campRegForm2()
	{
		return "thymeleaf/vendor/campingRegForm2";
	}
	
	@PostMapping("/reg2") 		// 캠핑지 등록
	@ResponseBody
	public boolean campRegister2(Facilities fac,@RequestParam("cfiles")MultipartFile[] cfiles,
			@RequestParam("map")MultipartFile[] map)
	{
		
		int rows = svc.addCamping(fac,cfiles, map, session);
		return rows>0;
	}
	
	@PostMapping("/cname_check")
	@ResponseBody
	public Map<String,Object> cnameCheck(@RequestParam String cname)
	{
		Map<String,Object> map = new HashMap<>();
		boolean ok = svc.cnameCheck(cname);
		map.put("ok", ok);
		return map;
	}
	
	@GetMapping("/myinfo")		// vendor 정보
	public String vendorInfo(Model m)
	{
		int vnum = (Integer)session.getAttribute("vnum");
		Vendor vendor = svc.getVendor(vnum);
		m.addAttribute("vendor",vendor);
		return "thymeleaf/vendor/vendorInfo";
	}
	
	@GetMapping("/vendorEdit") // vendor 정보 수정폼
	public String vendorEdit(Model m)
	{
		int vnum = (Integer)session.getAttribute("vnum");
		Vendor vendor = svc.getVendor(vnum);
		m.addAttribute("vendor",vendor);
		return "thymeleaf/vendor/vendorEdit";
	}
	
	@PostMapping("/confirm")
	@ResponseBody
	public boolean bookingConfirm(@RequestParam int bnum)
	{
		return svc.bookingConfirm(bnum);
	}
	
	// QnA 페이지
	@GetMapping("/qna") 
	public String Qna(Model m)
	{
		int vnum = (Integer)session.getAttribute("vnum");
		List<Map<String,Object>> mlist = svc.getQnAByVnum(vnum);
		m.addAttribute("mlist", mlist);
		return "thymeleaf/vendor/vendorQnA";
	}
	
	@GetMapping("/logout")
	public String logout()
	{
		svc.logout(session);
		return "thymeleaf/vendor/vendorLogin";
	}
	
	@PostMapping("add_answer")
	@ResponseBody
	public boolean addAnswer(Answer answer)
	{		
		return svc.addAnswer(answer);
	}
	
	@PostMapping("sendEmail/{email}")
	@ResponseBody
	public boolean sendEmail(@PathVariable String email)
	{
		String code = svc.createRandomStr();
		session.setAttribute("code", code);
		boolean res = svc.sendHTMLMessage(email, code);
		return res;
	}
	
	@PostMapping("verifyEmail/{code}")
	@ResponseBody
	public boolean verifyEmail(@PathVariable String code)
	{
		return code.equals((String)session.getAttribute("code"));
	}

	@ResponseBody
	public String test()
	{
		
		return null;
	}
}
   