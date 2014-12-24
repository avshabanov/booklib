<#macro page title>
<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">

    <meta name="Author" content="Alex" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>BookLib &raquo; ${title?html}</title>

    <#-- TODO: conditional inclusion: DEV, MINIFIED, LIVE -->
    <link rel="stylesheet" type="text/css" href="/assets/bootstrap-3.3.1/css/bootstrap.css" />
    <link rel="stylesheet" type="text/css" href="/assets/bootstrap-3.3.1/css/bootstrap-theme.css" />
    <link rel="stylesheet" type="text/css" href="/assets/app/css/global.css" />
  </head>
  <body>

  <#-- Navigation -->
  <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
      <#-- Brand and toggle get grouped for better mobile display -->
      <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#g-app-navbar-collapse">
          <span class="sr-only">Toggle navigation</span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="/g/index">BookLib</a>
      </div>
      <#-- Collect the nav links, forms, and other content for toggling -->
      <div class="collapse navbar-collapse" id="g-app-navbar-collapse">
        <ul class="nav navbar-nav">
          <li><a href="/g/authors">Authors</a></li>
          <li><a href="/g/genres">Genres</a></li>
          <li class="nav-divider"></li>
          <li><a href="/g/about">About</a></li>
        </ul>
      </div>
      <#-- /.navbar-collapse -->
    </div>
    <#-- /.container -->
  </nav>

  <div id="main-content" class="container">
    <#nested/>
  </div>

  <div id="footer">
  </div>

  <#-- TODO: conditional inclusion: DEV, MINIFIED, LIVE -->
  <script type="text/javascript" src="/assets/app/js/ns.js"></script><#-- < Sets up global namespace -->
  <#-- Vendor Scripts -->
  <script type="text/javascript" src="/assets/jquery-2.1.1/jquery.js"></script>
  <script type="text/javascript" src="/assets/bootstrap-3.3.1/js/bootstrap.js"></script>
  <#-- Custom Page Scripts -->
  <script type="text/javascript" src="/assets/app/js/appbase.js"></script>
</body>
</html>

</#macro>
