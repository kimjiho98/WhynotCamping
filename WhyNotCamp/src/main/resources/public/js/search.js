function mainsearchCheck(f)
{
	if (f.keyword.value == '')
	{
		botbox.text('검색어를 입력하세요');
		f.keyword.value='';
		return false;
	}
}


var botbox = $("#bot");
var keyword_del = $('.keyword_del');

var result = $('#result');
var search_input = $('.search_input');

$(document).ready(function(){
	
	search_input.keyup(delay(function (e) {
		if(!search_input.val()) keyword_del_func ();
		searchFind();
		if(search_input.val()) {
			keyword_del.show();
		} else {
			keyword_del.hide();
		}

	}, 300));

	keyword_del.click(function() {
		keyword_del_func();
	});
});

function keyword_del_func () {
	search_input.val('');
	keyword_del.hide();
	if(!search_input.val()) botbox.text(search_end[Math.floor(Math.random() * search_end.length )]);
}
