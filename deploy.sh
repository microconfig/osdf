BUILD_FOLDER="deploy"
NEXUS_URL="http://172.30.162.1/nexus/content/repositories/releases/ru/sber/lm/openshift"
SCRIPT_VERSION="1.0.0"
ENV="dev"
GIT_URL="ssh://git@10.21.25.60:8878/~18319756/os-configs.git"
CONFIG_VERSION="master"
OPENSHIFT_USERNAME="---"
OPENSHIFT_PASSWORD="---"

mkdir $BUILD_FOLDER

download () {
  echo "Downloading jar script from nexus"
  curl "${NEXUS_URL}/${SCRIPT_VERSION}/openshift-${SCRIPT_VERSION}.jar" --output "${BUILD_FOLDER}/openshift.jar"
}

init () {
  echo "Fetching configs and building them with microconfig"
  java -jar "${BUILD_FOLDER}/openshift.jar" init -e $ENV -g $GIT_URL -v $CONFIG_VERSION -f
}

deploy () {
  echo "Deploying configs to openshift"
  java -jar "${BUILD_FOLDER}/openshift.jar" deploy -u $OPENSHIFT_USERNAME -p $OPENSHIFT_PASSWORD
}

download
init
deploy

rm -rf $BUILD_FOLDER