#https://wiki-ebabel.herokuapp.com/index.php?title=Vagrant-nodejs-angularjs-tutorial

 #! /bin/bash
 if [ ! -f /home/vagrant/already-installed-flag ]
 then
   echo "*********UBUNTU UPDATE*********"
   sudo apt-get update
   echo "*********BUILD ESSENTIAL*********"
   sudo apt-get -qq -y install build-essential libssl-dev
   echo "*********INSTALL NVM*********"
   curl -sL https://raw.githubusercontent.com/creationix/nvm/v0.33.11/install.sh | bash
   source ~/.profile
   echo "*********INSTALL NODEJS && NPM*********"
   nvm ls-remote
   nvm install 8.11.2
   echo "*********INSTALL GIT*********"
   apt-get -qq -y install git
   echo "*********INSTALL TREE*********"
   apt-get -qq -y install tree
   echo "*********INSTALL UNZIP*********"
   apt-get -qq -y install unzip

   touch /home/vagrant/already-installed-flag
   echo "*********Done!*********"
 else
   echo "already installed flag set : /home/vagrant/already-installed-flag"
 fi