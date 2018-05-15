(function($) {
    $(document).ready(function() {
        $(".cmp-schedule").each(function () {
            var schedule = $(this);

            // Fetch the schedule data
            var url = schedule.find(".schedule-games").data("resource") + ".json";
            var success = function(data) {
                var gamesList = schedule.find(".schedule-games");
                $.each(data, function(idx, game) {
                    var gameHtml = '<div class="game" data-is-home="' + game.isHomeGame + '">';
                    gameHtml += '<span class="game-name">' + game.name + ':</span>';
                    gameHtml += new Date(game.date).toLocaleString();
                    gameHtml += " (";
                    gameHtml += gamesList.data(game.isHomeGame ? "label-home" : "label-away");
                    gameHtml += ")";
                    gameHtml += "</div>";

                    gamesList.append(gameHtml);
                });
            };
            var failure = function() {
                window.console && console.error("Failed to retrieve game schedule");
            };
            $.get(url, success, failure, "json");

            // Instrument the home/away filter radio buttons.
            schedule.find("input[name=filter]").change(function() {
                schedule.removeClass("filter-home")
                    .removeClass("filter-away")
                    .removeClass("filter-all")
                    .addClass("filter-" + $(this).val());
            });
        });
    });
})(jQuery);