
var keyword_del = $('.keyword_del');

var search_input = $('.search_input');

function mainsearchCheck(f)
{
   if (f.keyword.value == '')
   {
      botbox.text( search_error[Math.floor(Math.random() * search_error.length )]);
      f.keyword.value='';
      return false;
   }
}



function keyword_del_func () 
{
   search_input.val('');
   keyword_del.hide();
}
   
function searchFind() 
{
   var search_input = $('.search_input');
   var searchField = search_input.val();

   if(!searchField) return false;
   var expression = new RegExp(searchField, "i");
   var index = 0;
   $.getJSON('/layouts/5g/_var/search_list.json?2023011109', function(data) {
      $.each(data, function(key, value){
         if (value.subject.search(expression) != -1 || value.cate1.search(expression) != -1 || value.cate2.search(expression) != -1 || value.cate3.search(expression) != -1 || value.tag.search(expression) != -1){

            if(value.thumbnail == 'undefined') {
                var thumb2 = "/layouts/5g/image/nothumb.svg";
            } else {
                var thumb2 = value.thumbnail;
            }

            result.append(
               '<li class="">'+
               '<img src="'+thumb2+'" width="80" height="45" class="img-thumbnail" />'+
               '<div class="cright hand">'+
               '   <p class="cpath">'+value.cate1+' > '+value.cate2+'  > '+value.cate3+'</p>'+
               '   <p class="sbjval">'+value.subject+'</p>'+
               '</div>'+
               '<div class="clink">'+
               '   <a href="./?c=camping&m=camping&campID='+value.uid+'" class="cdirectlink"><img src="/layouts/5g/image/direct.svg" width="26" height="26" title="'+value.subject+'" alt=""/></a>'+
               '</div>'+
               '</li>'
            );
            index++;
         } 
         if(index > 99) return false;
      });   
      if(searchField) botbox.html("<span class='b blue'>'"+searchField+"'</span>   <span class='b'>"+index+"건</span>이 검색되었네요");
   });
}

function mainsearchCheck(f)
{
   if (f.keyword.value == '')
   {
      botbox.text( search_error[Math.floor(Math.random() * search_error.length )]);
      f.keyword.value='';
      return false;
   }
}
