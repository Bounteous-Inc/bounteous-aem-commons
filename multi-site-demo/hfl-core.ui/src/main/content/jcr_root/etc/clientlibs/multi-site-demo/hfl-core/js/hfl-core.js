/**
 * Generic utility functions used by all HFL apps.
 */
var Util = (function() {
    var regexBlank = /[^\s]+/;

    return {
        isBlank: function (s) {
            return Util.isEmpty(s) || (typeof s === "string" && !regexBlank.test(s));
        },

        isEmpty: function (s) {
            return s == null || s === '';
        },

        isNotBlank: function (s) {
            return !Util.isBlank(s);
        },

        isNotEmpty: function (s) {
            return !Util.isEmpty(s);
        }
    }
})();