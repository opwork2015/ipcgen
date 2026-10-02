# ipcgen Installation Guide for AIDE

## متطلبات المشروع

### الحد الأدنى
- Android IDE (AIDE)
- Android SDK 23+ (Android 6.0 Marshmallow)
- Java Development Kit

### الاختياري (للتشغيل الكامل)
- ملف QEMU binary (`qemu-system-x86_64`)
- ملف صورة ISO أو IMG

## خطوات التثبيت في AIDE

### 1. نسخ المشروع
1. افتح AIDE
2. قم بإنشاء مشروع جديد: **New Project**
3. اختر **From Repository**
4. أدخل رابط المستودع:
   ```
   https://github.com/opwork2015/ipcgen.git
   ```

### 2. الخصائص الأساسية
- **Application Name**: ipcgen
- **Package Name**: com.iBlast.ipcgen
- **Version**: 1.0
- **Min SDK**: Android 6 (API 23)
- **Target SDK**: Android 9 (API 29)

### 3. البناء والتطوير

#### في AIDE:
1. اضغط على **Build** → **Compile Project**
2. تأكد من عدم وجود أخطاء في قسم **Error**
3. اضغط **Build** → **Build APK**
4. سيتم حفظ APK في مجلد المشروع

#### التشغيل:
1. اضغط على **Run**
2. اختر الجهاز أو المحاكي
3. التطبيق سيُثبت وينطلق تلقائياً

## الميزات المدعومة

✅ **واجهة المستخدم**
- اختيار ملفات ISO/IMG من التخزين
- عرض مسار الملف المختار
- عرض حالة التطبيق

✅ **الخدمات**
- تشغيل QEMU في الخلفية
- إيقاف الخدمة من الواجهة
- إشعار دائم (Foreground Notification)

✅ **الصلاحيات**
- الإنترنت
- الوصول للتخزين الخارجي
- الصوت والميكروفون
- الاستيقاظ من وضع النوم

✅ **التوافقية**
- Android 6.0+ (Marshmallow)
- دعم كامل API 23-29

## ملاحظات مهمة

### QEMU Binary
التطبيق يبحث عن ملف QEMU في المواقع التالية:
```
/data/local/tmp/qemu-system-x86_64
/system/bin/qemu-system-x86_64
/system/xbin/qemu-system-x86_64
/data/data/com.iBlast.ipcgen/files/qemu-system-x86_64
```

إذا لم تجد ملف QEMU:
1. قم بتجميع QEMU للـ Android
2. ضعه في أحد المواقع أعلاه
3. تأكد من أنه قابل للتنفيذ: `chmod +x qemu-system-x86_64`

### صور الأنظمة (ISO/IMG)
أي صورة ISO أو IMG قياسية ستعمل، مثل:
- Ubuntu Live ISO
- Linux Mint
- Debian
- Windows ISO (إذا كان لديك موارد كافية)

## استكشاف الأخطاء

### الخطأ: "QEMU binary not found"
**الحل**: تأكد من وجود ملف QEMU في أحد المواقع المذكورة أعلاه.

### الخطأ: "Image file not found"
**الحل**: تأكد من أن الملف موجود والمسار صحيح.

### الخطأ: "Permission denied"
**الحل**: تحقق من الصلاحيات في AndroidManifest.xml

### التطبيق لا يتشغل
**الحل**: 
1. افتح **Logcat** في AIDE
2. ابحث عن أخطاء بكلمة "ipcgen"
3. تحقق من رسائل الخطأ

## تطوير إضافي

### إضافة دعم VNC
إذا أردت واجهة رسومية كاملة للـ QEMU:
```bash
qemu-system-x86_64 -vnc :0 ...
```

### تحسين الأداء
عدّل معاملات QEMU في `QemuService.java`:
- زيادة الذاكرة: `-m 2048`
- زيادة المعالجات: `-smp 4`
- تمكين التسريع: `-enable-kvm`

## الدعم والمساعدة

إذا واجهت مشكلة:
1. تحقق من الـ Logcat
2. تأكد من جميع الملفات موجودة
3. أعد بناء المشروع
4. جرب تثبيت APK يدويًا

---

**النسخة**: 1.0  
**الحزمة**: com.iBlast.ipcgen  
**آخر تحديث**: 2026-10-02