booklib
============

Servlet/SpringMVC demo web application written mostly in Kotlin

* Uses H2 as DB and AWS S3 for file hosting.
* Uses Freemarker as presentational engine.
* Uses "lean" embedded Jetty (so that certain modules are excluded, for example there is no module for JSP support as this project doesn't make any use of JSPs).

## How to install

Create target application directory

```
sudo mkdir /usr/local/booklib
```

Copy server.sh and booklib-website.jar to /usr/local/booklib/server.sh

```
sudo cp $DIST/scripts/server.sh /usr/local/booklib/server.sh
sudo cp $DIST/target/booklib-website.jar /usr/local/booklib/app.jar
```

(Developer mode only) Alternatively set up a link to the current ``server.sh``

```
sudo ln -s $DIST/server.sh /usr/local/booklib/server.sh
```
