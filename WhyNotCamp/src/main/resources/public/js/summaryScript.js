const navBtn = document.getElementById('nav-btn');
const cancelBtn = document.getElementById('cancel-btn');
const sideNav = document.getElementById('sidenav');
const modal = document.getElementById('modal');

navBtn.addEventListener("click", function(){
    sideNav.classList.add('show');
    modal.classList.add('showModal');
});

cancelBtn.addEventListener('click', function(){
    sideNav.classList.remove('show');
    modal.classList.remove('showModal');
});

window.addEventListener('click', function(event){
    if(event.target === modal){
        sideNav.classList.remove('show');
        modal.classList.remove('showModal');
    }
});

function formConfirm(form)
{
	var checkBox = document.querySelector(
                'input[name="confirm"]:checked');
         
    if(form.name.value=='')  
    {
		alert('예약자 성함을 입력해주세요');
		return false;
	}
	else if(form.email.value=='')  
    {
		alert('예약자 이메일을 입력해주세요');
		return false;
	}
	else if(form.phone.value=='')  
    {
		alert('예약자 연락처를 입력해주세요');
		return false;
	}               
	else if(checkBox == null)
	{
		alert('예약 동의가 필요합니다');
		return false;
	}
	
}