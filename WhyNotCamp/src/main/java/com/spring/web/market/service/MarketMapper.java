package com.spring.web.market.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.market.vo.M_Attach;
import com.spring.web.market.vo.Market;



@Mapper
public interface MarketMapper {

	public int addmarketform(Market market);
	public int addmattach(List<M_Attach> list);
	public List<Map<String,Object>> getform ();
	public List<Map<String,Object>> purchase_list();
	public List<Map<String,Object>> deepinfo(Market market);
	public List<Map<String,Object>> detail(Market market);
}
