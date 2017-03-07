<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<div>
    <div id="idFav"></div>
    <script>
        // Get favoritos icon by ajax
        var urlFavAjx = '<cms:link>/system/modules/com.saga.caprabochef.frontend/elements/favoritos-ajax.jsp</cms:link>'
        var detailPath = myArray[i].filepath;
        if (detailPath.endsWith('/')) {
            detailPath = detailPath.substring(0, detailPath.length - 1);
        }

        $.ajax({
            type: 'GET',
            url: urlFavAjx,
            data: {detailPath: detailPath}
        }).done(function(data){
            // console.log("done", data);
            try {
                var json = JSON.parse(data);
                $('#idFav').html(json.fav);
            } catch (err) {
                console.error("error parseando json: " + data);
            }
        }).fail(function(err){
            console.error("fail cargando FavAjx", err);
        });
    </script>
</div>
