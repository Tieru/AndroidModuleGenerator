./gradlew :plugin:clean :plugin:jar

rm compiled/plugin.jar
cp plugin/build/libs/plugin.jar compiled/plugin.jar

echo "" > settings.gradle
cat <<EOF > settings.gradle
include ':example'
include ':plugin'
include ':example:core_network_api'
include ':example:core_network_impl'
EOF

rm -r example/feature_enter_code_api
rm -r example/feature_enter_code_impl

./gradlew :example:generate <<< feature_enter_code