	function add_siteForm()
	{
		var siteForm = '<form id="site'+num+'">'+
		'<input type="hidden" name="snum" value="'+num+'">'+
		'사이트 이름: <input type="text" id="sname" name="sname" value="동백">'+
		'최대인원: <input type="number" name="max_ppl" min="1" value="1">'+
		'가격: <input type="number" name="price" min="0" value="0" step="1000">'+
		'</form>'
		$('#site').append(siteForm);
		num += 1;
	}
	
	function remove_siteForm()
	{
		if(num>1)
		{
			$('#site'+(num-1)).remove();
			num -= 1;
		}
	}

function register()
{
	var form = $('#regForm')[0];
	var data = new FormData(form);
	
	var snum_list = [];
	var max_ppl_list = [];
	var price_list = [];
	var sname_list = new Array();
		
	for(var i=1;i<num;i++)
	{
		var snum = $('#site'+i+' [name="snum"]').val();
		var max_ppl = $('#site'+i+' [name="max_ppl"]').val();
		var price = $('#site'+i+' [name="price"]').val();
		var sname = $('#site'+i+' #sname').val();
		
		snum_list.push(snum);
		max_ppl_list.push(max_ppl);
		price_list.push(price);
		sname_list.push(sname);
	}
	data.append("snum_list",snum_list);
	data.append("max_ppl_list",max_ppl_list);
	data.append("price_list",price_list);
	data.append("sname_list",sname_list);
		
	if(confirm('다음 단계로 넘어가시겠습니까?')){
		$.ajax({
	 		url : '/vendor/reg',
	 		method:'post',
	 		contentType : false ,
	 		processData : false,
	 		enctype: 'multipart/form-data',
	 		data : data,
	 		dataType : 'text',
	 		timeout: 600000,
	 		success : function(res)	{
	 				location.href='/vendor/reg2';
	 		},
	 		error : function(xhr,status,err){
	 			alert(err);
	 		}
	 	});
 	}
}



function cname_check() {
	var cname = $('#cname').val();
	var dat = {"cname":cname};
	$.ajax({
 		url : '/vendor/cname_check',
 		method:'post',
 		data: dat,
 		dataType : 'json',
 		success : function(res)
 		{
 			if(!res.ok)	{
 				alert("사용가능한 상호명 입니다");
				check=true;
 			}else alert('이미 사용중인 상호명 입니다');
 		},
 		error : function(xhr,status,err){
 			alert(err);
 		}
 		
 	});
}

//주소-좌표 변환 객체 생성
var geocoder = new kakao.maps.services.Geocoder();

//주소 검색창
function find_address(){
    new daum.Postcode({
        oncomplete: function(data) {
        	var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if(data.userSelectedType === 'R'){
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if(extraAddr !== ''){
                    extraAddr = ' (' + extraAddr + ')';
                }
            
            }

            // 주소 정보를 해당 필드에 넣는다.
           
            document.getElementById("address").value = addr;
            
           $("#address").prop("readonly",true);
            var juso =  addr;
        	geocoder.addressSearch(juso, callback);  
        }
    }).open();
}
// 주소 정보에 해당하는 좌표값을 요청 후 저장
var callback = function(result, status) {
	if (status === kakao.maps.services.Status.OK) {
	   var lng = $("#lng").val(result[0].x); 
	   var lat = $("#lat").val(result[0].y);
	}
};



function unbind(){
    checkUnload = false;
};
