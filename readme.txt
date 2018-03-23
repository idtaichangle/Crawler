如果使用webrenderer
#sudo apt-get install Xvfb firefox libxrender1 libxtst6 libxi6 libxt6 libxext6
sudo apt-get install Xvfb   libxrender1 libxtst6 libxi6  libxt6 libxext6 # firefox
cp libflashplayer.so ~/.mozilla/plugins


如果使用jxbrowser
linux:
sudo sh -c 'echo "deb http://archive.canonical.com/ubuntu/ $(lsb_release -sc) partner" >> /etc/apt/sources.list.d/canonical_partner.list'

sudo apt-get install xvfb chromium-browser adobe-flashplugin

#安装adobe-flashplugin后可以不复制libpepflashplayer.so.
#cp libpepflashplayer.so  /usr/lib/adobe-flashplugin/

windows:
https://www.adobe.com/support/flashplayer/debug_downloads.html
下载安装
Download the Flash Player content debugger for Opera and Chromium based applications – PPAPI