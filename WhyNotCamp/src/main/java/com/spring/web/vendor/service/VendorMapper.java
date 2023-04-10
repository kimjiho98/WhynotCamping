package com.spring.web.vendor.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.user.vo.Question;
import com.spring.web.vendor.vo.Answer;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.C_Attach;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.CampingSite;
import com.spring.web.vendor.vo.Facilities;
import com.spring.web.vendor.vo.Vendor;

@Mapper
public interface VendorMapper 
{

	// vnum으로 해당 vendor객체 리턴
	public Vendor getVendor(int vnum); 
	// vendor 추가
	public int addVendor(Vendor vendor);
	// vid로 vendor 찾기
	public Vendor getVendorByVid(String vid); 
	// vid 중복체크
	public List<Vendor> vidCheck(String vid); 
	// vnum에 해당하는 BookingList 리턴
	public List<Booking> getBookingList(int vnum); 
	// vnum에 해당하는 캠핑지 정보들 리턴
	public List<Map<String,Object>> getMainList(int vnum); 
	// 캠핑지 DB에 추가
	public int addCamping(CampingMain main); 
	// 첨부파일 DB에 추가
	public int addCattach(List<C_Attach> list); 
	// 예약승인 
	public int bookingConfirm(int bnum); 
	// cnum으로 질문 답변 가져오기
	public List<Map<String,Object>> getQnAByVnum(int vnum); 
	//  해당 캠핑장 QnA 리스트 불러오기
	public List<Question> getQuestionList(int cnum);
	// 캠핑장 리스트 불러오기 (보류)
	public List<CampingMain> getCampingList(int vnum);
	// 답변 등록
	public int addAnswer(Answer answer);
	// 메인페이지 정보
	public List<Map<String,Object>> getMain(int vnum);
	// 부대시설 등록
	public int addFacilities(Map<String,Object> m);
	// 캠핑지 정보 가져오기
	public CampingMain getCampingMain(int vnum);

	//CAMPING 상세정보 -지환-
	public List<Map<String,Object>> campingDetail(int vnum);
	//CAMPINGMAIN 수정 -지환-
	public int updatemain(CampingMain main);
	//캠핑 부대시설 수정 -지환-
	public int updateFaci(Facilities faci);		
	//캠핑 사이트 수정 -지환-
	public int updateSite(CampingSite site);
	//캠핑 사이트 삭제 -지환-
	public int deleteSite(CampingSite site);
	//캠핑 사이트 추가 -지환-
	public int addOneSite(CampingSite site);
	//캠핑장 첨부사진 삭제 -지환-
	public int deleteImg(C_Attach ca);	
	//캠핑장 첨부사진 추가 -지환-
	public int addImage(List<C_Attach> list);
	//VENDOR MAPPER에 추가 : 답변달았을 시 알람 추가  -지환-
	public int addAlarmByAnswer(int unum);
	//qnum으로 anum 가져오기 - 지환-
	public int getUnumByQ(int qnum);	
	// Camp_User에 암호화된 정보 저장 - 지환-
	public int addCampUser(Vendor vendor);
	// 벤더 권한 부여 -지환- 
	public int addAuthorities(Vendor vendor);
	

	// 캠핑지 이름 중복체크
	public List<CampingMain> cnameCheck(String cname);
	// 사이트 등록
	public int addSite(List<CampingSite> list);
}
