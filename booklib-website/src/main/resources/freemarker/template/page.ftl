<#macro page title>
<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="Author" content="Alex" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>BookLib &raquo; ${title?html}</title>

    <link rel="stylesheet" type="text/css" href="/static/bootstrap-3.3.1/css/bootstrap.css" />
    <link rel="stylesheet" type="text/css" href="/static/bootstrap-3.3.1/css/bootstrap-theme.css" />
    <link rel="stylesheet" type="text/css" href="/static/app/css/global.css" />
  </head>
  <body>

  <!-- Navigation -->
  <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
      <!-- Brand and toggle get grouped for better mobile display -->
      <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#g-app-navbar-collapse">
          <span class="sr-only">Toggle navigation</span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="/g/index">BookLib</a>
      </div>
      <!-- Collect the nav links, forms, and other content for toggling -->
      <div class="collapse navbar-collapse" id="g-app-navbar-collapse">
        <ul class="nav navbar-nav">
          <li><a href="#">Authors</a></li>
          <li><a href="#">Genres</a></li>
          <li class="nav-divider"></li>
          <li><a href="#">About</a></li>
        </ul>
      </div>
      <!-- /.navbar-collapse -->
    </div>
    <!-- /.container -->
  </nav>

  <div id="main-content" class="container">
    <#nested/>
  </div>

  <div id="footer">
  </div>

  <script type="text/javascript" src="/static/app/js/ns.js"></script>
  <script type="text/javascript" src="/static/jquery-2.1.1/jquery.js"></script>
  <script type="text/javascript" src="/static/bootstrap-3.3.1/js/bootstrap.js"></script>
  <script type="text/javascript" src="/static/app/js/appbase.js"></script>
  </body>
</html>

</#macro>
