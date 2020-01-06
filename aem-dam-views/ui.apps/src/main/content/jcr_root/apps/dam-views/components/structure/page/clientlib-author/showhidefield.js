(function (document, $, Coral) {

    $(document).on('foundation-contentloaded', function(e) {
        $(".aem-dialog-dropdown-showhide").each( function() {
            showHide($(this));
        });
    });

    $(document).on("change", ".aem-dialog-dropdown-showhide", function(e) {
        showHide($(this));
    });

    function showHide(el) {
        var isInMultifield = el.data('inmultifield');
        var parent = el.closest('coral-multifield-item-content');
        var target = el.data('aem-dialog-dropdown-showhide-target');
        var value = el.find('coral-select-item[selected]').val();
        if (isInMultifield) {
            $(parent).find(target).not(".hide").addClass("hide");
            $(parent).find(target + "[data-aem-showhidetargetvalue='" + value + "']").removeClass("hide");
        } else {
            $(target).not(".hide").addClass("hide");
            $(target).filter("[data-aem-showhidetargetvalue='" + value + "']").removeClass("hide");
        }
    }

})(document, Granite.$, Coral);