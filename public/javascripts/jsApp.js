/**
 * Created by Kayrnt on 16/02/2014.
 */
$(function () {
    $(".flip").hover(
        function() {
            $(this).find(".card").addClass("flipped");
        }, function() {
            $(this).find(".card").removeClass("flipped");
        }
    );
})

