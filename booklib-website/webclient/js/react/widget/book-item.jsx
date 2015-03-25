var BookItem = React.createClass({
  render: function() {
    var inlineItem = function (itemNavUrl, item) {
      return (
        <span key={item.id} className="item-sep">
          <span className="prepended-divider">,&nbsp;</span>
          <a href={itemNavUrl} title={item.name}>{item.name}</a>
        </span>
      );
    }

    var createAuthor = function (author) {
      return inlineItem("#author/" + author.id, author);
    };

    var createGenre = function (genre) {
      return inlineItem("#genre/" + genre.id, genre);
    };

    var favData = {id: 1000, type: "BOOK", isFavorite: false};
    var bookUrl = '#/book/' + this.props.data.id;

    return (
      <li>
        <div className="container">
          <div className="row">
            <div className="col-md-12">
              <h3><small>{this.props.data.id}</small>&nbsp;<a href={bookUrl} title={this.props.data.title}>{this.props.data.title}</a></h3>
            </div>
          </div>
          <div className="row">
            <div className="col-md-2"><FavStar data={favData}/></div>
            <div className="col-md-5">{this.props.data.authors.map(createAuthor)}</div>
            <div className="col-md-5">{this.props.data.genres.map(createGenre)}</div>
          </div>
        </div>
      </li>
    );
  }
});
