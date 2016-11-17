/* SAGA SOLUCIONES: Webform Confirmation Email
 *
 * Script que se encarga de comprobar si un campo email y su campo de confirmacion de email son iguales validando al rellenar el campo. 
 * En caso de ser distintos muestra un error.
 *
 */
$(function() {
    var $email = $(".emailfield");
    var $confEmail = $(".confirmationemailfield");
    var $submitBtn = $(".formbutton.btn.submitbutton");

    $confEmail.bind('input', function() {
        if ($(this).val() !== $email.val()) {
            //inhabilitamos submit
            $submitBtn.prop("disabled", true);
            // Mostramos el error
            if ($confEmail.val() != null && $confEmail.val().length > 0) {
                $(".webform_label_error.confemail").show();
            }

        } else {
            // Escondemos el error y habilitamos submit
            $submitBtn.prop("disabled", false);
            $(".webform_label_error.confemail").hide();
        }
    });

    $email.bind('input', function() {
        if ($(this).val() !== $confEmail.val()) {
            //inhabilitamos submit
            $submitBtn.prop("disabled", true);
            // Mostramos el error
            if ($confEmail.val() != null && $confEmail.val().length > 0) {
                $(".webform_label_error.confemail").show();
            }
        } else {
            // Escondemos el error y habilitamos submit
            $submitBtn.prop("disabled", false);
            $(".webform_label_error.confemail").hide();
        }
    });
});

$(function(){
    $("#emailform").submit(function(event ){
        var $email = $(".emailfield");
        var $confEmail = $(".confirmationemailfield");
        $.each($email, function( index, elem ) {
            if ($(elem).val() !== $($confEmail.get(index)).val()) {
                $(".webform_label_error").show();
                event.preventDefault();
                $('html, body').animate({scrollTop: $confEmail.offset().top - 100}, 1000);
            }
        });
    });
});