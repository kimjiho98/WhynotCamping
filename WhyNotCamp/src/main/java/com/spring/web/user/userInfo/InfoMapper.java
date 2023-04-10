package com.spring.web.user.userInfo;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.user.vo.Alarm;
import com.spring.web.user.vo.Question;
import com.spring.web.user.vo.R_Attach;
import com.spring.web.user.vo.Review;
import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.CampingMain;



@Mapper
public interface InfoMapper {

	//0. 유저정보보기 - 지환
	public User getMyInfo(int unum);
	
	//1-1. 유저비밀번호확인 - 지환
	public User userPwdCheck(int unum);
	
	//1-2-1. 유저비밀번호수정 - 지환
	public Integer pwdUpdated(User user);
	
	//1-2-2. Camp_User 테이블의 암호화 수정 - 지환
	public Integer pwdUpdatedToCampUser(User user);
	
	//1-3. 유저메일수정 - 지환
	public Integer emailUpdate(User user);
	
	//1-4. 유저 주소 연락처 수정 -지환-
	public Integer updatePA(User user);
	
	//1-5. 회원탈퇴 -지환-
	public Integer byeUser(User user);	
	
	//2.유저가 예약한 캠핑장 리스트 - 지환
	public List<Map<String,Object>> bookingList(int unum); 
	
	//3.cnum으로 캠핑장정보 가져오기 - 지환
	public CampingMain getCamp(int cnum);
		
	//4-1.알람여부 체크하기	 - 지환
	public List<Alarm> alarmCheck(int unum);
	
	//4-1-2. 리뷰를 쓴 예약정보인지.
	public Review reviewByBnum(int bnum);
	
	//4-1-3) 알람 삭제
	public Integer deleteAlarm(Alarm alarm);
	
	//4-2. 내가 갔었던 곳 리뷰작성하기 - 지환
	public Integer addReview(Review review);
	
	//4-3 리뷰에 첨부파일 등록하기
	public Integer addratt(List<R_Attach> list);
		
	//5. 내가 쓴 리뷰보기 - 지환
	public List<Map<String,Object>> myreviewList(int unum);
		
	//6-1) Question 입력
	public Integer addQuestion(Question q);
	
	//6-2) 내가 쓴 Q&A보기 - 지환
	public List<Map<String,Object>> myqaList(int unum);
	
}
