$(window).load(function(){

    var updateBtn =  $(".btn.btn-success.update");                     //업데이트 버튼
    var removeBtn = $(".btn.btn-danger.remove");                       //삭제버튼
    var downBtn = $(".glyphicon.glyphicon-save.btn-download");         //다운로드 버튼
    var searchBtn = $(".file-search")                                  //파일찾아보기 버튼

    /*********** 게시판 업데이트 버튼 **********/
    updateBtn.click(function(){
        var _curId = $(this).attr("id");
        var _arr = _curId.split(" ");
        updateBoard(_arr[1], _arr[0]);
    });

    /*********** 게시판 삭제 버튼 **********/
    removeBtn.click(function(){
        var _curId = $(this).attr("id");
        var _arr = _curId.split(" ");
        removeBoard(_arr[1], _arr[0]);
    });

    /*********** 검색 버튼 **********/
    searchBtn.click(function(){
        $(this).siblings(".clip_file_hide").trigger('click');
    })

    $('.clip_file_hide').each(function(){
        $(this).change(function(){
            $(".form-control.fileName").val($(this).val());
        })
    })
    /*********** 검색 버튼 끝 **********/

    /*********** 게시판 자료 다운로드 **********/
    downBtn.click(function(){
        alert("AA");
    })
})

//게시판 업데이트
function updateBoard(_id){
    jsRoutes.controllers.Boards.update(_id).ajax({
        type:"POST",
        context: this,
        success: function(data) {
           document.location.href ="/board/notice/" + _id
        },
        error: function() {
            alert("Error!")
        }
    })
}

//게시판 삭제
function removeBoard(_id, _category){
    jsRoutes.controllers.Boards.remove(_id, _category).ajax({
        type:"POST",
        context: this,
        success: function(data) {
            alert("삭제되었습니다.");
            document.location.href = "/board/" + _category + "/page=1"
        },
        error: function() {
            alert("Error!")
        }
    })
}