package com.spring.web.user.booking;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.vendor.vo.Booking;

import com.spring.web.vendor.vo.CampingMain;

@Mapper
public interface BookingMapper 
{
	public Booking findBooking(int bnum);	// bnum으로 booking 객체 가져오 - 소영
	
	public int findBnum();	// 예약페이지 위해 시퀀스에서 다음 bnum을 가져온다 - 소영 
	
	public List<Map<String,Object>> campDetail(int cnum);	// camp와 관련된 데이터 불러오기 - 소영
	
	public List<Map<String,Object>> roomAvailable(Date checkin, Date checkout,int cnum);
	
	public Object scoreAvg(int cnum);
	
	public int putBooking(int bnum, int cnum, int unum, int snum,String sname,
			Date checkin, Date checkout, int ppl,int ttlprice);
	
	public Map<String,Object> bookSummary(int bnum);
	
	public Map<String,Object> getFacility(int cnum);	// 체크인, 체크아웃, 캠핑시설 가져오기 - 소영

	
	public int bookConfirm(Booking book);
	
	public CampingMain getCampingMain(int cnum);
}