/**
 * ajax_chooseRight_edit
 */

var ChooseRight_EventRow = React.createClass({displayName: "ChooseRight_EventRow", 
	tr_onClick:function(trid,cbid,e){
		var cbox=$("#"+cbid);
		var tr=$("#"+trid);
		if(tr.hasClass("am-active")){
				cbox.prop("checked",false); 
			tr.removeClass("am-active");
		}else{
				cbox.prop("checked", true); 
			tr.addClass("am-active");
		}
	},
	componentDidMount:function(){
		$(".am-active input[type='checkbox']").prop("checked",true); 
	},
  render: function() {
    var event = this.props.event;
    var is_Checked=this.props.checkedRightuuid&&this.props.checkedRightuuid.indexOf(event.uuid)>-1;
    var className = is_Checked ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
      React.createElement("tr", {name: "table_tr_checkbox_chright", id: "tr_chright_"+event.uuid, className: className, onClick: this.tr_onClick.bind(this,"tr_chright_"+event.uuid,"tb_cbox__chright"+event.uuid)}, 
      React.createElement("td", null, 
      React.createElement("input", {type: "checkbox", alt: event.name, value: event.uuid, id: "tb_cbox__chright"+event.uuid, name: "table_checkbox"})
      ), 
        React.createElement("td", null, event.name), 
        React.createElement("td", null, event.tel), 
        React.createElement("td", null, event.sex=="0"?"男":"女"), 
        React.createElement("td", {className: "px_disable_"+event.disable}, event.disable=="1"?"禁用":"正常")
      ) 
    );
  }
}); 

var ChooseRight_EventsTable = React.createClass({displayName: "ChooseRight_EventsTable",
//	 getInitialState: function() {
//		 	alert(this.props.group_uuid);
//		    return this.props.group_uuid;
//		  },
//	
	handleClick: function(m) {
		 if(this.props.handleClick){
			 if(m=="cancel"){
				 this.props.handleClick(m,$('#selectgroup_uuid_chright').val());
				 return;
			 }
			 var uuids=null;
			 var names=null;
			 $($("input[name='table_checkbox']")).each(function(){
				　if(this.checked){
					 if(uuids==null){
						 uuids=this.value;
						 names=this.alt;
					 }
					 else{
						 uuids+=','+this.value ; 
						 names+=','+this.alt; 
					 }
					　   //遍历被选中CheckBox元素的集合 得到Value值
				　}
				});
			  
			 this.props.handleClick(m,$('#selectgroup_uuid_chright').val(),uuids,names);
		 }
	  },
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all_chright")[0].checked); 
		  if( $("#id_checkbox_all_chright")[0].checked){
			  $('tr[name="table_tr_checkbox_chright"]').addClass("am-active");
		  }else{
			  $('tr[name="table_tr_checkbox_chright"]').removeClass("am-active");
		  }
	  },
	  //
	  handleChange_selectgroup_uuid_chright:function(){
		  var v=$('#selectgroup_uuid_chright').val();
		//  this.setState(v);
		  w_ch_right.reshowBygroup(v);
	  },
  render: function() {
	  var that=this;
    return (
    React.createElement("div", null, 
    React.createElement(AMUIReact.Sticky, null, 
    React.createElement(AMUIReact.ButtonToolbar, null, 
    React.createElement(AMUIReact.Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "ok"), round: true}, "确认"), 
    React.createElement(AMUIReact.Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "cancel"), round: true}, "取消")
  )
  ), 
  React.createElement("div", {className: "header"}, 
  React.createElement("div", {className: "am-g"}, 
    React.createElement("h1", null, "老师选择")
  ), 
  React.createElement("hr", null)
), 
	  React.createElement("div", {className: "am-form-group"}, 
      React.createElement("select", {id: "selectgroup_uuid_chright", name: "group_uuid", "data-am-selected": "{btnSize: 'lg'}", value: this.props.group_uuid?this.props.group_uuid:"", onChange: this.handleChange_selectgroup_uuid_chright}, 
      this.props.group_list.map(function(event) {
          return (React.createElement("option", {value: event.uuid}, event.company_name));
        })
      )
    ), 
	  
      React.createElement(AMUIReact.Table, React.__spread({},  this.props), 
        React.createElement("thead", null, 
          React.createElement("tr", null, 
          	React.createElement("th", null, 
            React.createElement("input", {type: "checkbox", id: "id_checkbox_all_chright", onChange: this.handleChange_checkbox_all})
            ), 
            React.createElement("th", null, "昵称"), 
            React.createElement("th", null, "电话"), 
            React.createElement("th", null, "性别"), 
            React.createElement("th", null, "状态")
          )
        ), 
        React.createElement("tbody", null, 
          this.props.events.map(function(event) {
            return (React.createElement(ChooseRight_EventRow, {event: event, checkedRightuuid: that.props.checkedRightuuid}));
          })
        )
      )
      )
    );
  }
});
//end chooseRight
