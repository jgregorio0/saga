<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<%
	String dateformat = request.getParameter("dateformat");
	SimpleDateFormat df = new SimpleDateFormat(dateformat);
	Date currentDate = new Date();
	String currentDateStr = df.format(currentDate);
	pageContext.setAttribute("currentDate", currentDateStr);
%>

<script type="text/javascript" >
	///////////////////////////////////////
	var APP_CONTROLLER='${param.controller}';
	var dateformat='${param.dateformat}';

	///////////////////////////////////////

	var grid_demo_id = "myGrid1" ;

	var dsOption= {

		fields :[
			{name : 'USER_ID'},
			{name : 'USER_NAME' },
			{name : 'USER_FIRSTNAME' },
			{name : 'USER_LASTNAME'  },
			{name : 'USER_EMAIL'},
			{name : 'USER_DATECREATED' ,type: 'date'},
		],
		uniqueField : 'no'
	}

	var colsOption = [
		{id: 'USER_ID' , header: "id" , width :55, editable : false, editor: { type :"text",validRule : ['required']}  },
		{id: 'USER_NAME' , header: "Usuario" , width :100 , editable : false ,editor: { type :"text",validRule : ['required']} },
		{id: 'USER_FIRSTNAME' , header: "Nombre" , width :100,  editable : true ,editor: { type :"text" }	},
		{id: 'USER_LASTNAME' , header: "Apellidos" , width :100, editable : true, editor: { type :"text"}	},
		{id: 'USER_EMAIL' , header: "Email" , width :100, editable : true, editor: { type :"text" }  },
		{id: 'USER_DATECREATED' , header: "Fecha de creación" , width :100,  editable : true, editor: { type :"date", format:'${dateformat}',	validRule : ['datetime']}	}
	];

	var gridOption={
		id : grid_demo_id,


		//exportURL : APP_CONTROLLER + '?actionMethod=list',
		loadURL : APP_CONTROLLER + '?actionMethod=list',
		saveURL : APP_CONTROLLER + '?actionMethod=save' ,
		remotePaging : false ,
		width: "700",
		height: "330",
		container : 'mygrid_container',
		toolbarPosition : 'bottom',
		toolbarContent : 'nav | pagesize | reload | add del save | filter | print | xls | state',
		pageSizeList : [10,20,30,50,100,200],
		pageSize : 10,
		dataset : dsOption ,
		columns : colsOption ,
		recountAfterSave : true ,
		defaultRecord : {
			USER_ID : '-',
			USER_NAME : '',
			USER_FIRSTNAME : '',
			USER_LASTNAME : '',
			USER_EMAIL : '',
			USER_DATECREATED : '${currentDate}'
		},
		parameters : { pool: '${param.pool}' }
	};



	var mygrid=new Sigma.Grid( gridOption );

	Sigma.Utils.onLoad( function(){
		mygrid.render();
	} );

	//////////////////////////////////////////////////////////

</script>
<div id="content">
	<div id="mygrid_container"></div>
</div>