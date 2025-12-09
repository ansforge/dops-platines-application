#
# (c) Copyright 2017-2024, ANS. All rights reserved.
#

ARTIFACTORY_BASE_URL=http://st-forge.asipsante.fr:8081
if [ ! -f node-v8.12.0-linux-x64.tar.gz ]; then
        wget $ARTIFACTORY_BASE_URL/artifactory/nodejs/v8.12.0/node-v8.12.0-linux-x64.tar.gz
        tar xvzf node-v8.12.0-linux-x64.tar.gz
fi

if [ ! -f linux-x64-48_binding.node ]; then
        wget $ARTIFACTORY_BASE_URL/artifactory/repo-dev/com/sass-lang/node-sass/4.9.4/linux-x64-57_binding.node
fi


BASE_PATH=$PWD
export PATH=$PATH:$BASE_PATH/node-v8.12.0-linux-x64/bin
export SASS_BINARY_PATH=$BASE_PATH/linux-x64-57_binding.node
ln -s $BASE_PATH/node-v8.12.0-linux-x64/bin/node $BASE_PATH/node-v8.12.0-linux-x64/bin/nodejs

echo "PATH=$PATH"

npm config set registry $ARTIFACTORY_BASE_URL/artifactory/api/npm/npm/

cd $BASE_PATH

npm install -g npm
npm install
if [ $? -eq 0 ]
then
  echo "npm install successfully executed"
else
  echo "npm install failed" >&2
  exit 1
fi

node $BASE_PATH/node_modules/@angular/cli/bin/ng build
if [ $? -eq 0 ]
then
  echo "node build successfully executed"
else
  echo "node build failed" >&2
  exit 1
fi