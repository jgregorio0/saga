#https://wiki-ebabel.herokuapp.com/index.php?title=Vagrant-nodejs-angularjs-tutorial

 #! /bin/bash
 if [ ! -f /home/vagrant/already-installed-flag ]
 then
   echo "*********UBUNTU UPDATE*********"
   sudo apt-get update
   lsb_release -a
   echo "*********BUILD ESSENTIAL*********"
   sudo apt-get -qq -y install build-essential libssl-dev
   echo "*********INSTALL NVM*********"
   curl -sL https://raw.githubusercontent.com/creationix/nvm/v0.33.11/install.sh | bash
   source ~/.profile
   command -v nvm
   echo "*********INSTALL NODEJS && NPM*********"
   nvm ls-remote
   nvm install 8.11.2
   node -v
   npm -v
   echo "*********CONFIG NODE_MODULES*********"
   mkdir /home/vagrant/node_modules
   ln -s /home/vagrant/project/node_modules /home/vagrant/node_modules
   echo "*********INSTALL ANGULAR CLI*********"
   npm i -g @angular/cli@latest
   ng -v
   echo "*********INSTALL GIT*********"
   apt-get -qq -y install git
   git --version
   git config --global user.name "jgregorio"
   git config --global user.email gregoriojesus0@gmail.com
   echo "*********INSTALL TREE*********"
   apt-get -qq -y install tree
   tree --version
   echo "*********INSTALL UNZIP*********"
   apt-get -qq -y install unzip
   unzip -v
   touch /home/vagrant/already-installed-flag
   echo "*********Done!*********"
 else
   echo "already installed flag set : /home/vagrant/already-installed-flag"
 fi