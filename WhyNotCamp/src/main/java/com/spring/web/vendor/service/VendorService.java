package com.spring.web.vendor.service;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spring.web.user.vo.Question;
import com.spring.web.user.vo.R_Attach;
import com.spring.web.user.vo.Review;
import com.spring.web.vendor.vo.Answer;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.C_Attach;

import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.CampingSite;

import com.spring.web.vendor.vo.Facilities;
import com.spring.web.vendor.vo.Vendor;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.replace.Replace;

@Service
@Slf4j
public class VendorService 
{
	@Autowired
	private VendorMapper dao;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private JavaMailSender sender;
		
	private Path fileStroageLocation;

	
 	@Transactional	// DB에 vendor 추가
	public int addVendor(Vendor vendor) 
	{
		dao.addVendor(vendor);		
		
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		vendor.setPwd(enc.encode(vendor.getPwd()));
		Vendor v = new Vendor();
		
		dao.addCampUser(vendor);
		int rows = dao.addAuthorities(vendor);
		return rows;
	}
	
	// id 중복 체크
	public boolean vidCheck(String vid) 
	{
		List<Vendor> list = dao.vidCheck(vid);
		return list.size()>0;
	}
	
	// 로그인
	public boolean login(String vid, String pwd,HttpSession session) 
	{
		Vendor vendor = dao.getVendorByVid(vid);
		boolean suc = vendor.getPwd().equals(pwd);
		if(suc)
		{
			session.setAttribute("vid", vid);
			session.setAttribute("vnum", vendor.getVnum());
		}
		return suc;
	}
	
	// vnum으로 vendor 호출
	public Vendor getVendor(int vnum) 			
	{
		return dao.getVendor(vnum);
	}
	
	public List<Booking> getAllBooking(int vnum)
	{
		return dao.getBookingList(vnum);
	}
	
	// vnum으로 예약 리스트 뽑기 pagehelper 추가
	public Map<String,Object> getBookingList(int vnum,int pageNum) 		
	{
		Map<String,Object> map = new HashMap<>();
		PageHelper.startPage(pageNum,3);
		PageInfo<Booking> pageInfo = new PageInfo<>(dao.getBookingList(vnum));
		map.put("pageInfo", pageInfo);
		return map;
	}
	//
	public CampingMain getCampingMain(int vnum)
	{
		return dao.getCampingMain(vnum);
	}
	
	// 메인 페이지 정보 추출
	public Map<String,Object> getMain(int vnum)
	{
		
		List<Map<String,Object>> list = dao.getMain(vnum);
		CampingMain main = new CampingMain();
		Map<String,Object> map2 = new HashMap<>();
		if(list.size()!=0)  // 캠핑지 정보가 존재하는지
		{
			Map<String,Object> map = new HashMap<>();
			map = list.get(0); 		
			
			main.setCname((String)map.get("CNAME"));
			main.setCnum(((BigDecimal)map.get("CNUM")).intValue());
			main.setAddress((String)map.get("ADDRESS"));
			main.setPhone((String)map.get("PHONE"));
			main.setInfo((String)map.get("INFO"));
			
			if(map.get("IN_TIME")!=null)
			{
				main.setIn_time(((BigDecimal)map.get("IN_TIME")).intValue());
				main.setOut_time(((BigDecimal)map.get("OUT_TIME")).intValue());
			}
			List<Booking> blist = new ArrayList<>();
			for(int i=0;i<list.size();i++)
				{
					try {
						map = list.get(i);
						
						Booking booking = new Booking();
						if(map.get("BNUM")!=null)
						{
							
							booking.setBnum(((BigDecimal)map.get("BNUM")).intValue());
							if(!blist.contains(booking))
							{
								Timestamp in = (java.sql.Timestamp)map.get("CHECKIN");
								booking.setCheckin(new Date(in.getTime()));
								Timestamp out = (java.sql.Timestamp)map.get("CHECKOUT");
								booking.setCheckout(new Date(out.getTime()));
								booking.setName((String)map.get("BNAME"));
								booking.setPhone((String)map.get("BPHONE"));
								booking.setEmail((String)map.get("EMAIL"));
								booking.setPpl(((BigDecimal)map.get("PPL")).intValue());
								booking.setTtlprice(((BigDecimal)map.get("TTLPRICE")).intValue());
								booking.setConfirm(((BigDecimal)map.get("CONFIRM")).intValue());
								
								blist.add(booking);
							}
						}
					} catch (NullPointerException e)
					{
						log.info("오류발생");
						e.printStackTrace();
						continue;
					}
					map2.put("blist", blist);
				}
			
			List<Question> qlist = new ArrayList<>();
			for(int i=0;i<list.size();i++)
			{
				map = list.get(i);
				Question q = new Question();
				if(map.get("QNUM")!=null)
				{
					
					q.setQnum(((BigDecimal)map.get("QNUM")).intValue());
					
					if(!qlist.contains(q))
					{
						q.setCnum(((BigDecimal)map.get("CNUM")).intValue());
						Timestamp reg = (java.sql.Timestamp)map.get("QREGDATE");
						q.setQRegDate(new Date(reg.getTime()));
						q.setQuestion((String)map.get("QUESTION"));
						q.setUnum(((BigDecimal)map.get("UNUM")).intValue());
						
						qlist.add(q);
					}
				}
			}
			if(qlist.size()!=0)
			{
				map2.put("qlist", qlist);
			}
			map2.put("main", main);
			
			return map2;
		}
		return map2;
	}


	
	// 캠핑지 등록 
	public int addCamping(Facilities fac,MultipartFile[] cfiles,MultipartFile[] map,HttpSession ss) 			
	{
		CampingMain main = (CampingMain)ss.getAttribute("main");
		List <CampingSite> slist = (List<CampingSite>)ss.getAttribute("slist");
		
		dao.addCamping(main);
		addCattach(main, cfiles, map);
		
		Map<String,Object> m = new HashMap<>();
		m.put("vnum", main.getVnum());
		m.put("fac", fac);
		dao.addFacilities(m);	

		int rows = dao.addSite(slist);

		return rows;
	}
	
	
	public boolean bookingConfirm(int bnum)
	{
		return dao.bookingConfirm(bnum)>0;
	}
	
	// qna 정보 vnum으로 가져오기
	public List<Map<String,Object>> getQnAByVnum(int vnum)
	{
		List<Map<String,Object>> list = dao.getQnAByVnum(vnum);
		List<Map<String,Object>> mlist = new ArrayList<>();
		
		for(int i=0;i<list.size();i++)
		{
			Map<String,Object> m = list.get(i);
			
			Question q = new Question();
			q.setQnum(((BigDecimal)m.get("QNUM")).intValue());
			q.setUnum(((BigDecimal)m.get("UNUM")).intValue());
			q.setCnum(((BigDecimal)m.get("CNUM")).intValue());
			Timestamp qreg = (java.sql.Timestamp)m.get("QREGDATE");
			q.setQRegDate(new Date(qreg.getTime()));
			q.setQuestion((String)m.get("QUESTION"));
			
			Answer a = new Answer();
			if(m.get("ASNUM")!=null)
			{
				a.setAnswer((String)m.get("ANSWER"));
				Timestamp areg = (java.sql.Timestamp)m.get("AREGDATE");
				a.setARegDate(new Date(areg.getTime()));
			}
			Map<String,Object> map = new HashMap<>();
			map.put("q", q);
			map.put("a", a);
			
			mlist.add(map);
		}
		
		return mlist;
		
	}

	//캠핑장 상세정보 -지환-
	public Map<String,Object> campDetail(int vnum)
	{		
		List<Map<String,Object>> clist = dao.campingDetail(vnum);
		Map<String,Object> map = new HashMap<>();
		
		Map<String,Object> c = clist.get(0);
		CampingMain camp = new CampingMain();
		
		BigDecimal cnum = (java.math.BigDecimal)c.get("CNUM");
		BigDecimal vvnum = (java.math.BigDecimal)c.get("VNUM");
		camp.setCnum(cnum.intValue());
				
		camp.setVnum(vvnum.intValue());

		camp.setCname((String)c.get("CNAME"));
		camp.setAddress((String)c.get("ADDRESS"));
		camp.setPhone((String)c.get("PHONE"));
		camp.setInfo((String)c.get("INFO"));
		
		BigDecimal pet = (java.math.BigDecimal)c.get("PET");
		BigDecimal cook = (java.math.BigDecimal)c.get("COOK");
		BigDecimal bbq = (java.math.BigDecimal)c.get("BBQ");
		BigDecimal fire = (java.math.BigDecimal)c.get("FIRE");
		BigDecimal wifi = (java.math.BigDecimal)c.get("WIFI");
		BigDecimal toilet = (java.math.BigDecimal)c.get("TOILET");
		
		Facilities fac = new Facilities();
		
		fac.setPet(pet.intValue());
		fac.setCook(cook.intValue());
		fac.setBbq(bbq.intValue());
		fac.setFire(fire.intValue());
		fac.setWifi(wifi.intValue());
		fac.setToilet(toilet.intValue());		
		fac.setCnum(cnum.intValue());		
		
		C_Attach mapatt = new C_Attach();
		map.put("mapatt", mapatt);	
		
		for(int i = 0 ; i < clist.size() ; i ++)
		{			
			boolean found = false;
			Object obj = c.get("FNAME");
			if(obj==null)
			{
				if(!found) continue;
			}			
			Map<String,Object> cm = clist.get(i);
			
			String fname = (String)cm.get("FNAME");
			if(fname.startsWith("map"))
			{
				mapatt.setFname(fname);
				BigDecimal anum = (java.math.BigDecimal)cm.get("ANUM");
				mapatt.setAnum(anum.intValue());
				mapatt.setCnum(cnum.intValue());				
				map.put("mapatt", mapatt);
				
				continue;
			}				
			
			C_Attach catt = new C_Attach();			
			BigDecimal anum = (java.math.BigDecimal)cm.get("ANUM");			
			catt.setAnum(anum.intValue());
			catt.setFname((String)cm.get("FNAME"));

			catt.setCnum(cnum.intValue());	
			
			if(!camp.getAttList().contains(catt)) camp.getAttList().add(catt);			
		}

		for(int j = 0 ; j < clist.size() ; j++)
		{
			Map<String,Object> cm = clist.get(j);
			
			boolean atfound = false;
			BigDecimal snum = (java.math.BigDecimal)cm.get("SNUM");
			if(snum==null)
			{
				if(!atfound) continue;
			}						
			CampingSite site = new CampingSite();
						
			BigDecimal max_ppl= (java.math.BigDecimal)cm.get("MAX_PPL");
			BigDecimal price= (java.math.BigDecimal)cm.get("PRICE");
			
			site.setSnum(snum.intValue());
			site.setCnum(cnum.intValue());
			site.setPrice(price.intValue());
			site.setMax_ppl(max_ppl.intValue());
			site.setSname((String)cm.get("SNAME"));
		
			if(!camp.getSiteList().contains(site)) camp.getSiteList().add(site);
		}
		map.put("camp", camp);
		map.put("fac", fac);		
		return map;	
	}
	//캠핑장 메인정보 수정 - 지환 - 
	public Map<String,Object> editmain(CampingMain camp)
	{
		int res = dao.updatemain(camp);
		Map<String,Object> map = new HashMap<>();
		
		if(res>0)
		{
			map.put("updated", true);			
			map.put("url", "/vendor/campingedit");
			map.put("msg", "정보 수정 성공");
		}
		else
		{
			map.put("updated", false);
			map.put("msg", "정보 수정 실패");
		}	
		return map;
	}	
	//캠핑장 부대시설 수정 - 지환 - 
	public Map<String,Object> editfaci(Facilities faci)
	{
		int res = dao.updateFaci(faci);
		Map<String,Object> map = new HashMap<>();
		
		if(res>0)
		{
			map.put("updated", true);			
			map.put("url", "/vendor/campingedit");
			map.put("msg", "정보 수정 성공");
		}
		else
		{
			map.put("updated", false);
			map.put("msg", "정보 수정 실패");
		}	
		return map;
	}	
	
	//캠핑장 사이트정보 수정 - 지환 - 
	public Map<String,Object> editSite(CampingSite site)
	{
		int res = dao.updateSite(site);
		Map<String,Object> map = new HashMap<>();
		
		if(res>0)
		{
			map.put("updated", true);			
			map.put("url", "/vendor/campingedit");
			map.put("msg", "정보 수정 성공");
		}
		else
		{
			map.put("updated", false);
			map.put("msg", "정보 수정 실패");
		}	
		return map;
	}	
	//캠핑장 사이트정보 삭제 - 지환 - 
	public Map<String,Object> delSite(CampingSite site)
	{
		int res = dao.deleteSite(site);
		Map<String,Object> map = new HashMap<>();
		
		if(res>0)
		{
			map.put("deleted", true);			
			map.put("url", "/vendor/campingedit");
			map.put("msg", "삭제 성공");
		}
		else
		{
			map.put("deleted", false);
			map.put("msg", "삭제 실패");
		}	
		return map;
	}	
	//캠핑장 사이트 추가 - 지환 - 
	public Map<String,Object> addSiteOn(CampingSite site)
	{
		int res = dao.addOneSite(site);
		Map<String,Object> map = new HashMap<>();
		
		if(res>0)
		{
			map.put("added", true);			
			map.put("url", "/vendor/campingedit");
			map.put("msg", "추가 성공");
		}
		else
		{
			map.put("deleted", false);
			map.put("msg", "추가 실패");
		}	
		return map;
	}	
	//캠핑장 이미지삭제 - 지환 - 
	public Map<String,Object> delimg(C_Attach ca,String cname)
	{
		String fname = ca.getFname();
		Map<String,Object> map = new HashMap<>();		
		Path filePath=Paths.get("./src/main/resources/static/camping/"+cname+"/"+fname);
		try 
		{
			Files.deleteIfExists(filePath);
			int res = dao.deleteImg(ca);
			if(res>0)
			{
				map.put("deleted", true);			
				map.put("url", "/vendor/campingedit");
			}
			else
			{
				map.put("deleted", false);
				map.put("msg", "삭제 실패");
			}	
			return map;
		} 
		catch (IOException e) {
			
			e.printStackTrace();
		}	
		return map;
	}
	

	//캠핑장 img파일 추가  - 지환
	public String addImg(Map map)
	{
		MultipartFile[] ifiles = (MultipartFile[]) map.get("ifiles");
		MultipartFile[] mapimg = (MultipartFile[]) map.get("mapimg");
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		C_Attach ca = (C_Attach) map.get("ca");	
		String cname = (String)map.get("cname");
				
		int cnum = ca.getCnum();		
		List<C_Attach> list = new ArrayList<>();		
		Map<String,Object> resmap = new HashMap<>();		
								
		try 
		{		
			this.fileStroageLocation=Paths.get("./src/main/resources/static/camping/"+cname).toAbsolutePath().normalize();
			Files.createDirectories(this.fileStroageLocation);				

			if(ifiles[0].getSize()!=0)
			{
				for(int i = 0 ; i<ifiles.length ; i++)
				{						
					C_Attach att = new C_Attach();
					String fname = ifiles[i].getOriginalFilename();		
					att.setCnum(cnum);
					att.setFname(fname);
					
					list.add(att);												
									
					Path targetLocation = this.fileStroageLocation.resolve(fname);
					Files.copy(ifiles[i].getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				}	
			}
			
			if(mapimg[0].getSize()!=0)
			{	
				for(int i = 0 ; i<mapimg.length ; i++)
				{						
					C_Attach att = new C_Attach();
					String fname = "map_"+mapimg[i].getOriginalFilename();		
					att.setCnum(cnum);
					att.setFname(fname);
					
					list.add(att);												
									
					Path targetLocation = this.fileStroageLocation.resolve(fname);
					Files.copy(mapimg[i].getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				}								
			}	
			int res = dao.addImage(list);
			return res>0 ? "파일첨부 성공" : "파일 첨부 실패" ;				
		}
		catch (IOException e) 
		{					
			e.printStackTrace();
		}	
		return "이미지 첨부 실패";		
	}	
	
	
	
	//VENDOR SERVICE에 추가 : ANSWER입력시 알람테이블에 알람항목 추가하는 메소드 -지환-
	public void insertAlarmByAnswer(int unum)
	{
		int res = dao.addAlarmByAnswer(unum);		
	}

	public List<Question> getQuestionList(int cnum)
	{
		return dao.getQuestionList(cnum);
	}
	
	// 로그아웃
	public void logout(HttpSession session)
	{
		session.removeAttribute("vid");
		session.removeAttribute("vnum");
		session.removeAttribute("exist");
	}

	// QnA 답변 등록
	@Transactional
	public boolean addAnswer(Answer answer)
	{
		int unum = dao.getUnumByQ(answer.getQnum());
		
		return dao.addAnswer(answer)>0 && dao.addAlarmByAnswer(unum)>0;
	}
	
   public boolean sendVerifyMessage()
   {
      MimeMessage mimeMessage = sender.createMimeMessage();

      try {
         InternetAddress[] addressTo = new InternetAddress[1];
         addressTo[0] = new InternetAddress("hcsance2@nate.com");

	     mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);
	
	     mimeMessage.setSubject("캠핑어때 메일 인증");
	     
	     mimeMessage.setContent("<a href='http://192.168.18/mail/auth/abc123'>메일주소 인증</a>", "text/html;charset=utf-8");
	     
	     sender.send(mimeMessage);
	     return true;
     
      } catch (MessagingException e) {
    	  log.error("에러={}", e);
      }

      return false;
   }

   
   public boolean  saveToSession(CampingMain main,List <CampingSite> slist,HttpSession session)
   {
	   session.setAttribute("main",main);
	   session.setAttribute("slist", slist);
	   return true;
   }

	public boolean cnameCheck(String cname) 
	{
		List<CampingMain> list = dao.cnameCheck(cname);
		return list.size()>0;
	}
	
	public boolean sendHTMLMessage(String email,String code)
	   {
	      MimeMessage mimeMessage = sender.createMimeMessage();

	      try {
	         InternetAddress[] addressTo = new InternetAddress[1];
	         addressTo[0] = new InternetAddress(email);

	         mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);

	         mimeMessage.setSubject("여기어때 판매자 이메일 인증");
	         
	         mimeMessage.setContent("<h1>인증코드는 :"+code+"</h1><br><h2>인증코드 입력칸에 입력해주세요</h2>", "text/html;charset=utf-8");
	         
	         sender.send(mimeMessage);
	         return true;
	      } catch (MessagingException e) {
	         log.error("에러={}", e);
	      }

	      return false;
	   }
	
	/* --------------------------      부가 메소드            ------------------------------------ */
	
	private int addCattach(CampingMain main,MultipartFile[] cfiles,MultipartFile[] map) 
	{
		String cname = main.getCname();
		int vnum = main.getVnum();
		List<C_Attach> list = new ArrayList<>();
		try {
			this.fileStroageLocation = Paths.get("./src/main/resources/static/camping/"+main.getCname()).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStroageLocation);

			if(cfiles[0].getSize()!=0)
			{
				for(int i=0;i<cfiles.length;i++) {
					 C_Attach att = new C_Attach();
					 String fname = cfiles[i].getOriginalFilename();
					 att.setFname(fname);
					 att.setVnum(vnum);
					 list.add(att);
					 Path targetLocation = this.fileStroageLocation.resolve(fname);
		             Files.copy(cfiles[i].getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				}
			}
			if(map[0].getSize()!=0)
			{
				for(int i=0;i<map.length;i++) {
					 C_Attach att = new C_Attach();
					 String fname = "map_"+map[i].getOriginalFilename();
					 att.setFname(fname);
					 att.setVnum(vnum);
					 list.add(att);
					 Path targetLocation = this.fileStroageLocation.resolve(fname);
		             Files.copy(map[i].getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		int rows = dao.addCattach(list);
		return rows;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	 public String createRandomStr()
	   {
	      UUID randomUUID = UUID.randomUUID();
	      return randomUUID.toString().replaceAll("-", "");
	   }

}