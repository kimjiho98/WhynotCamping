package com.spring.web.user.userInfo;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener //리스너로 등록했다는 것은 세션이 생기자마자 바로 감청해서 아래들을 싹.
public class HttpSessionHandler implements HttpSessionListener
{
	
	public static Map<String,HttpSession> map = new HashMap<>(); //public static이기 때문에 언제나 접근하기가 쉽다.
	
	@Override
	public void sessionCreated(HttpSessionEvent se) //세션이 생성되자마자 발동.
	{
		HttpSession s = se.getSession();
		map.put(s.getId(), s);
		log.info("세션 생성 : " + s.getId());
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent se) //세션이 사라지기 전에 해야할 일들.
	{
		HttpSession s = se.getSession();
		map.remove(s.getId());
	}
	
	
}
