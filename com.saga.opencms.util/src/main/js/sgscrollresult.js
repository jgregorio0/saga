/**
 @module Saga.Habla

 Módulo con la clase ScrollResult. Esta clase encapsula la funcionalidad de
 paginar resultados de forma secuencial actualizando el listado de elementos
 con los de la siguiente página (scroll infinito) haciendo una petición AJAX
 a una URL que retorna el siguiente conjunto de resultados. La solicitud de
 nuevos elementos se realiza mediante el evento 'onclick' del selector indicado
 en la configuración. ScrollResult implementa la funcionalidad base, en cada caso
 se deberían crear implementaciones con configuraciones estándar:
 @example

  // Fichero mi-impementacion.js donde voy a crear la nueva implementación
   function MiImplementacion(args) {
    var options = $.extend(args, {
      params: {
        rows: 50,
        pageSize: 15,
        pageParam: 'pN',
        miParametro1 : 'miValor1',
        miParametro2 : 'miValor2'
      },
      selector : '#miSelector',
      update : '#miLista',
      onBeforeRequest :  function(e) {
        e.text('Cargando resultados...')
      },
      onLoadList : function(e) {
        e.text('Ver más resultados')
      },
      onEmptyList : function(e) {
        e.remove()
        alert('No hay más resultados')
      },
      onRequestError : function(e) {
        alert('Hubo un problema oteniendo nuevos resultados')
      }
    });
    new Saga.Habla.ScrollResult(options);
   }

    // En la página donde vaya a utilizar mi nueva implementación
   new MiImplementacion({
    url : '/donde/recibo/resultados/',
    params : {
      otroParma : 'otroValor'
    }
   })

 **/
(function(root, $){
  /**
   @class ScrollResult
   @constructor
   @param {Object} o Objeto con la configuración. Se pueden configurar las siguientes opciones:
    @param {String} o.url URL a la que se realizan las peticiones de nuevos registros
    @param {String} o.selector Selector del elemento que realiza la petición de nuevos registros
    @param {String} o.delegate Selector sobre el que se realiza delegación de eventos si se desea configurar
    @param {String} o.update Selector del elemento que se va a actualizar con nuevos resultados
    @param {Number} o.currentPage Página actual, por defecto es 1 porque asume que hay registros precargados
    @param {Object} o.params Parámetros que se van a enviar en cada nueva petición de registros. Existen 3
      parámetros que son obligatorios y que se cargan internamente con valores por defecto:
      @param {Number} o.params.rows Número máximo total de registros que se podrán obtener
      @param {Number} o.params.pageSize Número de registros máximo que se obtienen en cada petición
      @param {String} o.params.pageParam Nombre del parámetro que contiene la página de resultados que solicitar
    @param {Function} o.onBeforeRequest Función que se ejecuta antes de realizar la petición
      de nuevos resultados. Recibe el siguiente argumento:
      @param {Object} o.onBeforeRequest.el Instancia jQuery con el selector que lanza la actualización
    @param {Function} o.onLoadList Función que se ejecuta después de actualizar la lista de
      registros. Recibe el siguiente argumento:
      @param {Object} o.onLoadList.el Instancia jQuery con el selector que lanza la actualización
    @param {Function} o.onEmptyList Función que se ejecuta cuando no se reciben
      más registros. Recibe el siguiente argumento:
      @param {Object} o.onEmptyList.el Instancia jQuery con el selector que lanza la actualización
    @param {Function} o.onRequestError Función que se ejecuta cuando falla la petición
      AJAX. Recibe el siguiente argumento:
      @param {Object} o.onRequestError.el Instancia jQuery con el selector que lanza la actualización
   **/
  ScrollResult = function (o) {
    // Página actual. Se asume que se ha cargado inicialmente el primero conjunto de datos.
    var currentPage = 1;
    // Función vacía que se usa para inicialización de los ditintos callbacks
    var F = function () {};
    // Extendemos las opciones por defecto con las que recibimos por argumentos
    var options = $.extend(true, {
      params: {
        rows: 100,
        pageSize: 10,
        pageParam : 'p'
      },
      onBeforeRequest: F,
      onLoadList: F,
      onEmptyList: F,
      onRequestError: F
    }, o);

    // Las siguientes opciones son obligatorias y si no se asignan se lanza un error
    if (!options.url || !options.selector || !options.update) {
      throw new Error("Debe indicar los parámetros de configuración 'url', 'selector' y 'update'")
    }

    // Almacena el nombre del parámetro de paginación
    var page = options.params.pageParam;
    // Sobreescribimos la página actual si se indica
    currentPage = options.currentPage || currentPage;
    /**
     * Realiza la petición AJAX y agrega los nuevos resultados
     *
     * @method
     * @param {Object} el Selector que lanza la petición
     * @private
     */
    function update(el) {
      // Incluimos en los parámetros la siguiente página que se va a solicitar
      options.params[page] = currentPage++;
      $.get(options.url, options.params).done(function (data) {
        if (data.trim().length > 0) {
          $(options.update).append(data)
          options.onLoadList(el);
        } else {
          options.onEmptyList(el);
        }
      }).fail(function () {
        options.onRequestError(el);
      })
    }

    /**
     * Ejecuta el callback onBeforeRequest y la actualización de registros
     *
     * @method
     * @param {Object} el Selector que lanza la petición
     * @private
     */
    function request(el) {
      options.onBeforeRequest(el);
      update(el);
    }

    // Si en las opciones hemos indicado un selector para realizar delegación de eventos lo hacemos
    if (options.delegate) {
      $(options.selector).on('click', options.delegate, function(e) {
        e.preventDefault();
        // En este caso el elemento que lanza el evento es aquel sobre el que se delega
        request($(e.target))
      })
    } else {
      $(options.selector).on('click', function(e) {
        e.preventDefault();
        request($(this));
      })
    }

    return {
      /**
       * Cambia la página de resultados
       *
       * @method
       * @param {Number} p Página que se desea visualizar
       */
      page : function(p) {
        currentPage = p;
        var $el;
        if (options.delegate) {
          $el = $(options.selector).find(options.delegate);
        } else {
          $el = $(options.selector);
        }
        request($el);
      }
    }


  }

  // Exportarmos al módulo de Habla
  root.Saga = root.Saga || {};
  root.Saga.Habla = root.Saga.Habla || {};
  root.Saga.Habla.ScrollResult = ScrollResult;

})(window, jQuery);