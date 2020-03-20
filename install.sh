#!/usr/bin/env bash
# Install dependencies
dnf install -y ImageMagick libpng12 libXrender-devel libXrandr-devel libXfixes-devel libXinerama-devel fontconfig-devel freetype-devel libXi-devel libXt-devel libXext-devel libX11-devel libSM-devel libICE-devel glibc-devel libXtst-devel tinyxml gcc gcc-c++ maven

# Gia2d Dependency: png15, tiff
# https://centos.pkgs.org/8/centos-appstream-x86_64/libpng12-1.2.57-5.el8.x86_64.rpm.html
git clone https://github.com/glennrp/libpng.git --branch libpng15 --single-branch && cd libpng && ./configure --exec-prefix=/usr --libdir=/lib64 && make && make check && make install && cd .. && rm -rvf libpng/

# libtiff3 (http://www.libtiff.org/)
wget http://download.osgeo.org/libtiff/tiff-3.9.7.tar.gz && tar -zvxf tiff-3.9.7.tar.gz && cd tiff-3.9.7/ && ./configure --exec-prefix=/usr --libdir=/lib64 && make && make check && make install && cd .. && rm -rvf tiff-3.9.7.tar.gz tiff-3.9.7/

# Qt4
# https://github.com/qt/qt.git

# Qt4 Dependencies
# https://doc.qt.io/archives/qt-4.8/requirements-x11.html
# NOTE(tparker): There is not an entry for libglib-2.0 or libpthread
# As far as I can tell the glibc-devel provides the necessary libraries for libglib-2.0
# And libpthread appears to already be installed by default for CentOS 8
# I added (libXtst-devel) because of https://www.programering.com/a/MjM3kjNwATA.html
# Currently, there is a known bug with a cast in the itemview.cpp file, so we have to replace the type with sed
wget http://download.qt.io/archive/qt/4.8/4.8.7/qt-everywhere-opensource-src-4.8.7.tar.gz && tar -zxvf qt-everywhere-opensource-src-4.8.7.tar.gz && cd qt-everywhere-opensource-src-4.8.7/ && echo 'yes' | ./configure -prefix /opt/Qt-4.8.7  -opensource -shared -no-pch -no-javascript-jit -no-script -nomake demos -nomake examples && sed -i 's|view()->selectionModel()->select(index, QItemSelectionModel::Columns \& QItemSelectionModel::Deselect);|view()->selectionModel()->select(index, static_cast<QItemSelectionModel::SelectionFlags>(QItemSelectionModel::Columns \& QItemSelectionModel::Deselect));|g' ./src/plugins/accessible/widgets/itemviews.cpp && gmake -j4 && gmake install && ln -s /opt/Qt-4.8.7/lib/libQtCore.so.4 /usr/lib64/libQtCore.so.4 && cd .. && rm -rvf qt-everywhere-opensource-src-4.8.7.tar.gz qt-everywhere-opensource-src-4.8.7/

# Create users and group
groupadd rootarch
adduser rsa-data
usermod -aG rootarch rsa-data
usermod -g rootarch rsa-data

# Download application
git clone --depth 1 https://github.com/Topp-Roots-Lab/rsa-gui.git --branch master --single-branch /opt/rsa-gia
# Initialize file structure
mkdir -pv /etc/opt/rsa-gia /var/log/rsa-gia

# Add libraries and Java to appropriate environment variable to /etc/profile.d
echo 'export PATH="$PATH:/opt/java/java_default/bin:/opt/rsa-gia/bin"' > /etc/profile.d/rsagia.sh
JDK_HOME="$(readlink -f $(which java) | grep -oP '(.*jdk[\w\d\.\-]+)')"
echo "export JAVA_HOME=${JDK_HOME}" >> /etc/profile.d/rsagia.sh
echo 'export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:/opt/rsa-gia/bin/gia/lib/"' >> /etc/profile.d/rsagia.sh
source /etc/profile.d/rsagia.sh

# Install file manager tools
git clone --depth 1 https://github.com/Topp-Roots-Lab/rsa-tools.git --branch master --single-branch
pip2 install -r rsa-tools/requirements.txt
mkdir -pv /opt/rsa-gia/bin/importer /opt/rsa-gia/bin/file-handlers /opt/rsa-gia/bin/gia-programs/quality-control/qc
cp -Rv rsa-tools/Importer/* /opt/rsa-gia/bin/importer/
cp -Rv rsa-tools/FileHandlers/* /opt/rsa-gia/bin/file-handlers/
cp -Rv rsa-tools/QualityControl/* /opt/rsa-gia/bin/gia-programs/quality-control/qc/
cp -Rv rsa-tools/Analysis/* /opt/rsa-gia/bin

# Compile permission elevation scripts and set setuid
# Import tool
g++ /opt/rsa-gia/bin/importer/rsa-mv2orig-launcher.cpp -o /opt/rsa-gia/bin/importer/rsa-mv2orig-launcher
chown -v rsa-data:rootarch /opt/rsa-gia/bin/importer/rsa-mv2orig-launcher
chmod -v 4750 /opt/rsa-gia/bin/importer/rsa-mv2orig-launcher
chmod -v +x /opt/rsa-gia/bin/importer/rsa-mv2orig.py

# Moving & renaming tool
g++ /opt/rsa-gia/bin/file-handlers/rsa-renameorig-launcher.cpp -o /opt/rsa-gia/bin/file-handlers/rsa-renameorig-launcher
chown -v rsa-data:rootarch /opt/rsa-gia/bin/file-handlers/rsa-renameorig-launcher
chmod -v 4750 /opt/rsa-gia/bin/file-handlers/rsa-renameorig-launcher
chmod -v +x /opt/rsa-gia/bin/file-handlers/rsa-renameorig.py

# Quality control tool
chown -v rsa-data:rootarch /opt/rsa-gia/bin/gia-programs/quality-control/qc/all_qc_folder.py
chmod -v +x /opt/rsa-gia/bin/gia-programs/quality-control/qc/all_qc_folder.py

rm -rvf rsa-tools

# Initialize data file structure
# If the file structure has already been initialized by a previous
# installation, just create a symlink to its existing path as '/data',
# and then skip to the system menu creation step.
rsa-create-orig
yes | rsa-setrights-orig
yes | rsa-setrights-proc
chown -Rc rsa-data:rootarch /data
chmod -Rc a-x+X,u-x+rwX,g-swx+rwX,o-wx+rX /data
src_tmplt='/opt/rsa-gia/bin/rsa-gia-templates/*'
dest_tmplt='/data/rsa/rsa-gia-templates'
chown -v rsa-data:rootarch "$dest_tmplt"
chmod -v 2750 "$dest_tmplt"
cp -Rv $src_tmplt $dest_tmplt
# Set permissions for directories
find $dest_tmplt -mindepth 1 -type d -exec chown -v rsa-data:rootarch '{}' \;
find $dest_tmplt -mindepth 1 -type d -exec chmod -v 2750 '{}' \;
# Set permissions for files
find $dest_tmplt -mindepth 1 -type f -exec chown -v rsa-data:rootarch '{}' \;
find $dest_tmplt -mindepth 1 -type f -exec chmod -v 640 '{}' \;

# Create system menu entry for application
find /opt/rsa-gia/ -type f -iname "rsa-gia.desktop" -exec cp -v {} /usr/share/applications/ \;
find /opt/rsa-gia/ -type f -iname "rsa-gia.png" -exec cp -v {} /usr/share/pixmaps/ \;

# Add configuration file
find /opt/rsa-gia/ -type f -iname "default.*properties" -exec cp -v {} /etc/opt/rsa-gia \;
mv -vf /etc/opt/rsa-gia/default.qa.properties /etc/opt/rsa-gia/default.properties

# Initialize gia.log for Gia2d
touch /var/log/rsa-gia/gia.log
chown -Rc :rootarch /var/log/rsa-gia
chmod -Rc 2775 /var/log/rsa-gia
ln -sv /var/log/rsa-gia/gia.log /opt/rsa-gia/bin/gia/gia.log

# Compile GUI jar
pushd /opt/rsa-gia
mvn package
find /opt/rsa-gia/target -type f -iname "rsa*.jar" | while read f; do mv -vf "$f" "$(echo "/opt/rsa-gia/bin/gia-java/$(basename "$f" | sed 's/gui/gia/')")"; done
popd
rm -rvf /opt/rsa-gia/target

echo -e "Installation complete!\nMake sure to update '/etc/opt/rsa-gia/default.properties.' to point to your database server.\nYou will need to add any users to the 'rootarch' group before use.\nReboot required."
