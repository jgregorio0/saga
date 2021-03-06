/*
 backgrid-paginator
 http://github.com/wyuenho/backgrid
 Copyright (c) 2013 Jimmy Yuen Ho Wong and contributors
 Licensed under the MIT @license.
 */
!function(a,b){
// CommonJS
    "object"==typeof exports?module.exports=b(require("underscore"),require("backbone"),require("backgrid"),require("backbone.paginator")):"function"==typeof define&&define.amd?define(["underscore","backbone","backgrid","backbone.paginator"],b):b(a._,a.Backbone,a.Backgrid)}(this,function(a,b,c){"use strict";/**
 PageHandle is a class that renders the actual page handles and reacts to
 click events for pagination.
 This class acts in two modes - control or discrete page handle modes. If
 one of the `is*` flags is `true`, an instance of this class is under
 control page handle mode. Setting a `pageIndex` to an instance of this
 class under control mode has no effect and the correct page index will
 always be inferred from the `is*` flag. Only one of the `is*` flags should
 be set to `true` at a time. For example, an instance of this class cannot
 simultaneously be a rewind control and a fast forward control. A `label`
 and a `title` function or a string are required to be passed to the
 constuctor under this mode. If a `title` function is provided, it __MUST__
 accept a hash parameter `data`, which contains a key `label`. Its result
 will be used to render the generated anchor's title attribute.
 If all of the `is*` flags is set to `false`, which is the default, an
 instance of this class will be in discrete page handle mode. An instance
 under this mode requires the `pageIndex` to be passed from the constructor
 as an option and it __MUST__ be a 0-based index of the list of page numbers
 to render. The constuctor will normalize the base to the same base the
 underlying PageableCollection collection instance uses. A `label` is not
 required under this mode, which will default to the equivalent 1-based page
 index calculated from `pageIndex` and the underlying PageableCollection
 instance. A provided `label` will still be honored however. The `title`
 parameter is also not required under this mode, in which case the default
 `title` function will be used. You are encouraged to provide your own
 `title` function however if you wish to localize the title strings.
 If this page handle represents the current page, an `active` class will be
 placed on the root list element.
 If this page handle is at the border of the list of pages, a `disabled`
 class will be placed on the root list element.
 Only page handles that are neither `active` nor `disabled` will respond to
 click events and triggers pagination.
 @class Backgrid.Extension.PageHandle
 */
var d=c.Extension.PageHandle=b.View.extend({/** @property */
    tagName:"li",/** @property */
    events:{"click a":"changePage"},/**
     @property {string|function(Object.<string, string>): string} title
     The title to use for the `title` attribute of the generated page handle
     anchor elements. It can be a string or a function that takes a `data`
     parameter, which contains a mandatory `label` key which provides the
     label value to be displayed.
     */
    title:function(a){return"Page "+a.label},/**
     @property {boolean} isRewind Whether this handle represents a rewind
     control
     */
    isRewind:!1,/**
     @property {boolean} isBack Whether this handle represents a back
     control
     */
    isBack:!1,/**
     @property {boolean} isForward Whether this handle represents a forward
     control
     */
    isForward:!1,/**
     @property {boolean} isFastForward Whether this handle represents a fast
     forward control
     */
    isFastForward:!1,/**
     Initializer.
     @param {Object} options
     @param {Backbone.Collection} options.collection
     @param {number} pageIndex 0-based index of the page number this handle
     handles. This parameter will be normalized to the base the underlying
     PageableCollection uses.
     @param {string} [options.label] If provided it is used to render the
     anchor text, otherwise the normalized pageIndex will be used
     instead. Required if any of the `is*` flags is set to `true`.
     @param {string} [options.title]
     @param {boolean} [options.isRewind=false]
     @param {boolean} [options.isBack=false]
     @param {boolean} [options.isForward=false]
     @param {boolean} [options.isFastForward=false]
     */
    initialize:function(b){var c=this.collection,d=c.state,e=d.currentPage,f=d.firstPage,g=d.lastPage;a.extend(this,a.pick(b,["isRewind","isBack","isForward","isFastForward"]));var h;this.isRewind?h=f:this.isBack?h=Math.max(f,e-1):this.isForward?h=Math.min(g,e+1):this.isFastForward?h=g:(h=+b.pageIndex,h=f?h+1:h),this.pageIndex=h,this.label=(b.label||(f?h:h+1))+"";var i=b.title||this.title;this.title=a.isFunction(i)?i({label:this.label}):i},/**
     Renders a clickable anchor element under a list item.
     */
    render:function(){this.$el.empty();var a=document.createElement("a");a.href="#",this.title&&(a.title=this.title),a.innerHTML=this.label,this.el.appendChild(a);var b=this.collection,c=b.state,d=c.currentPage,e=this.pageIndex;return this.isRewind&&d==c.firstPage||this.isBack&&!b.hasPreviousPage()||this.isForward&&!b.hasNextPage()||this.isFastForward&&(d==c.lastPage||c.totalPages<1)?this.$el.addClass("disabled"):this.isRewind||this.isBack||this.isForward||this.isFastForward||c.currentPage!=e||this.$el.addClass("active"),this.delegateEvents(),this},/**
     jQuery click event handler. Goes to the page this PageHandle instance
     represents. No-op if this page handle is currently active or disabled.
     */
    changePage:function(a){a.preventDefault();var b=this.$el,c=this.collection;return b.hasClass("active")||b.hasClass("disabled")||(this.isRewind?c.getFirstPage({reset:!0}):this.isBack?c.getPreviousPage({reset:!0}):this.isForward?c.getNextPage({reset:!0}):this.isFastForward?c.getLastPage({reset:!0}):c.getPage(this.pageIndex,{reset:!0})),this}}),e=c.Extension.Paginator=b.View.extend({/** @property */
    className:"backgrid-paginator",/** @property */
    windowSize:10,/**
     @property {number} slideScale the number used by #slideHowMuch to scale
     `windowSize` to yield the number of pages to slide. For example, the
     default windowSize(10) * slideScale(0.5) yields 5, which means the window
     will slide forward 5 pages as soon as you've reached page 6. The smaller
     the scale factor the less pages to slide, and vice versa.
     Also See:
     - #slideMaybe
     - #slideHowMuch
     */
    slideScale:.5,/**
     @property {Object.<string, Object.<string, string>>} controls You can
     disable specific control handles by setting the keys in question to
     null. The defaults will be merged with your controls object, with your
     changes taking precedent.
     */
    controls:{rewind:{label:"《",title:"First"},back:{label:"〈",title:"Previous"},forward:{label:"〉",title:"Next"},fastForward:{label:"》",title:"Last"}},/** @property */
    renderIndexedPageHandles:!0,/**
     @property renderMultiplePagesOnly. Determines if the paginator
     should show in cases where the collection has more than one page.
     Default is false for backwards compatibility.
     */
    renderMultiplePagesOnly:!1,/**
     @property {Backgrid.Extension.PageHandle} pageHandle. The PageHandle
     class to use for rendering individual handles
     */
    pageHandle:d,/** @property */
    goBackFirstOnSort:!0,/**
     Initializer.
     @param {Object} options
     @param {Backbone.Collection} options.collection
     @param {boolean} [options.controls]
     @param {boolean} [options.pageHandle=Backgrid.Extension.PageHandle]
     @param {boolean} [options.goBackFirstOnSort=true]
     @param {boolean} [options.renderMultiplePagesOnly=false]
     */
    initialize:function(b){var c=this;c.controls=a.defaults(b.controls||{},c.controls,e.prototype.controls),a.extend(c,a.pick(b||{},"windowSize","pageHandle","slideScale","goBackFirstOnSort","renderIndexedPageHandles","renderMultiplePagesOnly"));var d=c.collection;c.listenTo(d,"add",c.render),c.listenTo(d,"remove",c.render),c.listenTo(d,"reset",c.render),c.listenTo(d,"backgrid:sorted",function(){c.goBackFirstOnSort&&d.state.currentPage!==d.state.firstPage&&d.getFirstPage({reset:!0})})},/**
     Decides whether the window should slide. This method should return 1 if
     sliding should occur and 0 otherwise. The default is sliding should occur
     if half of the pages in a window has been reached.
     __Note__: All the parameters have been normalized to be 0-based.
     @param {number} firstPage
     @param {number} lastPage
     @param {number} currentPage
     @param {number} windowSize
     @param {number} slideScale
     @return {0|1}
     */
    slideMaybe:function(a,b,c,d,e){return Math.round(c%d/d)},/**
     Decides how many pages to slide when sliding should occur. The default
     simply scales the `windowSize` to arrive at a fraction of the `windowSize`
     to increment.
     __Note__: All the parameters have been normalized to be 0-based.
     @param {number} firstPage
     @param {number} lastPage
     @param {number} currentPage
     @param {number} windowSize
     @param {number} slideScale
     @return {number}
     */
    slideThisMuch:function(a,b,c,d,e){return~~(d*e)},_calculateWindow:function(){var a=this.collection,b=a.state,c=b.firstPage,d=+b.lastPage;d=Math.max(0,c?d-1:d);var e=Math.max(b.currentPage,b.firstPage);e=c?e-1:e;var f=this.windowSize,g=this.slideScale,h=Math.floor(e/f)*f;e<=d-this.slideThisMuch()&&(h+=this.slideMaybe(c,d,e,f,g)*this.slideThisMuch(c,d,e,f,g));var i=Math.min(d+1,h+f);return[h,i]},/**
     Creates a list of page handle objects for rendering.
     @return {Array.<Object>} an array of page handle objects hashes
     */
    makeHandles:function(){var b=[],c=this.collection,d=this._calculateWindow(),e=d[0],f=d[1];if(this.renderIndexedPageHandles)for(var g=e;f>g;g++)b.push(new this.pageHandle({collection:c,pageIndex:g}));var h=this.controls;return a.each(["back","rewind","forward","fastForward"],function(a){var d=h[a];if(d){var e={collection:c,title:d.title,label:d.label};e["is"+a.slice(0,1).toUpperCase()+a.slice(1)]=!0;var f=new this.pageHandle(e);"rewind"==a||"back"==a?b.unshift(f):b.push(f)}},this),b},/**
     Render the paginator handles inside an unordered list.
     */
    render:function(){this.$el.empty();var a=this.collection.state.totalPages;
// Don't render if collection is empty
        if(this.renderMultiplePagesOnly&&1>=a)return this;if(this.handles)for(var b=0,c=this.handles.length;c>b;b++)this.handles[b].remove();for(var d=this.handles=this.makeHandles(),e=document.createElement("ul"),b=0;b<d.length;b++)e.appendChild(d[b].render().el);return this.el.appendChild(e),this}})});