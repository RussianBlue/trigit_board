$(window).load(function(){
    //게시판 관련
    //$('p.text-warning').hide();
    var updateBtn =  $(".btn.btn-success.update");                     //업데이트 버튼
    var removeBtn = $(".btn.btn-danger.remove");                       //삭제버튼
    var downBtn = $(".glyphicon.glyphicon-save.btn-download");         //다운로드 버튼
    var searchBtn = $(".file-search")                                  //파일찾아보기 버튼

    //유저 관련
    var userNewBtn = $('.btn-newuser');
    var userEditBtn = $('.btn-useredit');
    var userDeleteBtn = $('.btn-userdelete');
    var userUpdatebtn = $('.btn-editUser');

    var authSelect = $('select[name~="auth"]');

    $('p.text-warning.user_id').hide();
    //유저 생성
    userNewBtn.click(function(){
       var _user_id = $('input[name~="user_id"]')
       var _password = $('input[name~="password"]')
       var _name = $('input[name~="user_name"]')
       var _email = $('input[name~="email"]')
       var _project_id = $('input[name~="project_id"]');
       var _user_type = $('select[name~="auth"]');
       
       hasError(_user_id.parent().parent(), _user_id.val())
       hasError(_password.parent().parent(), _password.val())
       hasError(_name.parent().parent(), _name.val())
       hasError(_email.parent().parent(), _email.val())
       hasError(_project_id.parent().parent(), _project_id.val())

       if(_user_id.val() != "" && _password.val() != "" && _name.val() != "" && _email.val() != ""){
            if(confirm("등록하시겠습니까?")){
                newUser(_user_id.val(), _email.val(), _name.val(), _password.val(), _user_type.val(), _project_id.val()) 
            }
       }
    });

    userUpdatebtn.click(function(){
       var _curId = $(this).attr("id");      
       
       var _password = $('input[name~="password"]').val()
       var _name = $('input[name~="user_name"]').val()
       var _email = $('input[name~="email"]').val()
       
       editUser(_curId, _password, _email, _name);
    })

    userDeleteBtn.click(function(){
        var _curId = $(this).attr("id");
        if(confirm("삭제하시겠습니까?")){
            deleteUser(_curId);
        }        
    })

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
        
    })
})

function hasError(_target, _text){
    if(_text == ""){
        _target.addClass("has-error");      
    }else{
        if(_target.hasClass("has-error")){
            _target.removeClass("has-error");
        }
    }
}

function newUser(_id, _email, _name, _password, _user_type, _project_id){    
   jsRoutes.controllers.Admin.newUser(_id, _email, _name, _password, _user_type, _project_id).ajax({
        type:"POST",
        context: this,
        success: function(data) {
           document.location.href ="/admin/userList/page=1"
        },
        error: function() {
            $('p.text-warning.user_id').show();
        }
    }) 
}

function editUser(_id, _password, _email, _name){    
   jsRoutes.controllers.Admin.editUser(_id, _password, _email, _name).ajax({
        type:"POST",
        context: this,
        success: function(data) {
           document.location.href ="/admin/userList/page=1"
        },
        error: function() {
            alert("Error!")
        }
    }) 
}
//
function deleteUser(_id){
    jsRoutes.controllers.Admin.removeUser(_id).ajax({
        type:"POST",
        context: this,
        success: function(data) {
           var curPage = document.location.href.split("page=")[1];
           document.location.href ="/admin/userList/page=" + curPage;
        },
        error: function() {
            alert("Error!")
        }
    })
}

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