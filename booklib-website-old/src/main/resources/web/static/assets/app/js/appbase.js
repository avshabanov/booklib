
$(document).ready(function () {
  console.log("ready");
});

$(".j-fav-link").click(function (event) {
  event.preventDefault(); // do not actually navigate

  var self = $(this);
  var id = self.attr("item-id");
  var type = self.attr("item-type");

  console.log("id = " + id + ", type = " + type)

  var promise = $.ajax({
    url: "/g/author/rest/favorite/toggle?entityId=" + id + "&kind=" + type,
    method: "POST"
  })

  promise.done(function (data) {
    var isFavorite = data
    if (typeof(isFavorite) != "boolean") {
      console.warn("Unexpected fav status: " + isFavorite);
      return;
    }

    // we can use toggleClass here, but add/removeClass is safer
    if (isFavorite) {
      self.addClass("fav");
    } else {
      self.removeClass("fav");
    }
  })
});
