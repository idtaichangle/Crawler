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



mvn install:install-file -Dfile=E:\Develop\jxbrowser-6.21-cross-desktop-win_mac_linux\lib\jxbrowser-6.21.jar -DgroupId=com.teamdev -DartifactId=jxbrowser -Dversion=6.21  -Dpackaging=jar -DlocalRepositoryPath=C:\Users\lixy\workspace\Crawler\local-maven-repo
mvn install:install-file -Dfile=E:\Develop\jxbrowser-6.21-cross-desktop-win_mac_linux\lib\jxbrowser-linux64-6.21.jar -DgroupId=com.teamdev -DartifactId=jxbrowser-linux64 -Dversion=6.21  -Dpackaging=jar -DlocalRepositoryPath=C:\Users\lixy\workspace\Crawler\local-maven-repo
mvn install:install-file -Dfile=E:\Develop\jxbrowser-6.21-cross-desktop-win_mac_linux\lib\jxbrowser-win64-6.21.jar -DgroupId=com.teamdev -DartifactId=jxbrowser-win64 -Dversion=6.21  -Dpackaging=jar -DlocalRepositoryPath=C:\Users\lixy\workspace\Crawler\local-maven-repo