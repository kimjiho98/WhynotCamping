package com.spring.web.user.userInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.web.user.vo.Alarm;
import com.spring.web.user.vo.Question;
import com.spring.web.user.vo.R_Attach;
import com.spring.web.user.vo.Review;
import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Answer;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.CampingSite;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class InfoService {	
	
	//inject - 지환
	@Autowired
	private InfoMapper mapper;	

	//inject - 지환
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	private Path fileStroageLocation;
	
	//내정보 보기 - 지환
	public User myinfo(int unum)
	{
		User user = mapper.getMyInfo(unum);		
		return user;
	}
	
	//내비밀번호 수정 - 지환
	public Map<String,Object> updatePw(User user,String pw)
	{
		Map<String,Object> map = new HashMap<>();
		User userInfo = mapper.userPwdCheck(user.getUnum());
		
		if(user.getPwd().equals(userInfo.getPwd()))
		{
			User nuser = new User();
			nuser.setUnum(user.getUnum());
			nuser.setPwd(pw);
			int res2 = mapper.pwdUpdated(nuser);
						
			BCryptPasswordEncoder enc = new BCryptPasswordEncoder();	
			nuser.setPwd(enc.encode(pw));
			nuser.setUuid(user.getUuid());

			int res3 = mapper.pwdUpdatedToCampUser(nuser);
						
			if(res2>0&&res3>0)
			{
				map.put("msg", "새 비밀번호 설정을 마쳤습니다.");
				map.put("updated", true);
			}
			else
			{	
				map.put("msg", "비밀번호 수정에 실패하였습니다.");
				map.put("updated", false);
			}	
			
			return map;
			
		}
		else
		{				
			map.put("msg","현재 비밀번호가 일치하지 않습니다.");
			map.put("updated", false);			
			return map;
		}
	}
	
	//유저 연락처 및 주소 수정 - 지환 -
	public Map<String,Object> upPhoneAdd(User user)
	{
		int updated = mapper.updatePA(user);
		Map<String,Object> map = new HashMap<>();
		if(updated>0)
		{
			map.put("updated", updated);
			map.put("msg", "수정성공");
		}
		else
		{
			map.put("updated", updated);
			map.put("msg", "수정실패");
		}
		
		return map;
	}
	
	//회원탈퇴 - 지환 (만드는중 아직 안됨!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!)
	public Map<String,Object> outUser(int unum)
	{
		User user = mapper.getMyInfo(unum);		
		User bye = new User();
		bye.setUuid(user.getUuid());
		
		int res= mapper.byeUser(bye);
		
		Map<String,Object> map = new HashMap<>();
		
		return map;
	}
	
	
	
	//인증메일에 담을 내용 -지환
	 public boolean sendHTMLMessage(String email,String code)
	 {
		 MimeMessage mimeMessage = sender.createMimeMessage();

	      try {
	         InternetAddress[] addressTo = new InternetAddress[1];
	         addressTo[0] = new InternetAddress(email);

	         mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);
	         mimeMessage.setSubject("캠핑어때 이메일 인증");	         
	         mimeMessage.setContent("<h1>인증코드 :"+code+"</h1><br>", "text/html;charset=utf-8");
	         
	         sender.send(mimeMessage);
	         return true;
	      } catch (MessagingException e) {
	         log.error("에러={}", e);
	      }

	      return false;
	 } 	 
	 
	 // 메일 인증코드 만들기 - 지환
	 public String createRandomStr()
	{
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("-", "");
	}
	 
	 //메일인증 후 새로운 메일로 변경 - 지환
	 public boolean updateEmail(int unum, String email)
	 {
		 User user = new User();
		 user.setUnum(unum);
		 user.setEmail(email);
		 
		 int res = mapper.emailUpdate(user);
		 
		 return res>0;
	 }
	
	 
	 
	 
	 
	 
	 
	 
	
	//유저 예약 정보 - 지환
	public List<Map<String,Object>> bookList(int unum)
	{			
		List<Map<String,Object>> mlist = mapper.bookingList(unum);
		List<Map<String,Object>> list = new ArrayList<>();
			
		for(int i=0;i<mlist.size();i++)
		{
			Map<String,Object> map = new HashMap<>();
			Map<String,Object> m = mlist.get(i);
			
			Booking booking = new Booking();
			CampingMain cm = new CampingMain();
			
			BigDecimal snum = (java.math.BigDecimal)m.get("SNUM");
			BigDecimal bnum = (java.math.BigDecimal)m.get("NUM");
			BigDecimal ppl = (java.math.BigDecimal)m.get("PPL");
			BigDecimal price = (java.math.BigDecimal)m.get("TTLPRICE");
			BigDecimal confirm = (java.math.BigDecimal)m.get("CONFIRM");
			BigDecimal cnum = (java.math.BigDecimal)m.get("CNUM");
			
			Timestamp tcheckin =(java.sql.Timestamp)m.get("CHECKIN");
			Date checkin = new Date(tcheckin.getTime());
			
			Timestamp tcheckout =(java.sql.Timestamp)m.get("CHECKOUT");
			Date checkout = new Date(tcheckout.getTime());
			
			Timestamp tregDate =(java.sql.Timestamp)m.get("REGDATE");
			Date regDate = new Date(tregDate.getTime());
						
			booking.setBnum(bnum.intValue());
			booking.setCheckin(checkin);
			booking.setCheckout(checkout);
			booking.setRegDate(regDate);
			booking.setPpl(ppl.intValue());
			booking.setTtlprice(price.intValue());
			booking.setConfirm(confirm.intValue());
			booking.setSnum(snum.intValue());
			booking.setName((String) m.get("NAME"));
			booking.setPhone((String) m.get("PHONE"));			
			
			cm.setCname((String) m.get("CNAME"));
			cm.setCnum(cnum.intValue());
						
			Calendar cal = Calendar.getInstance();
					
			Calendar revdate = Calendar.getInstance();
			revdate.setTime(checkout);
			revdate.add(Calendar.DATE, 14);
			
			boolean revready = revdate.compareTo(cal)>=0&&confirm.intValue()==1 ;			
			Review rbb = mapper.reviewByBnum(bnum.intValue());

			if(rbb!=null)
			{
				revready = false;
			}						
			map.put("revready", revready);

			cm.getSiteList().add(((CampingSite) m.get("SITE")));

			map.put("booking", booking);
			map.put("campingarea", cm);			
			
			list.add(map);
		}							
		return list;
	}	

	//cnum으로 캠핑장정보 가져오기 - 지환
	public CampingMain getCamp(int cnum)
	{
		CampingMain camp =mapper.getCamp(cnum);		
		log.info("serv 2 : " + camp.getCname());
		return camp;
	}
	
	//알람있는지 체크 - 지환
	public List<Map<String,Object>> getAlarm(int unum)
	{
		List<Alarm> alist = mapper.alarmCheck(unum);
		List<Map<String,Object>> list = new ArrayList<>();
		
		if(alist.size()>0)	
		{
			for(int i = 0 ; i < alist.size() ; i++)
		    {	
				Map<String,Object> map = new HashMap<>();
				Alarm alarm = alist.get(i);
				map.put("alarm", alarm);
				
				if(alarm.getAvsr()==1)
				{
					map.put("msg","올리신 질문에 답변이 등록되었습니다.");
					map.put("url", "/userinfo/myqalist");
				}	
				else if(alarm.getAvsr()==2)
				{
					map.put("msg","다녀오신 캠핑장에 리뷰를 남겨주세요.");
					map.put("url", "/userinfo/mybooking");
				}				
				list.add(map);
		    }
		}				
		return list;	
	}	
	
	
	public void delAlarm(Alarm alarm)
	{
		mapper.deleteAlarm(alarm);
	}
	

	//내가 갔었던 곳 리뷰작성하기  - 지환
	@Transactional
	public String addReview(Map map)
	{
		MultipartFile[] rfiles = (MultipartFile[]) map.get("rfiles");
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		Review review = (Review) map.get("review");	
		
		int bnum = review.getBnum();
		int unum = review.getUnum();
		int cnum = review.getCnum();

		int res1 = mapper.addReview(review);
		List<R_Attach> list = new ArrayList<>();
		
		Map<String,Object> resmap = new HashMap<>();		
		
		if(rfiles[0].getSize()!=0)
		{						
			try 
			{		
				this.fileStroageLocation=Paths.get("./src/main/resources/static/review/"+bnum).toAbsolutePath().normalize();
				Files.createDirectories(this.fileStroageLocation);
				
				for(int i = 0 ; i<rfiles.length ; i++)
				{						
					R_Attach att = new R_Attach();
					String fname = rfiles[i].getOriginalFilename();						
					att.setCnum(cnum);
					att.setUnum(unum);
					att.setBnum(bnum);
					att.setFname(fname);
					
					list.add(att);												
									
					Path targetLocation = this.fileStroageLocation.resolve(fname);
					Files.copy(rfiles[i].getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				}												
			}
			catch (IOException e) 
			{					
				e.printStackTrace();
			}	
			int res2 = mapper.addratt(list);
			return res2>0 ? "게시글 등록 및 파일첨부 성공" : "파일 첨부 실패" ;		
		}		
		else return res1>0 ? "게시글 작성 성공" : "게시글 작성 실패";		
	}	
	

	
	//내가 쓴 리뷰보기  - 지환
	public List<Map<String,Object>> myreview(int unum)
	{
		List<Map<String,Object>> mlist = mapper.myreviewList(unum);
		
		List<Map<String,Object>> list = new ArrayList<>();
		
		for(int i=0;i<mlist.size();i++)
		{
			Map<String,Object> map = new HashMap<>();
			Map<String,Object> m = mlist.get(i);
			
			Review rv = new Review();
			Booking bk = new Booking();
			
			BigDecimal rnum = (java.math.BigDecimal)m.get("RNUM");
			BigDecimal score = (java.math.BigDecimal)m.get("SCORE");
			Timestamp bcheckin =(java.sql.Timestamp)m.get("CHECKIN");
			Date checkin = new Date(bcheckin.getTime());
			
			Timestamp bcheckout =(java.sql.Timestamp)m.get("CHECKOUT");
			Date checkout = new Date(bcheckout.getTime());
			
			Timestamp tregDate =(java.sql.Timestamp)m.get("REGDATE");
			Date regDate = new Date(tregDate.getTime());			
						
			rv.setRnum(rnum.intValue());
			rv.setScore(score.intValue());
			rv.setRegDate(regDate);		
			rv.setContent((String)m.get("CONTENT"));
			bk.setCheckin(checkin);
			bk.setCheckout(checkout);
			
			String cname = (String) m.get("CNAME");
						
			map.put("booking", bk);
			map.put("cname", cname);	
			
			boolean found = false;
			String sFname = (String) m.get("FNAME");	
				
			if(sFname==null)
			{
				map.put("review", rv);
				list.add(map);
				if(!found) continue;
			}			
			
			String[] file = sFname.split(",");
									
			for(int j =0; j<file.length; j++)
			{			 
			    R_Attach att = new R_Attach();
			    
			    att.setFname(file[j]);			    
				BigDecimal bnum = (java.math.BigDecimal)m.get("BNUM");				
				att.setRnum(rnum.intValue());
				att.setBnum(bnum.intValue());
				att.setUnum(unum);
				
				rv.getAttList().add(att);				
				if(!(rv.getAttList().contains(att))) rv.getAttList().add(att);
			}	
					
			map.put("review", rv);			
			list.add(map);			
		}			
		return list;	
	}	
	
	//Question넣기
	public Map<String,Object> addQuestion(Question q)
	{
		log.info("퀘스쳔" + q.toString());
		
		Map<String,Object> map = new HashMap<>();
		int res = mapper.addQuestion(q);
			
		if(res>0)
		{
			map.put("msg", "질문등록 성공");
			map.put("added", true);
		}
		else
		{			
			map.put("msg", "질문등록 실패");
			map.put("added", false);
		}			
		return map;
	}
	
	
	//Q&A보기  - 지환
	public List<Map<String,Object>> userQAList(int unum)
	{
		List<Map<String,Object>> list = new ArrayList<>();
		List<Map<String,Object>> mlist = mapper.myqaList(unum);
				
		for(int i = 0; i<mlist.size() ; i++)
		{
			Map<String,Object> map = new HashMap<>();
			Map<String,Object> m = mlist.get(i);
			Question q = new Question();
			Answer a = new Answer();
			CampingMain c = new CampingMain();
			
			BigDecimal qnum = (java.math.BigDecimal)m.get("QNUM");
			BigDecimal cnum = (java.math.BigDecimal)m.get("CNUM");
						
			Timestamp q_regdate =(java.sql.Timestamp)m.get("QREGDATE");
			Date qregdate = new Date(q_regdate.getTime());
			
			q.setUnum(unum);
			q.setCnum(cnum.intValue());
			q.setQnum(qnum.intValue());
			q.setQRegDate(qregdate);		
			q.setQuestion((String)m.get("QUESTION"));
					
			map.put("question", q);
			map.put("cname", m.get("CNAME"));			
			
			BigDecimal asnum = (java.math.BigDecimal)m.get("ASNUM");
			if(asnum==null)
			{
				map.put("answer", a);
				list.add(map);
				continue;
			}						
			Timestamp a_regdate =(java.sql.Timestamp)m.get("AREGDATE");
			Date aregdate = new Date(a_regdate.getTime());
						
			a.setAsnum(asnum.intValue());
			a.setARegDate(aregdate);
			a.setAnswer((String)m.get("ANSWER"));
			
			map.put("answer", a);			
			list.add(map);
		}		
		return list;
	}
	
}
