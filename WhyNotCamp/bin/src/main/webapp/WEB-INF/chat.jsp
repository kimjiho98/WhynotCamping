<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<meta charset="UTF-8">
	<title>Chating</title>
	<style>
		*{
			margin:0;
			padding:0;
		}
		body{
		background-color: rgba(255,217,134,0.68);
		}
		.container{
			width: 500px;
			margin: 0 auto;
			padding: 25px
		}
		.container h1{
			text-align: left;
			padding: 5px 5px 5px 15px;
			color: #ff6a00;
			text-shadow: 2px 2px #0000004a;
			border-left: 3px solid #FFBB00;
			margin-bottom: 20px;
		}
		.chating{
			background-color: #fff;
			width: 500px;
			height: 500px;
			overflow: auto;
			border-radius: 10px;
		}
		.chating .me{
			
			text-align: right;
		}
		.me span{
		padding:0.2em 1em;
			border-color :  #ff6a00;
			border-width : 1px;
			color: #ff6a00;
		}
		.chating .others{
			color: #1e9800;
			text-align: left;
		}
		input{
			width: 330px;
			height: 25px;
		}
	</style>
</head>

<script type="text/javascript">
	var ws;

	function wsOpen(){
		ws = new WebSocket("ws://" + location.host + "/chating");
		wsEvt();
	}
		
	function wsEvt() {
		ws.onopen = function(data){
			//소켓이 열리면 동작
		}
		
		ws.onmessage = function(data) {
			//메시지를 받으면 동작
			var msg = data.data;
			if(msg != null && msg.trim() != ''){
				var d = JSON.parse(msg);
				if(d.type == "getId"){
					var si = d.sessionId != null ? d.sessionId : "";
					if(si != ''){
						$("#sessionId").val(si); 
					}
				}else if(d.type == "message"){
					if(d.sessionId == $("#sessionId").val()){
						$("#chating").append("<p class='me'><span>나 :" + d.msg + "</span></p>");	
					}else{
						$("#chating").append("<p class='others'>" + d.userName + " :" + d.msg + "</p>");
					}
						
				}else{
					console.warn("unknown type!")
				}
			}
		}

		document.addEventListener("keypress", function(e){
			if(e.keyCode == 13){ //enter press
				send();
			}
		});
	}
$(function(){
	wsOpen();
})
	function chatName(){
		var userName = "${nickname}";
		if(userName == null || userName.trim() == ""){
			alert("사용자 이름을 입력해주세요.");
			$("#userName").focus();
		}else{
			wsOpen();
			$("#yourName").hide();
			$("#yourMsg").show();
		}
	}

	function send() {
		var option ={
			type: "message",
			sessionId : $("#sessionId").val(),
			userName : "${nickname}",
			msg : $("#chatting").val()
		}
		console.log(option.msg);
		if(option.msg!='')
		{
		ws.send(JSON.stringify(option))
		$('#chatting').val("");
		}
	}
</script>
<body>
	<div id="container" class="container">
		<h1>${product}</h1>
		<input type="hidden" id="sessionId" value="">
		
		<div id="chating" class="chating">
		</div>
		
		<div id="yourMsg">
			<table class="inputTable">
				<tr>
					<th>메시지</th>
					<th><input id="chatting" placeholder="보내실 메시지를 입력하세요."></th>
					<th><button onclick="send()" id="sendBtn">보내기</button></th>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>