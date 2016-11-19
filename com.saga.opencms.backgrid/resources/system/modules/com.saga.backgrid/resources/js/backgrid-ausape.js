//     Personalizamos los recursos de backgrid para Ausape


/** Personalizamos las celdas sobreescribiendo la funcion render
 *  para añadir en el selector de clase el atributo class */
var auRenderCell = function () {
    var $el = this.$el;
    $el.empty();
    var model = this.model;
    var columnName = this.column.get("name");
    var formattedData = this.formatter.fromRaw(model.get(columnName), model);
    $el.text(formattedData);
    $el.addClass(columnName);

    // Add title
    if (this.column.get("title")) {
        $el.attr("title", formattedData);
    }

    // Add class
    $el.addClass(this.column.get("class"));

    this.updateStateClassesMaybe();
    this.delegateEvents();
    return this;
}

var AuIntegerCell = Backgrid.IntegerCell.extend({
    orderSeparator: '',

    //Sobreescribimos para añadir la clase de la columna
    render: auRenderCell
});

var AuStringCell = Backgrid.StringCell.extend({

    //Sobreescribimos para añadir la clase de la columna
    render: auRenderCell
});

var AuMomentCell = Backgrid.Extension.MomentCell.extend({

    //Sobreescribimos para añadir la clase de la columna
    render: auRenderCell
});

var AuUriCell = Backgrid.UriCell.extend({

    //Sobreescribimos para añadir la clase de la columna
    render: function () {
        this.$el.empty();
        var rawValue;
        var formattedValue;
        var type = this.column.get("type");
        var name = this.column.get("name");
        rawValue = this.model.get(name);
        var a;

        // Si es de tipo json
        if (type === "json") {
            var titleValue = this.column.get("title");
            a = $("<a>", {
                class: rawValue.class || name,
                tabIndex: -1,
                href: rawValue.href,
                title: rawValue.title || titleValue || this.title,
                target: rawValue.target || this.target
            }).text(rawValue.text)
        }

        // Si es de tipo icon
        else if (type === "icon") {
            var icon = this.column.get("icon");
            formattedValue = '<i class="' + icon + '" aria-hidden="true"></i>';
            var titleValue = this.column.get("title");
            a = $("<a>", {
                id: this.model.get("id"),
                class: name,
                tabIndex: -1,
                href: rawValue,
                title: titleValue || this.title || formattedValue,
                target: this.target
            }).append(formattedValue);
        } else {
            formattedValue = this.formatter.fromRaw(rawValue, this.model);
            a = $("<a>", {
                class: name,
                tabIndex: -1,
                href: rawValue,
                title: this.title || formattedValue,
                target: this.target
            }.text(formattedValue))
        }

        // Append to elem
        this.$el.append(a);

        // Add class
        this.$el.addClass(this.column.get("class"));

        this.delegateEvents();
        return this;
    }
});

/** Personalizamos las columnas sobreescribiendo la funcion render
 *  para añadir en el selector de clase el atributo class */
// Creamos un modelo de columna propio
var AuColumn = Backgrid.Column.extend({
    defaults: _.defaults(
        Backgrid.Column.prototype.defaults,
        {
            headerCell: Backgrid.HeaderCell.extend({

                /**
                 Sobreescribimos el renderizado para añadir la clase de columna
                 */
                render: function () {
                    this.$el.empty();
                    var column = this.column;
                    var sortable = Backgrid.callByNeed(column.sortable(), column, this.collection);
                    var label;
                    if(sortable){
                        label = $("<a>").text(column.get("label")).append("<b class='sort-caret'></b>");
                    } else {
                        label = document.createTextNode(column.get("label"));
                    }

                    this.$el.append(label);
                    this.$el.addClass(column.get("name"));
                    this.$el.addClass(column.get("direction"));

                    // Add class
                    this.$el.addClass(column.get("class"));

                    this.delegateEvents();
                    return this;
                }
            })
        })
});

/** Personalizamos el paginador sobreescribiendo la funcion render
 *  para añadir el selector de clase pagination en los elementos ul */
// Creamos un paginador propio
var AuPaginator = Backgrid.Extension.Paginator.extend({
    render: function () {
        this.$el.empty();

        var totalPages = this.collection.state.totalPages;

        // Don't render if collection is empty
        if(this.renderMultiplePagesOnly && totalPages <= 1) {
            return this;
        }

        if (this.handles) {
            for (var i = 0, l = this.handles.length; i < l; i++) {
                this.handles[i].remove();
            }
        }

        var handles = this.handles = this.makeHandles();

        var ul = document.createElement("ul");

        // Add class pagination
        ul.classList.add("pagination")

        for (var i = 0; i < handles.length; i++) {
            ul.appendChild(handles[i].render().el);
        }

        this.el.appendChild(ul);

        return this;
    },
    className: "text-center",
    controls: {
        rewind: {
            label: "<<",
            title: "Ir a la primera página"
        },
        back: {
            label: "<",
            title: "Ir a la página anterior"
        },
        forward: {
            label: ">",
            title: "Ir a la siguiente página"
        },
        fastForward: {
            label: ">>",
            title: "Ir a la última página"
        }
    },

    pageHandle: Backgrid.Extension.PageHandle.extend({
        title: function (data) {
            return 'Ir a la página ' + data.label;
        }
    })
});

/** Personalizamos la fecha para que habra un datepicker **/
var AuDateFilter = Backgrid.Extension.ServerSideFilter.extend({
    events: {
        "focus .datepicker": "datePicker",
        "keyup input[type=search]": "showClearButtonMaybe",
        "click a[data-backgrid-action=clear]": "clear",
        "submit": "search"
    },
    datePicker: function () {
        'use strict';
        $(function () {
            $.datepicker.setDefaults($.datepicker.regional['es']);
            $(".datepicker").datepicker({
                onClose: function(){
                    $(this).trigger("keyup");
                    $(this).trigger("submit");
                }
            });
        });
    },
    template: function (data) {
        return '<input type="search" ' + 'class="datepicker"' + (data.placeholder ? 'placeholder="' + data.placeholder + '"' : '') + ' name="' + data.name + '" ' + (data.value ? 'value="' + data.value + '"' : '') + '/><a data-backgrid-action="clear" href="#"><span class="fa fa-times">&nbsp;</span></a>';
    }
});

/** Personalizamos la coleccion sobreescribiendo la funcion parseState
 * y parseRecords para añadir los mensajes, el estado y los datos en la respuesta*/
// Creamos una coleccion propia
var AuPageableCollection = Backbone.PageableCollection.extend({
    msg: "msgs",
    abridge: "abridge",

    parseState: function (resp, queryParams, state, options) {

        // Actualizamos los mensajes
        if (resp && _.isObject(resp.msgs) && _.isArray(resp.records)) {
            AuUpdateMsgs(this.msg, resp.msgs.errors, resp.msgs.infos)
        }

        // Actualizamos la info de la tabla
        if (resp && _.isObject(resp.abridge) && _.isArray(resp.records)) {
            AuUpdateAbridge(this.abridge, resp.abridge)
        }

        // Actualizamos el estado
        if (resp && _.isObject(resp.state) && _.isArray(resp.records)) {

            var newState = _.clone(state);
            var serverState = resp.state;

            _.each(_.pairs(_.omit(queryParams, "directions")), function (kvp) {
                var k = kvp[0], v = kvp[1];
                var serverVal = serverState[v];
                if (!_.isUndefined(serverVal) && !_.isNull(serverVal)) newState[k] = serverState[v];
            });

            if (serverState.order) {
                newState.order = _.invert(queryParams.directions)[serverState.order] * 1;
            }

            return newState;
        }
    },

    parseRecords: function (resp, options) {
        if (resp && _.isArray(resp.records)) {
            return resp.records;
        }

        return resp;
    }
});

// Creamos funcion para actualizar mensajes
function AuUpdateMsgs(div, errors, infos){
    var div = "#" + div;
    var $msgs = $(div);

    $msgs.empty();

    errors.forEach(function(item){
        $msgs.append(
            $('<div class="alert alert-danger" role="alert"></div>')
                .text(item));
    })

    infos.forEach(function(item){
        $msgs.append(
            $('<div class="alert alert-warning" role="alert"></div>')
                .text(item));
    })
}

// Creamos funcion para actualizar la info de la tabla
function AuUpdateAbridge(div, abridge){
    var div = "#" + div;
    var $abridge = $(div);

    $abridge.empty();

    var $ul = $("<ul>", {
        class: "nav nav-pills",
        role:"tablist"
    });
    for (var k in abridge) {
        $ul.append(
            $('<li>', {
                //class: "list-group-item col-xs-2"
                role:"presentation"
            })
                .append($('<span>', {
                    class: "badge"
                }).text(abridge[k].badge))
                .append("&nbsp;" + abridge[k].text + "&nbsp;"));
    }

    //$ul.addClass("row list-group table-cell text-right breadcrumb");

    $abridge.append($ul);
}

/**
 var MyUriFormatter = _.extend({}, Backgrid.CellFormatter.prototype, {
  fromRaw: function (rawValue, model) {
    return rawValue.replace("http://", '');
  }
});

 var MyUriCell = Backgrid.UriCell.extend({
  // every cell class has a default formatter, which you can override
  formatter: MyUriFormatter
});

 var grid = new Backgrid.Grid({
  columns: [{
    name: "url",
    cell: MyUriCell,
  }],
  collection: col
});*/

/**
{
    name: "composite",
        cell: Backgrid.Cell.extend({

    render: function(){

        // You have access to the whole model - get values like normal in Backbone
        var half_value_1 = this.model.get("half_value_1");
        var half_value_2 = this.model.get("half_value_2");

        // Put what you want inside the cell (Can use .html if HTML formatting is needed)
        this.$el.text( half_value_1 + '/' + half_value_2 );

        // MUST do this for the grid to not error out
        return this;

    }

})
}*/