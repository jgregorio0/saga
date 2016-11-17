package com.fins.gt.util;

import com.fins.gt.common.Const;
import com.fins.gt.model.ColumnInfo;
import com.fins.gt.model.FilterInfo;
import com.fins.gt.model.GridInfo;
import com.fins.gt.model.PageInfo;
import com.fins.gt.model.SortInfo;
import com.fins.org.json.JSONObject;

public class ModelUtils {
	public static GridInfo createGridInfo(JSONObject modelJS){
		GridInfo info=new GridInfo();
		info.setId(modelJS.getString("id"));
		return info;
	}
		
	public static ColumnInfo createColumnInfo(JSONObject modelJS){
		ColumnInfo info=new ColumnInfo();
		info.setId(modelJS.getString("id"));
		info.setHeader(modelJS.getString("header"));
		info.setFieldIndex(modelJS.getString("fieldIndex"));
		info.setSortOrder(modelJS.getString("sortOrder"));
		
		info.setHidden(modelJS.getBoolean("hidden"));
		info.setExportable(modelJS.getBoolean("exportable"));
		info.setPrintable(modelJS.getBoolean("printable"));
		
		return info;
	}

	public static PageInfo createPageInfo(JSONObject modelJS){
		PageInfo info=new PageInfo();
		
		info.setEndRowNum(modelJS.getInt("endRowNum",Const.nullInt));
		info.setPageNum(modelJS.getInt("pageNum",Const.nullInt));
		info.setPageSize(modelJS.getInt("pageSize",Const.nullInt));
		info.setStartRowNum(modelJS.getInt("startRowNum",Const.nullInt));
		info.setTotalPageNum(modelJS.getInt("totalPageNum",Const.nullInt));
		info.setTotalRowNum(modelJS.getInt("totalRowNum",Const.nullInt));
		
		return info;
	}
	
	public static JSONObject generatePageInfoJSON(PageInfo pageInfo){
		JSONObject pageInfoJS = new JSONObject();
		pageInfoJS.put("endRowNum", pageInfo.getEndRowNum());
		pageInfoJS.put("pageNum", pageInfo.getPageNum());
		pageInfoJS.put("pageSize", pageInfo.getPageSize());
		pageInfoJS.put("startRowNum", pageInfo.getStartRowNum());
		pageInfoJS.put("totalPageNum", pageInfo.getTotalPageNum());
		pageInfoJS.put("totalRowNum", pageInfo.getTotalRowNum());
		return pageInfoJS;
	}
	
	public static SortInfo createSortInfo(JSONObject modelJS){
		SortInfo info=new SortInfo();

		info.setColumnId(modelJS.getString("columnId"));
		info.setFieldName(modelJS.getString("fieldName"));
		info.setSortOrder(modelJS.getString("sortOrder"));
		
		return info;
	}
	
	public static FilterInfo createFilterInfo(JSONObject modelJS){
		FilterInfo info=new FilterInfo();

		info.setColumnId(modelJS.getString("columnId") );
		info.setFieldName(modelJS.getString("fieldName") );
		info.setLogic(modelJS.getString("logic") );
		info.setValue(modelJS.getString("value") );
		
		return info;
	}

}
