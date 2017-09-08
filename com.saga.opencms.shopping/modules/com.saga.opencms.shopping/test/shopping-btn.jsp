<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<div class="center">

    <%--PRODUCT 1--%>
    <div class="input-group">

        <%--MINUS--%>
        <span class="input-group-btn">
            <button type="button" class="btn btn-default shopping-btn-quantity"
                    data-input-id="shopping-quantity-0"
                    data-amount="-1">
                <span class="glyphicon glyphicon-minus"></span>
            </button>
        </span>

        <%--COUNT--%>
        <input type="text" id="shopping-quantity-0" name="quantity"
               class="form-control shopping-input-quantity"
               value="0"
               data-structure-id="83e0a814-8040-11e7-a65a-f53d2dda7236"
               data-rm-value="0"
               data-max="10"
               data-min="0">

        <%--PLUS--%>
        <span class="input-group-btn">
            <button type="button" class="btn btn-default shopping-btn-quantity"
                    data-input-id="shopping-quantity-0"
                    data-amount="1">
                <span class="glyphicon glyphicon-plus"></span>
            </button>
        </span>
    </div>

    <%--PRODUCT 2--%>
    <div class="input-group">

        <%--MINUS--%>
        <span class="input-group-btn">
            <button type="button" class="btn btn-default shopping-btn-quantity"
                    data-input-id="shopping-quantity-1"
                    data-amount="-1">
                <span class="glyphicon glyphicon-minus"></span>
            </button>
        </span>

        <%--COUNT--%>
        <input type="text" id="shopping-quantity-1" name="quantity"
               class="form-control shopping-input-quantity"
               value="0"
               data-structure-id="87707853-81e8-11e7-b716-f53d2dda7236"
               data-rm-value="0"
               data-max="10"
               data-min="0">

        <%--PLUS--%>
        <span class="input-group-btn">
            <button type="button" class="btn btn-default shopping-btn-quantity"
                    data-input-id="shopping-quantity-1"
                    data-amount="1">
                <span class="glyphicon glyphicon-plus"></span>
            </button>
        </span>
    </div>
</div>