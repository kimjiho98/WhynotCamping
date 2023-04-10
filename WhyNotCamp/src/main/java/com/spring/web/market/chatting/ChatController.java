package com.spring.web.market.chatting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ChatController {

	@Autowired
	private javax.servlet.http.HttpSession session;
	
	static int roomNumber = 0;
	
	@RequestMapping("/chat")
	public ModelAndView chat(@RequestParam String nickname) {
				
		ModelAndView mv = new ModelAndView();
			
		mv.setViewName("chat");
		mv.addObject("nickname", nickname);
		mv.addObject("product","27인치 모니터");
		return mv;
	}
}
