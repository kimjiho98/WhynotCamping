package com.spring.web.user.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.web.vendor.vo.CampingMain;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/search")
@Controller
@SessionAttributes("unum")
public class SearchController 
{
	@Autowired
	private SearchService svc;
	
	@Autowired
	private HttpSession session;
	
	// main화면에서 검색하면 campingmain 리스트를 리턴한다 - 소영
	@GetMapping("/camp")	
	public String searchMain(@RequestParam String keyword, Model m)
	{
		List<Map<String,Object>> list = svc.searchMain(keyword);
		String jsStr = JSONArray.toJSONString(list);	//list를 json문자열로 변환	
				
		m.addAttribute("list", jsStr.replaceAll("\"","\'"));	// 오류나서 따옴표 바꿨음
		
		if(session.getAttribute("unum")!=null)
		{		
			List<CampingMain> pythonResult = svc.recommendList((int)session.getAttribute("unum"));
			m.addAttribute("recommendList",pythonResult);
		}
		return "thymeleaf/map/map";
	}
	
	// 유저 상세 디테일 - 현명
		@GetMapping("/detail/{cnum}")
		@Transactional
		public String usermapdetail(@PathVariable int cnum, Model m)
		{
			Map<String,Object> detail = svc.usermapdetail(cnum);
			m.addAttribute("m",detail.get("main"));   //메인
			m.addAttribute("f",detail.get("fac"));    // 카테고리 
			m.addAttribute("score",detail.get("score"));    //평점
			
			return "thymeleaf/user/usermapDetail";
		}
		
	
		

}
