jQuery.noConflict();
jQuery( document ).ready(function() {

    // show all-rules action
    jQuery("#ruletoggle").click( function(p_event) {
        jQuery(".elements").fadeToggle();
    });

    // click action on each grammar rule set
    jQuery( ".grammarlisthead" ).click( function(p_event) {
        jQuery("#"+jQuery(this).attr("data-ruleset") ).fadeToggle();
    });

});