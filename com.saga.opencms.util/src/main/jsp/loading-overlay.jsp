<%--LOADING OVERLAY--%>
<div id="shopping-list-modal-email-loading" class="fade" style="
                                    background: rgba(255,255,255,0.4);
                                    position: absolute;
                                    left: 0;
                                    right: 0;
                                    top: 0;
                                    bottom: 0;
                                    width: 100%;
                                    height: 100%;
                                    -webkit-transition: all 0.3s ease;
                                    -moz-transition: all 0.3s ease;
                                    -ms-transition: all 0.3s ease;
                                    -o-transition: all 0.3s ease;
                                    transition: all 0.3s ease;
                                    z-index: -1;">
                                    <span class="fa fa-spinner fa-spin fa-3x fa-fw" style="
                                        position: absolute;
                                        left: 50%;
                                        top: 50%;
                                        margin: -39px 0 0 -39px;
                                        display: block;
                                        color: #222;
                                        /* z-index: 10000; */
                                        "></span>
    <span class="sr-only">Loading...</span>
</div>
<script>
    var fadeInLoading = function () {
        var $loading = $('#shopping-list-modal-email-loading');
        $loading.addClass('in');
        $loading.css('z-index', 0);
    };

    var fadeOutLoading = function () {
        var $loading = $('#shopping-list-modal-email-loading');
        $loading.removeClass('in');
        $loading.css('z-index', -1);
    };
</script>