#!/usr/bin/env bash
./gradlew :plugin:clean :plugin:fatJar

rm compiled/plugin.jar
cp plugin/build/libs/plugin.jar compiled/plugin.jar

echo "" > settings.gradle
cat <<EOF > settings.gradle
include ':example'
include ':plugin'
include ':example:app_core'
include ':example:core_network_api'
include ':example:core_network_impl'
EOF

rm -r example/feature_create_payment_api
rm -r example/feature_create_payment_impl

./gradlew :example:generate <<< feature_create_payment