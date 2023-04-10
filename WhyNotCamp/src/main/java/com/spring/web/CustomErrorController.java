package com.spring.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CustomErrorController implements ErrorController
{
	private String viewPath = "thymeleaf/error/";
	 @GetMapping("/error")
	   public String error(HttpServletRequest request)
	   {
	      Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	      if(status!=null)    //"javax.servlet.error.status_code"
	      {
	         int statusCode = Integer.parseInt(status.toString());

	         if(statusCode==HttpStatus.NOT_FOUND.value()) {
	            return viewPath + "404";
	         }else if(statusCode==HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	            return viewPath + "500";
	         }
	      }
	      return viewPath + "other";
	   }
}
