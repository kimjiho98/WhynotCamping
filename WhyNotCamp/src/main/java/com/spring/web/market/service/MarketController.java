package com.spring.web.market.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.web.vendor.service.VendorController;
import com.spring.web.vendor.service.VendorService;

import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;

import com.spring.web.market.vo.Market;


import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/market")
@Slf4j
public class MarketController {
   @Autowired
	public MarketService ms;
   
   @Autowired
   public HttpSession session;
   
   @GetMapping("/home") //홈으로 이동 -지호
   public String main(HttpSession session, Model m)
   {
	   this.session = session;
	   List<Map<String,Object>> list = ms.getform();
	   m.addAttribute("list",list);
	   return "thymeleaf/market/Markethome";
   }
 
   @GetMapping("/sellform") //판매글로이동 -지호
   public String sale()
   {	  
	   return "thymeleaf/market/sellform";
   }
   
   @PostMapping("/save") //파일저장기능 -지호
   @ResponseBody
   public Map<String,Boolean> save(Market market , @RequestParam("fname")MultipartFile[] file)
   {
	   ms.addMarketform(market);
	   Map<String,Boolean> map=new HashMap<>();
	   if(file[0].getSize()!=0)
	   { 
		  int row= ms.addMattach(market, file);
	      if(row>0)
	      {   boolean checked= true;
	      		map.put("completed", checked);
	    	  return map;
	      }
	      else 
	      {
	    	  boolean checked=false;
	    	  map.put("completed", checked);
	    	  return map;
	      }
	   }
	   boolean checked= true;
 		map.put("completed", checked);
	   return map;
   }
 @GetMapping("purchase") //초기 구매페이지 -지호
 public String purchasepage(Model m)
 {	
	 List<Map<String, Object>> mlist=ms.purchase_list();
	 m.addAttribute("list",mlist);
	 return "thymeleaf/market/Purchase";
 }
 
 @PostMapping("deepinfo") //상세검색로직 -지호
 public String deepinfo(Market market, Model m)
 {		
	 List<Map<String,Object>> mlist=ms.deepinfo(market);
	 m.addAttribute("list",mlist);
	return "thymeleaf/market/Purchase";
 }
@GetMapping("detail") //상품정보-지호
public String detail(Market market,Model m)
{
	List<Map<String,Object>> mlist=ms.detail(market);
	m.addAttribute("lists",mlist);
	return "thymeleaf/market/Detail";
}
 
}

