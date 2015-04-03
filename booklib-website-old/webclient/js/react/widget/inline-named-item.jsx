var InlineNamedItem = React.createClass({
  render: function () {
    return (
      <span key={this.props.item.id} className="item-sep">
        <span className="prepended-divider">,&nbsp;</span>
        <a href={this.props.itemNavUrl} title={this.props.item.name}>{this.props.item.name}</a>
      </span>
    );
  }
});

//
// Helper functions for existing named items
//

function createAuthorInlineItem(author) {
  return (<InlineNamedItem key={author.id} itemNavUrl={"#/author/" + author.id} item={author} />);
}

function createGenreInlineItem(genre) {
  return (<InlineNamedItem key={genre.id} itemNavUrl={"#/genre/" + genre.id} item={genre} />);
}

