在C100智能能眼镜端快速开启摄像头，拍照并保存
人脸检测以及特征点提取

在眼镜端如下路径放入人脸检测原型人连图片，以.jpg命名。
/sdcard/FaceDetection/baseface/

该程序目前只能适配于C100智能眼镜，如果需要适配其它机型，
可以修改GlassCameraHelper.java文件中的
mParams.setPreviewSize(1280, 720); // 设置预览大小
mParams.setPictureSize(1344, 756);
修改成机型支持的分辨率

