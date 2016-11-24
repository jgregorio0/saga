!function(e,t){if("function"==typeof define&&define.amd)define(["underscore","backbone"],function(i,r){return e.Backgrid=t(i,r)});else if("object"==typeof exports){var i=require("backbone");i.$=i.$||require("jquery"),module.exports=t(require("underscore"),i)}else e.Backgrid=t(e._,e.Backbone)}(this,function(e,t){"use strict";function i(e,t,i){var r=t-(e+"").length;r=0>r?0:r;for(var n="",s=0;r>s;s++)n+=i;return n+e}var r="	\n\x0B\f\r   ᠎             　\u2028\u2029\ufeff";if(!String.prototype.trim||r.trim()){r="["+r+"]";var n=new RegExp("^"+r+r+"*"),s=new RegExp(r+r+"*$");String.prototype.trim=function(){if(void 0===this||null===this)throw new TypeError("can't convert "+this+" to object");return String(this).replace(n,"").replace(s,"")}}var o=t.$,l={Extension:{},resolveNameToClass:function(t,i){if(e.isString(t)){var r=e.map(t.split("-"),function(e){return e.slice(0,1).toUpperCase()+e.slice(1)}).join("")+i,n=l[r]||l.Extension[r];if(e.isUndefined(n))throw new ReferenceError("Class '"+r+"' not found");return n}return t},callByNeed:function(){var t=arguments[0];if(!e.isFunction(t))return t;var i=arguments[1],r=[].slice.call(arguments,2);return t.apply(i,r+""?r:[])}};e.extend(l,t.Events);var a=l.Command=function(t){e.extend(this,{altKey:!!t.altKey,"char":t["char"],charCode:t.charCode,ctrlKey:!!t.ctrlKey,key:t.key,keyCode:t.keyCode,locale:t.locale,location:t.location,metaKey:!!t.metaKey,repeat:!!t.repeat,shiftKey:!!t.shiftKey,which:t.which})};e.extend(a.prototype,{moveUp:function(){return 38==this.keyCode},moveDown:function(){return 40===this.keyCode},moveLeft:function(){return this.shiftKey&&9===this.keyCode},moveRight:function(){return!this.shiftKey&&9===this.keyCode},save:function(){return 13===this.keyCode},cancel:function(){return 27===this.keyCode},passThru:function(){return!(this.moveUp()||this.moveDown()||this.moveLeft()||this.moveRight()||this.save()||this.cancel())}});var h=l.CellFormatter=function(){};e.extend(h.prototype,{fromRaw:function(e,t){return e},toRaw:function(e,t){return e}});var c=l.NumberFormatter=function(t){if(e.extend(this,this.defaults,t||{}),this.decimals<0||this.decimals>20)throw new RangeError("decimals must be between 0 and 20")};c.prototype=new h,e.extend(c.prototype,{defaults:{decimals:2,decimalSeparator:".",orderSeparator:","},HUMANIZED_NUM_RE:/(\d)(?=(?:\d{3})+$)/g,fromRaw:function(t,i){if(e.isNull(t)||e.isUndefined(t))return"";t=parseFloat(t).toFixed(~~this.decimals);var r=t.split("."),n=r[0],s=r[1]?(this.decimalSeparator||".")+r[1]:"";return n.replace(this.HUMANIZED_NUM_RE,"$1"+this.orderSeparator)+s},toRaw:function(t,i){if(t=t.trim(),""===t)return null;for(var r="",n=t.split(this.orderSeparator),s=0;s<n.length;s++)r+=n[s];var o=r.split(this.decimalSeparator);r="";for(var s=0;s<o.length;s++)r=r+o[s]+".";"."===r[r.length-1]&&(r=r.slice(0,r.length-1));var l=1*(1*r).toFixed(~~this.decimals);return e.isNumber(l)&&!e.isNaN(l)?l:void 0}});var d=l.PercentFormatter=function(){l.NumberFormatter.apply(this,arguments)};d.prototype=new l.NumberFormatter,e.extend(d.prototype,{defaults:e.extend({},c.prototype.defaults,{multiplier:1,symbol:"%"}),fromRaw:function(e,t){var i=[].slice.call(arguments,1);return i.unshift(e*this.multiplier),(c.prototype.fromRaw.apply(this,i)||"0")+this.symbol},toRaw:function(t,i){var r=t.split(this.symbol);if(r&&r[0]&&""===r[1]||null==r[1]){var n=c.prototype.toRaw.call(this,r[0]);return e.isUndefined(n)?n:n/this.multiplier}}});var u=l.DatetimeFormatter=function(t){if(e.extend(this,this.defaults,t||{}),!this.includeDate&&!this.includeTime)throw new Error("Either includeDate or includeTime must be true")};u.prototype=new h,e.extend(u.prototype,{defaults:{includeDate:!0,includeTime:!0,includeMilli:!1},DATE_RE:/^([+\-]?\d{4})-(\d{2})-(\d{2})$/,TIME_RE:/^(\d{2}):(\d{2}):(\d{2})(\.(\d{3}))?$/,ISO_SPLITTER_RE:/T|Z| +/,_convert:function(t,r){if(""===(t+"").trim())return null;var n,s=null;if(e.isNumber(t)){var o=new Date(t);n=i(o.getUTCFullYear(),4,0)+"-"+i(o.getUTCMonth()+1,2,0)+"-"+i(o.getUTCDate(),2,0),s=i(o.getUTCHours(),2,0)+":"+i(o.getUTCMinutes(),2,0)+":"+i(o.getUTCSeconds(),2,0)}else{t=t.trim();var l=t.split(this.ISO_SPLITTER_RE)||[];n=this.DATE_RE.test(l[0])?l[0]:"",s=n&&l[1]?l[1]:this.TIME_RE.test(l[0])?l[0]:""}var a=this.DATE_RE.exec(n)||[],h=this.TIME_RE.exec(s)||[];if(r){if(this.includeDate&&e.isUndefined(a[0]))return;if(this.includeTime&&e.isUndefined(h[0]))return;if(!this.includeDate&&n)return;if(!this.includeTime&&s)return}var o=new Date(Date.UTC(1*a[1]||0,1*a[2]-1||0,1*a[3]||0,1*h[1]||null,1*h[2]||null,1*h[3]||null,1*h[5]||null)),c="";return this.includeDate&&(c=i(o.getUTCFullYear(),4,0)+"-"+i(o.getUTCMonth()+1,2,0)+"-"+i(o.getUTCDate(),2,0)),this.includeTime&&(c=c+(this.includeDate?"T":"")+i(o.getUTCHours(),2,0)+":"+i(o.getUTCMinutes(),2,0)+":"+i(o.getUTCSeconds(),2,0),this.includeMilli&&(c=c+"."+i(o.getUTCMilliseconds(),3,0))),this.includeDate&&this.includeTime&&(c+="Z"),c},fromRaw:function(t,i){return e.isNull(t)||e.isUndefined(t)?"":this._convert(t)},toRaw:function(e,t){return this._convert(e,!0)}});var m=l.StringFormatter=function(){};m.prototype=new h,e.extend(m.prototype,{fromRaw:function(t,i){return e.isUndefined(t)||e.isNull(t)?"":t+""}});var p=l.EmailFormatter=function(){};p.prototype=new h,e.extend(p.prototype,{toRaw:function(t,i){var r=t.trim().split("@");return 2===r.length&&e.all(r)?t:void 0}});var f=l.SelectFormatter=function(){};f.prototype=new h,e.extend(f.prototype,{fromRaw:function(t,i){return e.isArray(t)?t:null!=t?[t]:[]}});var g=l.CellEditor=t.View.extend({initialize:function(e){this.formatter=e.formatter,this.column=e.column,this.column instanceof k||(this.column=new k(this.column)),this.listenTo(this.model,"backgrid:editing",this.postRender)},postRender:function(e,t){return(null==t||t.get("name")==this.column.get("name"))&&this.$el.focus(),this}}),v=l.InputCellEditor=g.extend({tagName:"input",attributes:{type:"text"},events:{blur:"saveOrCancel",keydown:"saveOrCancel"},initialize:function(e){v.__super__.initialize.apply(this,arguments),e.placeholder&&this.$el.attr("placeholder",e.placeholder)},render:function(){var e=this.model;return this.$el.val(this.formatter.fromRaw(e.get(this.column.get("name")),e)),this},saveOrCancel:function(t){var i=this.formatter,r=this.model,n=this.column,s=new a(t),o="blur"===t.type;if(s.moveUp()||s.moveDown()||s.moveLeft()||s.moveRight()||s.save()||o){t.preventDefault(),t.stopPropagation();var l=this.$el.val(),h=i.toRaw(l,r);e.isUndefined(h)?r.trigger("backgrid:error",r,n,l):(r.set(n.get("name"),h),r.trigger("backgrid:edited",r,n,s))}else s.cancel()&&(t.stopPropagation(),r.trigger("backgrid:edited",r,n,s))},postRender:function(e,t){if(null==t||t.get("name")==this.column.get("name"))if("right"===this.$el.css("text-align")){var i=this.$el.val();this.$el.focus().val(null).val(i)}else this.$el.focus();return this}}),y=l.Cell=t.View.extend({tagName:"td",formatter:h,editor:v,events:{click:"enterEditMode"},initialize:function(t){this.column=t.column,this.column instanceof k||(this.column=new k(this.column));var i=this.column,r=this.model,n=this.$el,s=l.resolveNameToClass(i.get("formatter")||this.formatter,"Formatter");e.isFunction(s.fromRaw)||e.isFunction(s.toRaw)||(s=new s),this.formatter=s,this.editor=l.resolveNameToClass(this.editor,"CellEditor"),this.listenTo(r,"change:"+i.get("name"),function(){n.hasClass("editor")||this.render()}),this.listenTo(r,"backgrid:error",this.renderError),this.listenTo(i,"change:editable change:sortable change:renderable",function(e){var t=e.changedAttributes();for(var i in t)t.hasOwnProperty(i)&&n.toggleClass(i,t[i])}),this.updateStateClassesMaybe()},updateStateClassesMaybe:function(){var e=this.model,t=this.column,i=this.$el;i.toggleClass("editable",l.callByNeed(t.editable(),t,e)),i.toggleClass("sortable",l.callByNeed(t.sortable(),t,e)),i.toggleClass("renderable",l.callByNeed(t.renderable(),t,e))},render:function(){var e=this.$el;e.empty();var t=this.model,i=this.column.get("name");return e.text(this.formatter.fromRaw(t.get(i),t)),e.addClass(i),this.updateStateClassesMaybe(),this.delegateEvents(),this},enterEditMode:function(){var e=this.model,t=this.column,i=l.callByNeed(t.editable(),t,e);i&&(this.currentEditor=new this.editor({column:this.column,model:this.model,formatter:this.formatter}),e.trigger("backgrid:edit",e,t,this,this.currentEditor),this.undelegateEvents(),this.$el.empty(),this.$el.append(this.currentEditor.$el),this.currentEditor.render(),this.$el.addClass("editor"),e.trigger("backgrid:editing",e,t,this,this.currentEditor))},renderError:function(e,t){(null==t||t.get("name")==this.column.get("name"))&&this.$el.addClass("error")},exitEditMode:function(){this.$el.removeClass("error"),this.currentEditor.remove(),this.stopListening(this.currentEditor),delete this.currentEditor,this.$el.removeClass("editor"),this.render()},remove:function(){return this.currentEditor&&(this.currentEditor.remove.apply(this.currentEditor,arguments),delete this.currentEditor),y.__super__.remove.apply(this,arguments)}}),w=l.StringCell=y.extend({className:"string-cell",formatter:m}),b=l.UriCell=y.extend({className:"uri-cell",title:null,target:"_blank",initialize:function(e){b.__super__.initialize.apply(this,arguments),this.title=e.title||this.title,this.target=e.target||this.target},render:function(){this.$el.empty();var e=this.model.get(this.column.get("name")),t=this.formatter.fromRaw(e,this.model);return this.$el.append(o("<a>",{tabIndex:-1,href:e,title:this.title||t,target:this.target}).text(t)),this.delegateEvents(),this}}),C=(l.EmailCell=w.extend({className:"email-cell",formatter:p,render:function(){this.$el.empty();var e=this.model,t=this.formatter.fromRaw(e.get(this.column.get("name")),e);return this.$el.append(o("<a>",{tabIndex:-1,href:"mailto:"+t,title:t}).text(t)),this.delegateEvents(),this}}),l.NumberCell=y.extend({className:"number-cell",decimals:c.prototype.defaults.decimals,decimalSeparator:c.prototype.defaults.decimalSeparator,orderSeparator:c.prototype.defaults.orderSeparator,formatter:c,initialize:function(e){C.__super__.initialize.apply(this,arguments);var t=this.formatter;t.decimals=this.decimals,t.decimalSeparator=this.decimalSeparator,t.orderSeparator=this.orderSeparator}})),x=(l.IntegerCell=C.extend({className:"integer-cell",decimals:0}),l.PercentCell=C.extend({className:"percent-cell",multiplier:d.prototype.defaults.multiplier,symbol:d.prototype.defaults.symbol,formatter:d,initialize:function(){x.__super__.initialize.apply(this,arguments);var e=this.formatter;e.multiplier=this.multiplier,e.symbol=this.symbol}})),E=l.DatetimeCell=y.extend({className:"datetime-cell",includeDate:u.prototype.defaults.includeDate,includeTime:u.prototype.defaults.includeTime,includeMilli:u.prototype.defaults.includeMilli,formatter:u,initialize:function(t){E.__super__.initialize.apply(this,arguments);var i=this.formatter;i.includeDate=this.includeDate,i.includeTime=this.includeTime,i.includeMilli=this.includeMilli;var r=this.includeDate?"YYYY-MM-DD":"";r+=this.includeDate&&this.includeTime?"T":"",r+=this.includeTime?"HH:mm:ss":"",r+=this.includeTime&&this.includeMilli?".SSS":"",this.editor=this.editor.extend({attributes:e.extend({},this.editor.prototype.attributes,this.editor.attributes,{placeholder:r})})}}),T=(l.DateCell=E.extend({className:"date-cell",includeTime:!1}),l.TimeCell=E.extend({className:"time-cell",includeDate:!1}),l.BooleanCellEditor=g.extend({tagName:"input",attributes:{tabIndex:-1,type:"checkbox"},events:{mousedown:function(){this.mouseDown=!0},blur:"enterOrExitEditMode",mouseup:function(){this.mouseDown=!1},change:"saveOrCancel",keydown:"saveOrCancel"},render:function(){var e=this.model,t=this.formatter.fromRaw(e.get(this.column.get("name")),e);return this.$el.prop("checked",t),this},enterOrExitEditMode:function(e){if(!this.mouseDown){var t=this.model;t.trigger("backgrid:edited",t,this.column,new a(e))}},saveOrCancel:function(e){var t=this.model,i=this.column,r=this.formatter,n=new a(e);if(n.passThru()&&"change"!=e.type)return!0;n.cancel()&&(e.stopPropagation(),t.trigger("backgrid:edited",t,i,n));var s=this.$el;if(n.save()||n.moveLeft()||n.moveRight()||n.moveUp()||n.moveDown()){e.preventDefault(),e.stopPropagation();var o=r.toRaw(s.prop("checked"),t);t.set(i.get("name"),o),t.trigger("backgrid:edited",t,i,n)}else if("change"==e.type){var o=r.toRaw(s.prop("checked"),t);t.set(i.get("name"),o),s.focus()}}})),R=(l.BooleanCell=y.extend({className:"boolean-cell",editor:T,events:{click:"enterEditMode"},render:function(){this.$el.empty();var e=this.model,t=this.column,i=l.callByNeed(t.editable(),t,e);return this.$el.append(o("<input>",{tabIndex:-1,type:"checkbox",checked:this.formatter.fromRaw(e.get(t.get("name")),e),disabled:!i})),this.delegateEvents(),this}}),l.SelectCellEditor=g.extend({tagName:"select",events:{change:"save",blur:"close",keydown:"close"},template:e.template('<option value="<%- value %>" <%= selected ? \'selected="selected"\' : "" %>><%- text %></option>',null,{variable:null,evaluate:/<%([\s\S]+?)%>/g,interpolate:/<%=([\s\S]+?)%>/g,escape:/<%-([\s\S]+?)%>/g}),setOptionValues:function(t){this.optionValues=t,this.optionValues=e.result(this,"optionValues")},setMultiple:function(e){this.multiple=e,this.$el.prop("multiple",e)},_renderOptions:function(t,i){for(var r="",n=0;n<t.length;n++)r+=this.template({text:t[n][0],value:t[n][1],selected:e.indexOf(i,t[n][1])>-1});return r},render:function(){this.$el.empty();var t=e.result(this,"optionValues"),i=this.model,r=this.formatter.fromRaw(i.get(this.column.get("name")),i);if(!e.isArray(t))throw new TypeError("optionValues must be an array");for(var n=null,s=null,n=null,l=null,a=null,h=0;h<t.length;h++){var n=t[h];if(e.isArray(n))s=n[0],n=n[1],this.$el.append(this.template({text:s,value:n,selected:e.indexOf(r,n)>-1}));else{if(!e.isObject(n))throw new TypeError("optionValues elements must be a name-value pair or an object hash of { name: 'optgroup label', value: [option name-value pairs] }");l=n.name,a=o("<optgroup></optgroup>",{label:l}),a.append(this._renderOptions.call(this,n.values,r)),this.$el.append(a)}}return this.delegateEvents(),this},save:function(e){var t=this.model,i=this.column;t.set(i.get("name"),this.formatter.toRaw(this.$el.val(),t))},close:function(e){var t=this.model,i=this.column,r=new a(e);r.cancel()?(e.stopPropagation(),t.trigger("backgrid:edited",t,i,new a(e))):(r.save()||r.moveLeft()||r.moveRight()||r.moveUp()||r.moveDown()||"blur"==e.type)&&(e.preventDefault(),e.stopPropagation(),this.save(e),t.trigger("backgrid:edited",t,i,new a(e)))}})),$=l.SelectCell=y.extend({className:"select-cell",editor:R,multiple:!1,formatter:f,optionValues:void 0,delimiter:", ",initialize:function(e){$.__super__.initialize.apply(this,arguments),this.listenTo(this.model,"backgrid:edit",function(e,t,i,r){t.get("name")==this.column.get("name")&&(r.setOptionValues(this.optionValues),r.setMultiple(this.multiple))})},render:function(){this.$el.empty();var t=e.result(this,"optionValues"),i=this.model,r=this.formatter.fromRaw(i.get(this.column.get("name")),i),n=[];try{if(!e.isArray(t)||e.isEmpty(t))throw new TypeError;for(var s=0;s<r.length;s++)for(var o=r[s],l=0;l<t.length;l++){var a=t[l];if(e.isArray(a)){var h=a[0],a=a[1];a==o&&n.push(h)}else{if(!e.isObject(a))throw new TypeError;for(var c=a.values,d=0;d<c.length;d++){var u=c[d];u[1]==o&&n.push(u[0])}}}this.$el.append(n.join(this.delimiter))}catch(m){if(m instanceof TypeError)throw new TypeError("'optionValues' must be of type {Array.<Array>|Array.<{name: string, values: Array.<Array>}>}");throw m}return this.delegateEvents(),this}}),k=l.Column=t.Model.extend({defaults:{name:void 0,label:void 0,sortable:!0,editable:!0,renderable:!0,formatter:void 0,sortType:"cycle",sortValue:void 0,direction:null,cell:void 0,headerCell:void 0},initialize:function(){this.has("label")||this.set({label:this.get("name")},{silent:!0});var e=l.resolveNameToClass(this.get("headerCell"),"HeaderCell"),t=l.resolveNameToClass(this.get("cell"),"Cell");this.set({cell:t,headerCell:e},{silent:!0})},sortValue:function(){var t=this.get("sortValue");return e.isString(t)?this[t]:e.isFunction(t)?t:function(e,t){return e.get(t)}}});e.each(["sortable","renderable","editable"],function(t){k.prototype[t]=function(){var i=this.get(t);return e.isString(i)?this[i]:e.isFunction(i)?i:!!i}});var N=l.Columns=t.Collection.extend({model:k}),_=l.Row=t.View.extend({tagName:"tr",initialize:function(e){var i=this.columns=e.columns;i instanceof t.Collection||(i=this.columns=new N(i));for(var r=this.cells=[],n=0;n<i.length;n++)r.push(this.makeCell(i.at(n),e));this.listenTo(i,"add",function(t,i){var n=i.indexOf(t),s=this.makeCell(t,e);r.splice(n,0,s);var o=this.$el;0===n?o.prepend(s.render().$el):n===i.length-1?o.append(s.render().$el):o.children().eq(n).before(s.render().$el)}),this.listenTo(i,"remove",function(e,t,i){r[i.index].remove(),r.splice(i.index,1)})},makeCell:function(e){return new(e.get("cell"))({column:e,model:this.model})},render:function(){this.$el.empty();for(var e=document.createDocumentFragment(),t=0;t<this.cells.length;t++)e.appendChild(this.cells[t].render().el);return this.el.appendChild(e),this.delegateEvents(),this},remove:function(){for(var e=0;e<this.cells.length;e++){var i=this.cells[e];i.remove.apply(i,arguments)}return t.View.prototype.remove.apply(this,arguments)}}),D=l.EmptyRow=t.View.extend({tagName:"tr",emptyText:null,initialize:function(e){this.emptyText=e.emptyText,this.columns=e.columns},render:function(){this.$el.empty();var t=document.createElement("td");t.setAttribute("colspan",this.columns.length);var i=document.createElement("span");return i.innerHTML=e.result(this,"emptyText"),t.appendChild(i),this.el.className="empty",this.el.appendChild(t),this}}),M=l.HeaderCell=t.View.extend({tagName:"th",events:{"click a":"onClick"},initialize:function(e){this.column=e.column,this.column instanceof k||(this.column=new k(this.column));var t=this.column,i=this.collection,r=this.$el;this.listenTo(t,"change:editable change:sortable change:renderable",function(e){var t=e.changedAttributes();for(var i in t)t.hasOwnProperty(i)&&r.toggleClass(i,t[i])}),this.listenTo(t,"change:direction",this.setCellDirection),this.listenTo(t,"change:name change:label",this.render),l.callByNeed(t.editable(),t,i)&&r.addClass("editable"),l.callByNeed(t.sortable(),t,i)&&r.addClass("sortable"),l.callByNeed(t.renderable(),t,i)&&r.addClass("renderable"),this.listenTo(i.fullCollection||i,"sort",this.removeCellDirection)},removeCellDirection:function(){this.$el.removeClass("ascending").removeClass("descending"),this.column.set("direction",null)},setCellDirection:function(e,t){this.$el.removeClass("ascending").removeClass("descending"),e.cid==this.column.cid&&this.$el.addClass(t)},onClick:function(e){function t(e,t){"ascending"===r.get("direction")?n.trigger(s,t,"descending"):"descending"===r.get("direction")?n.trigger(s,t,null):n.trigger(s,t,"ascending")}function i(e,t){"ascending"===r.get("direction")?n.trigger(s,t,"descending"):n.trigger(s,t,"ascending")}e.preventDefault();var r=this.column,n=this.collection,s="backgrid:sort",o=l.callByNeed(r.sortable(),r,this.collection);if(o){var a=r.get("sortType");"toggle"===a?i(this,r):t(this,r)}},render:function(){this.$el.empty();var e,t=this.column,i=l.callByNeed(t.sortable(),t,this.collection);return e=i?o("<a>").text(t.get("label")).append("<b class='sort-caret'></b>"):document.createTextNode(t.get("label")),this.$el.append(e),this.$el.addClass(t.get("name")),this.$el.addClass(t.get("direction")),this.delegateEvents(),this}}),S=(l.HeaderRow=l.Row.extend({initialize:function(){l.Row.prototype.initialize.apply(this,arguments)},makeCell:function(e,t){var i=e.get("headerCell")||t.headerCell||M;return i=new i({column:e,collection:this.collection})}}),l.Header=t.View.extend({tagName:"thead",initialize:function(e){this.columns=e.columns,this.columns instanceof t.Collection||(this.columns=new N(this.columns)),this.row=new l.HeaderRow({columns:this.columns,collection:this.collection})},render:function(){return this.$el.append(this.row.render().$el),this.delegateEvents(),this},remove:function(){return this.row.remove.apply(this.row,arguments),t.View.prototype.remove.apply(this,arguments)}})),U=l.Body=t.View.extend({tagName:"tbody",initialize:function(e){this.columns=e.columns,this.columns instanceof t.Collection||(this.columns=new N(this.columns)),this.row=e.row||this.row||_,this.rows=this.collection.map(function(e){var t=new this.row({columns:this.columns,model:e});return t},this),this.emptyText=e.emptyText,this._unshiftEmptyRowMayBe();var i=this.collection;this.listenTo(i,"add",this.insertRow),this.listenTo(i,"remove",this.removeRow),this.listenTo(i,"sort",this.refresh),this.listenTo(i,"reset",this.refresh),this.listenTo(i,"backgrid:sort",this.sort),this.listenTo(i,"backgrid:edited",this.moveToNextCell),this.listenTo(this.columns,"add remove",this.updateEmptyRow)},_unshiftEmptyRowMayBe:function(){return 0===this.rows.length&&null!=this.emptyText?(this.emptyRow=new D({emptyText:this.emptyText,columns:this.columns}),this.rows.unshift(this.emptyRow),!0):void 0},insertRow:function(e,i,r){if(this.rows[0]instanceof D&&this.rows.pop().remove(),!(i instanceof t.Collection||r))return void this.collection.add(e,r=i);var n=new this.row({columns:this.columns,model:e}),s=i.indexOf(e);this.rows.splice(s,0,n);var o=this.$el,l=o.children(),a=n.render().$el;return s>=l.length?o.append(a):l.eq(s).before(a),this},removeRow:function(t,i,r){return r?((e.isUndefined(r.render)||r.render)&&this.rows[r.index].remove(),this.rows.splice(r.index,1),this._unshiftEmptyRowMayBe()&&this.render(),this):(this.collection.remove(t,r=i),void(this._unshiftEmptyRowMayBe()&&this.render()))},updateEmptyRow:function(){null!=this.emptyRow&&this.emptyRow.render()},refresh:function(){for(var e=0;e<this.rows.length;e++)this.rows[e].remove();return this.rows=this.collection.map(function(e){var t=new this.row({columns:this.columns,model:e});return t},this),this._unshiftEmptyRowMayBe(),this.render(),this.collection.trigger("backgrid:refresh",this),this},render:function(){this.$el.empty();for(var e=document.createDocumentFragment(),t=0;t<this.rows.length;t++){var i=this.rows[t];e.appendChild(i.render().el)}return this.el.appendChild(e),this.delegateEvents(),this},remove:function(){for(var e=0;e<this.rows.length;e++){var i=this.rows[e];i.remove.apply(i,arguments)}return t.View.prototype.remove.apply(this,arguments)},sort:function(i,r){if(!e.contains(["ascending","descending",null],r))throw new RangeError('direction must be one of "ascending", "descending" or `null`');e.isString(i)&&(i=this.columns.findWhere({name:i}));var n,s=this.collection;n="ascending"===r?-1:"descending"===r?1:null;var o=this.makeComparator(i.get("name"),n,n?i.sortValue():function(e){return 1*e.cid.replace("c","")});return t.PageableCollection&&s instanceof t.PageableCollection?(s.setSorting(n&&i.get("name"),n,{sortValue:i.sortValue()}),s.fullCollection?(null==s.fullCollection.comparator&&(s.fullCollection.comparator=o),s.fullCollection.sort(),s.trigger("backgrid:sorted",i,r,s)):s.fetch({reset:!0,success:function(){s.trigger("backgrid:sorted",i,r,s)}})):(s.comparator=o,s.sort(),s.trigger("backgrid:sorted",i,r,s)),i.set("direction",r),this},makeComparator:function(e,t,i){return function(r,n){var s,o=i(r,e),l=i(n,e);return 1===t&&(s=o,o=l,l=s),o===l?0:l>o?-1:1}},moveToNextCell:function(e,t,i){var r,n,s,o,a,h=this.collection.indexOf(e),c=this.columns.indexOf(t);if(-1===c)return this;if(this.rows[h].cells[c].exitEditMode(),i.moveUp()||i.moveDown()||i.moveLeft()||i.moveRight()||i.save()){var d=this.columns.length,u=d*this.collection.length;if(i.moveUp()||i.moveDown()){o=h+(i.moveUp()?-1:1);var m=this.rows[o];m?(r=m.cells[c],l.callByNeed(r.column.editable(),r.column,e)&&(r.enterEditMode(),e.trigger("backgrid:next",o,c,!1))):e.trigger("backgrid:next",o,c,!0)}else if(i.moveLeft()||i.moveRight()){for(var p=i.moveRight(),f=h*d+c+(p?1:-1);f>=0&&u>f;p?f++:f--)if(o=~~(f/d),a=f-o*d,r=this.rows[o].cells[a],n=l.callByNeed(r.column.renderable(),r.column,r.model),s=l.callByNeed(r.column.editable(),r.column,e),n&&s){r.enterEditMode(),e.trigger("backgrid:next",o,a,!1);break}f==u&&e.trigger("backgrid:next",~~(f/d),f-o*d,!0)}}return this}});l.Footer=t.View.extend({tagName:"tfoot",initialize:function(e){this.columns=e.columns,this.columns instanceof t.Collection||(this.columns=new l.Columns(this.columns))}}),l.Grid=t.View.extend({tagName:"table",className:"backgrid",header:S,body:U,footer:null,initialize:function(i){i.columns instanceof t.Collection||(i.columns=new N(i.columns||this.columns)),this.columns=i.columns;var r=e.omit(i,["el","id","attributes","className","tagName","events"]);this.body=i.body||this.body,this.body=new this.body(r),this.header=i.header||this.header,this.header&&(this.header=new this.header(r)),this.footer=i.footer||this.footer,this.footer&&(this.footer=new this.footer(r)),this.listenTo(this.columns,"reset",function(){this.header&&(this.header=new(this.header.remove().constructor)(r)),this.body=new(this.body.remove().constructor)(r),this.footer&&(this.footer=new(this.footer.remove().constructor)(r)),this.render()})},insertRow:function(){return this.body.insertRow.apply(this.body,arguments),this},removeRow:function(){return this.body.removeRow.apply(this.body,arguments),this},insertColumn:function(){return this.columns.add.apply(this.columns,arguments),this},removeColumn:function(){return this.columns.remove.apply(this.columns,arguments),this},sort:function(){return this.body.sort.apply(this.body,arguments),this},render:function(){return this.$el.empty(),this.header&&this.$el.append(this.header.render().$el),this.footer&&this.$el.append(this.footer.render().$el),this.$el.append(this.body.render().$el),this.delegateEvents(),this.trigger("backgrid:rendered",this),this},remove:function(){return this.header&&this.header.remove.apply(this.header,arguments),this.body.remove.apply(this.body,arguments),this.footer&&this.footer.remove.apply(this.footer,arguments),t.View.prototype.remove.apply(this,arguments)}});return l});