$(window).load(function(){
    //게시판 관련
    //$('p.text-warning').hide();
    var updateBtn        =  $(".btn.btn-success.update");                     //업데이트 버튼
    var removeBtn        = $(".btn.btn-danger.remove");                       //삭제버튼
    var downBtn          = $(".glyphicon.glyphicon-save.btn-download");         //다운로드 버튼
    var searchBtn        = $(".file-search")                                  //파일찾아보기 버튼
    
    //유저 관련
    var userNewBtn       = $('.btn-newuser');
    var userEditBtn      = $('.btn-useredit');
    var userDeleteBtn    = $('.btn-userdelete');
    var userUpdatebtn    = $('.btn-editUser');
    //프로젝트 추가하기 버튼
    var addProjectBtn    = $('.btn-addProject');
    var removeProjectBtn = $('.btn-removeProject');
    //신규 프로젝트 생성 버튼
    var newProjectBtn    = $('.btn-newProject');
    var editProjectBtn   = $('.btn-editProject');
    var deleteProjectBtn = $('.btn-projectdelete');

    //프로젝트 열기 버튼
    var productOpenBtn    = $('.btn-product-open');
    var productEditBtn    = $('.btn-product-edit');
    var productAddBtn     = $('.btn-product-add');

    $('p.text-warning.user_id').hide();

    /*********** PRODUCT 관련버튼 **********/
    productOpenBtn.click(function(){
      var _openUrl = $(this).attr("_url");
      var _width = $(this).attr("_width");
      var _height = $(this).attr("_height")
      var _openOption = "width = " + _width + ", height=" + _height + ", toolbar=no, top=10, left=10"

      window.open(_openUrl, "_contents", _openOption)
    })

    productEditBtn.click(function(){
      
    })

    productAddBtn.click(function(){
      var _msFile = $('input[name~="ms_file_name"]');
      var _sbFile = $('input[name~="sb_file_name"]');
      var _pURL = $('input[name~="product_url"]');
      var _pWidth = $('input[name~="product_width"]');
      var _pHeight = $('input[name~="product_height"]');
    })


    $('.ms-search').click(function(){
        $(this).siblings(".ms_file").trigger('click');
    })

    //파일관련
    $('.ms_file').each(function(){
        $(this).change(function(){
            $(".form-control.ms_name").val($(this).val());
        })
    })

    $('.sb-search').click(function(){
        $(this).siblings(".sb_file").trigger('click');
    })

    //파일관련
    $('.sb_file').each(function(){
        $(this).change(function(){
            $(".form-control.sb_name").val($(this).val());
        })
    })
    /*********** 검색 버튼 끝 **********/


    //신규 프로젝트 생성
    newProjectBtn.click(function(){ 
      var _project_code = $('input[name~="project_code"]')
      var _project_title = $('input[name~="project_title"]')
      
      hasError(_project_code.parent().parent(), _project_code.val())
      hasError(_project_title.parent().parent(), _project_title.val())

      if(_project_code.val() != "" && _project_title.val() != ""){
        if(confirm("등록하시겠습니까?")){
          newProject(_project_code.val(), _project_title.val());
        }
      }else{
        alert("프로젝트코드 및 프로젝트명을 입력해 주세요.")
      }
    })

    editProjectBtn.click(function(){
      var _id = $(this).attr("id");
      var _project_title = $('input[name~="project_title"]')
      
      if(_project_title.val() != ""){
        if(confirm("수정하시겠습니까?")){
          editProject(_id, _project_title.val());
        }
      }else{
        alert("프로젝트 명을 입력해 주세요.");
      }
    })

    //프로젝트 삭제
    deleteProjectBtn.click(function(){      
      var _id = $(this).attr("id");
      removeProject(_id)
    })


    //프로젝트 추가하기
    addProjectBtn.click(function(){            
      var _project_code = $('#select-project option:selected').val();
      var _project_text = $('#select-project option:selected').text();
      //프로젝트 사이즈
      var _op_total = $('#project option').size();
      //중복 아이템 체크
      if(_op_total > 0){
        var _bolInit = true;
        for(i=0; i<=_op_total; i++){
          if($('#project option:eq('+i+')').val() == _project_code){
            _bolInit = false;
            alert("이미 등록되어 있습니다.")
            break;
          }
        }
        //해당 아이템이 없을때만 추가
        if(_bolInit) {
          $('#project').append("<option value='"+_project_code+"'>"+_project_text+"</option>");
        }
      }else{
        $('#project').append("<option value='"+_project_code+"'>"+_project_text+"</option>");
      }
    })
    //프로젝트 제거하기
    removeProjectBtn.click(function(){
       $('#project option:selected').remove();
    })

    //유저 생성
    userNewBtn.click(function(){
       var _user_id = $('input[name~="user_id"]')
       var _password = $('input[name~="password"]')
       var _name = $('input[name~="user_name"]')
       var _email = $('input[name~="email"]')
       var _project_id = "";
       var _user_type = $('#select-auth option:selected').val();
       
       hasError(_user_id.parent().parent(), _user_id.val())
       hasError(_password.parent().parent(), _password.val())
       hasError(_name.parent().parent(), _name.val())
       hasError(_email.parent().parent(), _email.val())
       
       var _op_total = $('#project option').size();

       if(_op_total == 0){
         alert("한 개 이상의 프로젝트를 선택해 주세요.")
       }

       if(_user_id.val() != "" && _password.val() != "" && _name.val() != "" && _email.val() != "" && _op_total > 0){
          if(confirm("등록하시겠습니까?")){
            for(i=0; i<_op_total; i++){
              if(i == (_op_total-1)){
                _project_id += $('#project option:eq('+i+')').val()
              }else{
                _project_id += $('#project option:eq('+i+')').val() + "|";
              }
            }            
            newUser(_user_id.val(), _email.val(), _name.val(), _password.val(), _user_type, _project_id) 
          }
       }
    });
    //유저 업데이트 버튼
    userUpdatebtn.click(function(){
       var _curId = $(this).attr("id");      
       
       var _user_id = $('input[name~="user_id"]')
       var _password = $('input[name~="password"]')
       var _name = $('input[name~="user_name"]')
       var _email = $('input[name~="email"]')
       var _project_id = "";
       var _user_type = $('#select-auth option:selected').val();
       
       var _op_total = $('#project option').size();

       hasError(_user_id.parent().parent(), _user_id.val())
       hasError(_password.parent().parent(), _password.val())
       hasError(_name.parent().parent(), _name.val())
       hasError(_email.parent().parent(), _email.val())

       if(_op_total == 0){
         alert("한 개 이상의 프로젝트를 선택해 주세요.")
       }

       if(_user_id.val() != "" && _password.val() != "" && _name.val() != "" && _email.val() != "" && _op_total > 0){
          if(confirm("수정하시겠습니까?")){
            for(i=0; i<_op_total; i++){
              if(i == (_op_total-1)){
                _project_id += $('#project option:eq('+i+')').val()
              }else{
                _project_id += $('#project option:eq('+i+')').val() + "|";
              }
            }
            editUser(_curId, _user_id.val(), _email.val(), _name.val(), _password.val(), _user_type, _project_id) 
          }
       }
    })
    //유저 삭제 버튼
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

    //파일관련
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


function newProject(_code, _title){
  jsRoutes.controllers.Projects.newProject(_code, _title).ajax({
      type:"POST",
      context: this,
      success: function(data) {
         document.location.href ="/project/project_list/page=1"
      },
      error: function() {
         
      }
  }) 
}

function editProject(_id, _title){
  jsRoutes.controllers.Projects.editProject(_id, _title).ajax({
      type:"POST",
      context: this,
      success: function(data) {
         document.location.href ="/project/project_list/page=1"
      },
      error: function() {
         
      }
  }) 
}

function removeProject(_id){
  jsRoutes.controllers.Projects.removeProject(_id).ajax({
      type:"POST",
      context: this,
      success: function(data) {
         document.location.href ="/project/project_list/page=1"
      },
      error: function() {
         
      }
  }) 
}

function newUser(_id, _email, _name, _password, _user_type, _project_id){    
   jsRoutes.controllers.Admin.newUser(_id, _email, _name, _password, _user_type, _project_id).ajax({
        type:"POST",
        context: this,
        success: function(data) {
           document.location.href ="/admin/user_list/page=1"
        },
        error: function() {
           $('p.text-warning.user_id').show();
        }
    }) 
}

function editUser(_id, _user_id, _email, _name, _password, _user_type, _project_id){    
   jsRoutes.controllers.Admin.editUser(_id, _user_id, _email, _name, _password, _user_type, _project_id).ajax({
        type:"POST",
        context: this,
        success: function(data) {
           document.location.href ="/admin/user_list/page=1"
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
           document.location.href ="/admin/user_list/page=" + curPage;
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