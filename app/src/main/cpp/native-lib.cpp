#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_theoryvision_theory6_controls_com_theoryvision_CameraView_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
