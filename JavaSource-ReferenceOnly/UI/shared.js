/*global window, Shared, Backbone, _, Mailbox, Accounting, app, $ */
window.app = window.app || {};
app.views = app.views || {};
app.models = app.models || {};
app.collections = app.colleciton || {};
app.templates = app.templates || {};
app.page = app.page || {};

window.bootstrap_alert = window.bootstrap_alert || {
    success: function (message, delay) {
        "use strict";
        $('.alerts').html('<div class="alert alert-success fade in"><a class="close" data-dismiss="alert"><i class="icon-remove"></i></a><span>' + message + '</span></div>');
        this.fadeOut(delay || 10000);
    },

    important: function (message, delay) {
        "use strict";
        $('.alerts').html('<div class="alert alert-danger fade in"><a class="close" data-dismiss="alert"><i class="icon-remove"></i></a><span>' + message + '</span></div>');
        this.fadeOut(delay || 10000);
    },

    fadeOut: function (delay) {
        "use strict";
        window.setTimeout(function () {
            $(".alerts .alert").fadeOut("slow");
        }, delay);
    }
};

$(function () {
    "use strict";
    //$('.tip').tooltip();
    //function beauty() {
    //    $('.beauty .background').css('background-position-x', '0px');
    //    $('.beauty .background').animate({ 'background-position-x': '-100000px' }, 150000, 'linear', beauty);
    //}
    //beauty();

    //$('#spinner > div').sprite({ fps: 19, no_of_frames: 19 });


    window.Shared = {};

    Shared.Alerts = Shared.Alerts || {
        success: function (message, delay) {
            $('.header-alerts').html('<div class="alert alert-success fade in"><a class="close" data-dismiss="alert"><i class="icon-remove"></i></a><span>' + message + '</span></div>');

            this.fadeOut(delay || 3000);
        },
        error: function (message, delay) {
            $('.header-alerts').html('<div class="alert alert-error fade in"><a class="close" data-dismiss="alert"><i class="icon-remove"></i></a><span>' + message + '</span></div>');
            this.fadeOut(delay || 3000);
        },
        fadeOut: function (delay) {
            if (typeof delay === "number") {
                window.setTimeout(function () {

                    $(".header-alerts .alert").fadeOut("slow");
                }, delay);
            }
        }
    };

    Shared.Spinner = {
        cnt: 0,
        ajax: function (options, done, fail) {
            var that = this;
            this.show();
            $.ajax(options)
                .done(function (data) {
                    if (done) {
                        done(data);
                    }
                })
                .fail(function (data) {
                    if (fail) {
                        fail(data);
                    }
                })
                .always(function () {
                    that.hide();
                });
        },

        show: function () {
            if (this.cnt === 0) {
                $("#spinner").show();
            }
            this.cnt += 1;
        },

        hide: function () {
            this.cnt = (--this.cnt <= 0) ? 0 : this.cnt;
            if (this.cnt === 0) {
                $("#spinner").hide();
            }
        }
    };

  });
