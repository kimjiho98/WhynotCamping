package com.spring.web.user.search;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.vendor.vo.CampingMain;

@Mapper
public interface SearchMapper 
{
	// 키워드로 검색한 결과 리스트
	List<Map<String,Object>> searchMain(String keyword);
	
	// 유저가 본 캠핑장의 상세 리스트
	public List<Map<String,Object>> usermapdetail(int cnum);
	
	public Float scoreAvg(int cnum);
	
	public List<CampingMain> userRecommend(List<Integer> list);

}

