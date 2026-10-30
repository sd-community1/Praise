# تسبيح — Tasbeeh

تطبيق أندرويد أصيل للتسبيح والذكر الرقمي. مبني بـ Kotlin + Jetpack Compose.

---

## مميزات التطبيق

- عدّاد رقمي كبير مع مؤشر دائري للتقدم
- 7 أذكار مختارة + خيار ذكر مخصص
- أهداف جاهزة: 33، 99، 100، 500 + هدف مخصص
- اهتزاز وصوت عند كل ضغطة
- زر تراجع عن آخر ضغطة
- إعادة تعيين مع تأكيد
- سجل الجلسات مع التاريخ والإحصائيات
- حفظ تلقائي — يحتفظ بالبيانات بعد إغلاق التطبيق
- وضع داكن (أخضر زمردي + ذهبي) ووضع فاتح
- يعمل بالكامل بدون إنترنت

---

## كيفية البناء عبر GitHub Actions

### الخطوات (للمبتدئين)

**1. أنشئ مستودعاً جديداً على GitHub**
- اذهب إلى github.com → New repository
- اسمه مثلاً: `tasbeeh-app`
- اجعله **Public** أو **Private** (كلاهما يعمل)
- **لا تضف** README أو .gitignore عند الإنشاء

**2. ارفع ملفات المشروع**

الطريقة الأسهل عبر GitHub مباشرة:
```
اسحب وأفلت مجلد المشروع كاملاً إلى صفحة المستودع
```

أو عبر Git:
```bash
cd tasbeeh
git init
git add .
git commit -m "Initial commit: Tasbeeh app"
git branch -M main
git remote add origin https://github.com/USERNAME/tasbeeh-app.git
git push -u origin main
```

**3. تفعيل GitHub Actions** (يتفعل تلقائياً عند رفع الملفات)

**4. تشغيل الـ Build**
- اذهب إلى تبويب **Actions** في مستودعك
- اختر **Android Build**
- اضغط **Run workflow** → **Run workflow**
- انتظر 5–10 دقائق

**5. تحميل الـ APK**
- بعد انتهاء البناء بنجاح (✅ خضراء)
- اضغط على اسم الـ workflow run
- انزل إلى قسم **Artifacts**
- اضغط على **Tasbeeh-Android-APK**
- فك ضغط الملف المحمّل
- ثبّت `app-debug.apk` على هاتفك

> **ملاحظة:** قبل التثبيت، فعّل "تثبيت من مصادر غير معروفة" في إعدادات هاتفك.

---

## مسار الـ APK المتوقع

```
app/build/outputs/apk/debug/app-debug.apk
```

اسم الـ Artifact: `Tasbeeh-Android-APK`  
مدة الاحتفاظ: 30 يوماً

---

## بنية المشروع

```
tasbeeh/
├── .github/
│   └── workflows/
│       └── android.yml          ← GitHub Actions
├── gradle/
│   ├── libs.versions.toml       ← إصدارات المكتبات
│   └── wrapper/
│       └── gradle-wrapper.properties
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── kotlin/com/tasbeeh/app/
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── Models.kt
│       │   │   │   ├── PreferencesManager.kt
│       │   │   │   └── TasbeehViewModel.kt
│       │   │   └── ui/
│       │   │       ├── screens/
│       │   │       │   ├── AppNavigation.kt
│       │   │       │   ├── CounterScreen.kt
│       │   │       │   ├── DhikrSelectScreen.kt
│       │   │       │   ├── HistoryScreen.kt
│       │   │       │   └── SettingsScreen.kt
│       │   │       └── theme/
│       │   │           ├── Theme.kt
│       │   │           └── Typography.kt
│       │   └── res/
│       │       ├── drawable/     ← أيقونة المشغل
│       │       ├── mipmap-*/     ← أيقونات بكثافات مختلفة
│       │       ├── raw/          ← click.wav, complete.wav
│       │       └── values/       ← strings.xml, themes.xml
│       ├── test/                 ← اختبارات الوحدة (18 اختبار)
│       └── androidTest/          ← اختبار التشغيل
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── .gitignore
```

---

## استكشاف الأخطاء

### كيف تقرأ خطأ في GitHub Actions؟
1. اضغط على الـ workflow run الفاشل (❌ حمراء)
2. اضغط على اسم الـ job الفاشل
3. افتح الخطوة التي فشلت (علامة ❌)
4. اقرأ أول سطر خطأ — ليس آخر سطر

---

### أخطاء شائعة وحلولها

| الخطأ | السبب | الحل |
|-------|-------|------|
| `Permission denied: ./gradlew` | صلاحيات gradlew | الـ workflow يحلها تلقائياً بـ `chmod +x` |
| `gradlew: No such file or directory` | ملف gradlew مفقود | تأكد أن ملف `gradlew` موجود في جذر المشروع |
| `Could not find or load main class GradleWrapperMain` | gradle-wrapper.jar مفقود | الـ workflow يستخدم `gradle/actions/setup-gradle` بدلاً منه |
| `Android SDK location not found` | SDK غير مثبت | الـ workflow يثبّته تلقائياً عبر `setup-java` |
| `Unsupported class file major version` | JDK خاطئ | الـ workflow يستخدم JDK 17 صراحةً |
| `Unresolved reference: ...` | مشكلة Kotlin | تحقق أن جميع ملفات `.kt` موجودة |
| `Plugin ... was not found` | مشكلة Gradle | تحقق من `libs.versions.toml` و `settings.gradle.kts` |
| `No files were found with the provided path` | APK لم يُنتج | انظر خطوة "Build debug APK" للخطأ الحقيقي |
| `Manifest merger failed` | تعارض في AndroidManifest | تحقق من صحة الملف |
| `SDK license not accepted` | رخص Android | الـ workflow يقبلها تلقائياً |

---

### إذا فشل البناء كلياً
1. اذهب إلى Actions → اضغط على الـ run الفاشل
2. افتح خطوة **Build debug APK**
3. انسخ أول رسالة خطأ
4. ابحث عنها في الجدول أعلاه

---

## المكدس التقني

| المكوّن | الإصدار |
|--------|---------|
| Kotlin | 1.9.22 |
| Android Gradle Plugin | 8.2.2 |
| Gradle | 8.2.1 |
| Compose BOM | 2024.02.00 |
| Min SDK | 23 (Android 6.0) |
| Target SDK | 34 (Android 14) |
| JDK | 17 |

---

## ملاحظات

- الـ APK المبني هو **debug APK** مخصص للتجربة والتثبيت المباشر
- لا يتطلب Google Play Services
- لا يتطلب إنترنت
- لا يجمع أي بيانات
