# Virtual Machine Example Setup

This is an example run through of setting up Viper with CentOS 8 from scratch as a virtual machine (VirtualBox). This set up does not assume the virtual machine is a part of the Data Science cluster, so it does not use its authentication server or NFS mounts. This assumes you have already installed CentOS 8 on your VM. For guidance on how to set up the operating system, please follow the appropriate guide found at https://github.com/Topp-Roots-Lab/operations-scripts.

```bash
# ==== Users and groups ====
groupadd rootarch
groupadd ibaxter
adduser rsa-data
adduser ctopp
adduser njiang
usermod -aG rootarch root
usermod -aG rootarch rsa-data
usermod -aG rootarch tparker
usermod -aG rootarch ctopp
usermod -aG rootarch njiang
usermod -aG ibaxter tparker
usermod -g ibaxter tparker
usermod -aG ctopp tparker
usermod -aG ctopp njiang
usermod -g ctopp njiang

# ==== Desktop Environment and System-wide Dependencides ====
dnf groupinstall -y "Server with GUI"
systemctl set-default graphical.target
sytemctl isolate graphical.target
systemctl isolate graphical.target

# ==== Guest Additions ====
dnf update kernel*
mkdir -pv /media/VirtualBoxGuestAdditions
mount -r /dev/cdrom /media/VirtualBoxGuestAdditions/
dnf install -y https://dl.fedoraproject.org/pub/epel/epel-release-latest-8.noarch.rpm
yum install -y gcc kernel-devel kernel-headers dkms make bzip2 perl tigervnc-server tigervnc-server-module epel-release git wget gcc-c++ cmake python2 python36
KERN_DIR=/usr/src/kernels/`uname -r`
echo $KERN_DIR 
export KERN_DIR
cd /media/VirtualBoxGuestAdditions/
./VBoxLinuxAdditions.run 
reboot

# ==== Core dependencies ====
# Pip
curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
python3 get-pip.py 
# Java
rsync -avuP --stats tparker@stargate.datasci.danforthcenter.org:/shares/ctopp_share/data/repos/jdk/jdk-8u202-linux-x64.rpm .
rpm -ivh jdk-8u202-linux-x64.rpm
alternatives --set java /usr/java/jdk1.8.0_202-amd64/jre/bin/java

# Access tweaks (VNC)
git clone --depth 1 https://github.com/Topp-Roots-Lab/operations-scripts.git --branch master --single-branch
find operations-scripts/ -type f -iname "vnc.sh" -exec cp -v {} /etc/profile.d/ \;
firewall-cmd --permanent --add-port=5993/tcp
firewall-cmd --reload

# Qt5.9.8
wget http://download.qt.io/archive/qt/5.9/5.9.8/qt-opensource-linux-x64-5.9.8.run
chmod +x qt-opensource-linux-x64-5.9.8.run 
./qt-opensource-linux-x64-5.9.8.run

# Meshlab & Server tweaks
git clone --depth 1 https://github.com/cnr-isti-vclab/meshlab.git --branch master --single-branch
git clone --depth 1 https://github.com/cnr-isti-vclab/vcglib.git --branch devel --single-branch

QMAKE_FLAGS=('-spec' 'linux-g++' 'CONFIG+=release' 'CONFIG+=qml_release' 'CONFIG+=c++11' 'QMAKE_CXXFLAGS+=-fPIC' 'QMAKE_CXXFLAGS+=-std=c++11' 'QMAKE_CXXFLAGS+=-fpermissive' 'INCLUDEPATH+=/usr/include/eigen3' "LIBS+=-L`pwd`/lib/linux-g++")
MAKE_FLAGS=('-j4')
dnf install -y qt5-qtbase qt5-qtbase-devel qt5-qtscript qt5-qtscript-devel qt5-qtxmlpatterns qt5-qtxmlpatterns-devel mesa-libGLU mesa-libGLU-devel gmp gmp-devel
ln -sv /opt/Qt5.9.8/5.9.8/gcc_64/bin/qmake /usr/local/bin/qmake
pushd meshlab/src/external
qmake external.pro ${QMAKE_FLAGS[@]} && make ${MAKE_FLAGS[@]}
cd ../common
qmake common.pro ${QMAKE_FLAGS[@]} && make ${MAKE_FLAGS[@]}
cd ..
qmake meshlab_full.pro ${QMAKE_FLAGS[@]} && make ${MAKE_FLAGS[@]}
mkdir -pv /opt/meshlab
cp -Rv distrib/* /opt/meshlab
popd
ln -sv /opt/meshlab/meshlab /usr/local/bin/meshlab
ln -sv /opt/meshlab/meshlabserver /usr/local/bin/meshlabserver
ln -sv /opt/meshlab/libcommon.so /usr/lib64/libcommon.so
ln -sv /opt/meshlab/libcommon.so.1 /usr/lib64/libcommon.so.1
cp -Rv operations-scripts/servers/viper/meshlab/meshlab.desktop /usr/share/applications/
cp -Rv operations-scripts/servers/viper/meshlab/meshlab.png /usr/share/pixmaps/
echo 'model/vrml=meshlab.desktop' >> /usr/share/applications/mimeapps.list

# ==== RSA-GiA ====
# Install dependencies
dnf install -y ImageMagick libpng12 libXrender-devel libXrandr-devel libXfixes-devel libXinerama-devel fontconfig-devel freetype-devel libXi-devel libXt-devel libXext-devel libX11-devel libSM-devel libICE-devel glibc-devel libXtst-devel tinyxml
git clone https://github.com/glennrp/libpng.git --branch libpng15 --single-branch && cd libpng && ./configure --exec-prefix=/usr --libdir=/lib64 && make && make check && make install && cd ..
wget http://download.osgeo.org/libtiff/tiff-3.9.7.tar.gz && tar -zvxf tiff-3.9.7.tar.gz && cd tiff-3.9.7/ && ./configure --exec-prefix=/usr --libdir=/lib64 && make && make check && make install && cd ..
wget http://download.qt.io/archive/qt/4.8/4.8.7/qt-everywhere-opensource-src-4.8.7.tar.gz && tar -zxvf qt-everywhere-opensource-src-4.8.7.tar.gz && cd qt-everywhere-opensource-src-4.8.7/ && echo 'yes' | ./configure -prefix /opt/Qt-4.8.7  -opensource -shared -no-pch -no-javascript-jit -no-script -nomake demos -nomake examples && sed -i 's|view()->selectionModel()->select(index, QItemSelectionModel::Columns \& QItemSelectionModel::Deselect);|view()->selectionModel()->select(index, static_cast<QItemSelectionModel::SelectionFlags>(QItemSelectionModel::Columns \& QItemSelectionModel::Deselect));|g' ./src/plugins/accessible/widgets/itemviews.cpp && gmake -j4 && gmake install && ln -s /opt/Qt-4.8.7/lib/libQtCore.so.4 /usr/lib64/libQtCore.so.4 && cd ..
# Download application
git clone --depth 1 https://github.com/Topp-Roots-Lab/rsa-gui.git --branch maven-refactor --single-branch /opt/rsa-gia
git clone --depth 1 https://github.com/Topp-Roots-Lab/rsa-tools.git --branch master --single-branch
mkdir -pv /etc/opt/rsa-gia /var/log/rsa-gia
# Initialize gia.log for Gia2d
touch /var/log/rsa-gia/gia.log
ln -sv /var/log/rsa-gia/gia.log /opt/rsa-gia/bin/gia/gia.log
chown -Rc :rootarch /var/log/rsa-gia
chmod -Rc g+w /var/log/rsa-gia
echo 'export PATH="$PATH:/opt/java/java_default/bin:/opt/rsa-gia/bin"' > /etc/profile.d/rsagia.sh
echo 'export JAVA_HOME=/usr/java/jdk1.8.0_202-amd64/:$JAVA_HOME' >> /etc/profile.d/rsagia.sh
echo 'export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:/opt/rsa-gia/bin/gia/lib/"' >> /etc/profile.d/rsagia.sh
source /etc/profile.d/rsagia.sh 
pip2 install -r rsa-tools/requirements.txt
mkdir -pv /opt/rsa-gia/bin/importer /opt/rsa-gia/bin/file-handlers /opt/rsa-gia/bin/gia-programs/quality-control/qc
cp -Rv rsa-tools/Importer/* /opt/rsa-gia/bin/importer/
cp -Rv rsa-tools/FileHandlers/* /opt/rsa-gia/bin/file-handlers/
cp -Rv rsa-tools/QualityControl/* /opt/rsa-gia/bin/gia-programs/quality-control/qc/
# Compile permission elevation scripts and set setuid
g++ /opt/rsa-gia/bin/importer/rsa-mv2orig-launcher.cpp -o /opt/rsa-gia/bin/importer/rsa-mv2orig-launcher
chown -v rsa-data:rootarch /opt/rsa-gia/bin/importer/rsa-mv2orig-launcher
chmod -v 4750 /opt/rsa-gia/bin/importer/rsa-mv2orig-launcher
chmod -v +x /opt/rsa-gia/bin/importer/rsa-mv2orig.py
g++ /opt/rsa-gia/bin/file-handlers/rsa-renameorig-launcher.cpp -o /opt/rsa-gia/bin/file-handlers/rsa-renameorig-launcher
chown -v rsa-data:rootarch /opt/rsa-gia/bin/file-handlers/rsa-renameorig-launcher
chmod -v 4750 /opt/rsa-gia/bin/file-handlers/rsa-renameorig-launcher
chmod -v +x /opt/rsa-gia/bin/file-handlers/rsa-renameorig.py
chown -v rsa-data:rootarch /opt/rsa-gia/bin/gia-programs/quality-control/qc/all_qc_folder.py
chmod -v +x /opt/rsa-gia/bin/gia-programs/quality-control/qc/all_qc_folder.py
rsa-create-orig 
yes | rsa-setrights-orig
yes | rsa-setrights-proc
chown -Rc rsa-data:rootarch /data
chmod -Rv a-x+X,u-x+rwX,g-swx+rwX,o-wx+rX /data
src_tmplt='/opt/rsa-gia/bin/rsa-gia-templates/*'
dest_tmplt='/data/rsa/rsa-gia-templates'
chown -v rsa-data:rootarch "$dest_tmplt"
chmod -v 2750 "$dest_tmplt"
cp -Rv $src_tmplt $dest_tmplt
# directories
find $dest_tmplt -mindepth 1 -type d -exec chown -v rsa-data:rootarch '{}' \;
find $dest_tmplt -mindepth 1 -type d -exec chmod -v 2750 '{}' \;
# files
find $dest_tmplt -mindepth 1 -type f -exec chown -v rsa-data:rootarch '{}' \;
find $dest_tmplt -mindepth 1 -type f -exec chmod -v 640 '{}' \;
# rm -rvf /opt/rsa-gia/bin/rsa-gia-templates /opt/rsa-gia/bin/rsa-install-rsagiatemplates rsa-create-rsadata-rootarchrsa-mv2orig
find /opt/rsa-gia/ -type f -iname "rsa-gia.desktop" -exec cp -v {} /usr/share/applications/ \;
find /opt/rsa-gia/ -type f -iname "default.*properties" -exec cp -v {} /etc/opt/rsa-gia \;
find /opt/rsa-gia/ -type f -iname "rsa-gia.png" -exec cp -v {} /usr/share/pixmaps/ \;
yes | cp -Rvf /opt/rsa-gia/bin/rsa-gia-templates /data/rsa/

# Download data
mkdir -pv /data/rsa/to_sort/root /data/rsa/to_sort/tparker /data/rsa/to_sort/njiang /data/rsa/to_sort/ctopp
rsync -vrogP --chown=rsa-data:rootarch --chmod=D2775,F644 --stats  tparker@stargate.datasci.danforthcenter.org:/shares/ctopp_share/data/rsa/original_images/corn/TIM/p0019/t13/PNG/ /data/rsa/to_sort/root
chown -Rc rsa-data:rootarch /data/rsa/to_sort/
cd /data/rsa/to_sort/
find /data/rsa/to_sort/root -type f -iname "*.PNG" | while read f ; do  cp -pv "$f" "/data/rsa/to_sort/tparker/$(basename $f)"; done
```
