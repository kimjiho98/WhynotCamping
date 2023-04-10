package com.spring.web.user.vo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(exclude = {"cnum","unum","bnum","content","score","regdate","nickname","attList"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {

	private int rnum;
	private int cnum;
	private int unum;
	private int bnum;
	private String nickname;
	private String content;
	private int score;
	private Date regDate;
	private List<R_Attach> attList = new ArrayList<>();
	
}
