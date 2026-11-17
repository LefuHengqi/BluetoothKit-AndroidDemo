发布新版本，在宿主工程执行下面这条命令，然后在 PPBaseKit-Android 里提交、推送：
./gradlew :ppsdkkit:ppbasekit:publishReleasePublicationToMavenRepository :ppsdkkit:ppcalculatekit:publishReleasePublicationToMavenRepository :ppsdkkit:ppbluetoothkit:publishReleasePublicationToMavenRepository
-PPUBLISH_REPO_DIR=D:/project/PPBaseKit-Android
PUBLISH_REPO_DIR 在 Windows 上要写成 D:/...，不能写 /d/...。