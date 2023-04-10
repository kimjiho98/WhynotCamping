package com.spring.web.market.vo;

import java.sql.Blob;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.spring.web.user.vo.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Market {
		private int mnum;
		private String	title;
		private String	author;
		private String	price ;
		private String	content;
		private Date	regdate;
        private String	category;
        private String	region;
        
        
		private int unum; //데이터테이블 쿼리비교용
		private List<M_Attach> attlist =new ArrayList<>();
	}

