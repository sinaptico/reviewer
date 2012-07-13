== Welcome to reviewer

== Installation

Dependencies:

* Java 1.6 
* MySql
* Tomcat
* Maven 2

To install Maven 2:

    $ sudo apt-get install maven2
    $ cd /home/{user}/.mv2
    $ mkdir repository
    $ cd repository    
      copy all the third parties libs here.
    
To install:

    $ git clone git@github.com:sinaptico/reviewer.git
    $ cd /home/{user}
    $ touch reviewer.properties
    $ vi reviewer.properties

Now, let's create an `.rvmrc` file to store our rvm configuration and re-use it:

    $ echo "rvm 1.9.2@waysact" >> .rvmrc ; cd ../ ; cd waysact ; rvm gemset create waysact

And then install the rest of the gems we need:

    $ gem install bundler
    $ bundle install
    $ rake db:create:all
    $ rake db:migrate
    $ rails s

And you should be able to browse to [http://localhost:3000/](http://localhost:3000/) and you should see the application.

Or you should be using [pow.cx](http://pow.cx) and link it

Note that we have created some [detailed instructions](https://github.com/waysact/waysact/wiki/Development-environment) on how to setup your development environment.

