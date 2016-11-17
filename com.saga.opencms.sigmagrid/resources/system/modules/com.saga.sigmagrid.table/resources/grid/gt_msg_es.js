//

if (!window.Sigma){
	window.Sigma={};
}
Sigma.Msg=Sigma.Msg || {};
SigmaMsg=Sigma.Msg;

Sigma.Msg.Grid = Sigma.Msg.Grid || {};

Sigma.Msg.Grid.es={
	LOCAL	: "ES",
	ENCODING		: "UTF-8",
	NO_DATA : "No Data",


	GOTOPAGE_BUTTON_TEXT: 'Ir a',

	FILTERCLEAR_TEXT: "Borrar todos los filtros",
	SORTASC_TEXT	: "Ascender",
	SORTDESC_TEXT	: "Descender",
	SORTDEFAULT_TEXT: "Original",

	ERR_PAGENUM		: "El número de la página debe ser un entero entre 1 y #{1}.",

	EXPORT_CONFIRM	: "Esta operación afectará a todos los registros de la tabla.\n\n( Pulsa \"Cancelar\" para limitar los resultados a la página actual.)",
	OVER_MAXEXPORT	: "El número de registros excede #{1}, el máximo permitido.",

	PAGE_STATE	: "Mostrando #{1} - #{2}, de un total de #{3} páginas y #{4} registros.",
	PAGE_STATE_FULL	: "Página #{5}, mostrando #{1} - #{2}, de un total de #{3} páginas y #{4} registros.",

	SHADOWROW_FAILED: "Información relevante no disponible",
	NEARPAGE_TITLE	: "Página siguiente",
	WAITING_MSG : 'Cargando...',

	NO_RECORD_UPDATE: "Níngún registro ha sido actualizado",
	UPDATE_CONFIRM	: "¿Desea guardar los cambios?",
	NO_MODIFIED: "Nada ha sido modificado",

	PAGE_BEFORE : 'Página',
	PAGE_AFTER : 'Página anterior',

	PAGESIZE_BEFORE :   '',
	PAGESIZE_AFTER :   'Por página',

	RECORD_UNIT : '',
	
	CHECK_ALL : 'Seleccionar todos',

	COLUMNS_HEADER : 'Columnas',

	DIAG_TITLE_FILTER : 'Opciones de filtro',
	DIAG_NO_FILTER : 'Sin filtro',
	TEXT_ADD_FILTER	: "Añadir",
	TEXT_CLEAR_FILTER	: "Eliminar todos",
	TEXT_OK	: "OK",
	TEXT_DEL : "Eliminar",
	TEXT_CANCEL	: "Cancelar",
	TEXT_CLOSE	: "Cerrar",
	TEXT_UP : "Arriba",
	TEXT_DOWN : "Abajo",

	NOT_SAVE : "¿Desea guardar los cambios? \n Pulsa \"Cancelar\" para deshacer.",

	DIAG_TITLE_CHART  : 'Chart',

	CHANGE_SKIN : "Skins",

	STYLE_NAME_DEFAULT : "Classic",
	STYLE_NAME_CHINA : "Pink",
	STYLE_NAME_VISTA : "Vista",
	STYLE_NAME_MAC : "Mac",

	MENU_FREEZE_COL : "Bloquear columnas",
	MENU_SHOW_COL : "Ocultar columnas",
	MENU_GROUP_COL : "Group Span",

	TOOL_RELOAD : "Recargar" ,
	TOOL_ADD : "Añadir" ,
	TOOL_DEL : "Eliminar" ,
	TOOL_SAVE : "Guardar" ,

	TOOL_PRINT : "Imprimir" ,
	TOOL_XLS : "Exportar a xls" ,
	TOOL_PDF : "Exportar a pdf" ,
	TOOL_CSV : "Exportar a csv" ,
	TOOL_XML : "Exportar a xml",
	TOOL_FILTER : "Filtro" ,
	TOOL_CHART : "Chart" 

};

Sigma.Msg.Grid['default']=Sigma.Msg.Grid.es;


if (!Sigma.Msg.Validator){
	Sigma.Msg.Validator={ };
}

Sigma.Msg.Validator.en={

		'required'	: '{0#This field} es obligatorio.',
		'date'		: '{0#This field} debe tener el formato correcto ({1#YYYY-MM-DD}).',
		'time'		: '{0#This field} debe tener el formato correcto ({1#HH:mm}).',
		'datetime'	: '{0#This field} debe tener el formato correcto ({1#YYYY-MM-DD HH:mm}).',
		'email'		: '{0#This field} debe tener formato de email.',
		'telephone'	: '{0#This field} debe tener formato de teléfono.',
		'number'	: '{0} debe ser un número.',
		'integer'	: '{0} debe ser un entero.',
		'float'		: '{0} debe ser un entero o un decimal.',
		'money'		: '{0} debe ser un entero o un decimal con 2 decimales.',
		'range'		: '{0} debe comprender el rango de {1} y {2}.',
		'equals'	: '{0} debe ser igual a {1}.',
		'lessthen'	: '{0} debe ser menor que {1}.',
		'idcard'	: '{0} debe tener formtao de ID',

		'enchar'	: 'Letras, digitos y subrayado sólo se permite para {0}',
		'cnchar'	: '{0} debe ser caracteres chinos',
		'minlength'	: '{0} debe contener más de {1} caracteres.',
		'maxlength'	: '{0} debe contener menos de {1} caracteres.'

}

Sigma.Msg.Validator['default'] = Sigma.Msg.Validator.es;

//